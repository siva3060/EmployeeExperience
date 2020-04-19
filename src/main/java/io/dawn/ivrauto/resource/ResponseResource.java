package io.dawn.ivrauto.resource;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import io.dawn.ivrauto.model.Candidate;
import io.dawn.ivrauto.model.Question;
import io.dawn.ivrauto.model.Response;
import io.dawn.ivrauto.model.Screening;
import io.dawn.ivrauto.repository.CandidateRepository;
import io.dawn.ivrauto.repository.QuestionRepository;
import io.dawn.ivrauto.repository.ResponseRepository;
import io.dawn.ivrauto.service.CandidateService;
import io.dawn.ivrauto.service.QuestionService;
import io.dawn.ivrauto.service.ResponseService;
import io.dawn.ivrauto.util.ResponseParser;
import io.dawn.ivrauto.util.TwiMLUtil;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ResponseResource {

  private static final String FORMAT = "(?<=\\G.{" + 2 + "})";
  private QuestionRepository questionRepository;
  private ResponseRepository responseRepository;
  private CandidateRepository candidateRepository;
  private QuestionService questionService;
  private CandidateService candidateService;
  private ResponseService responseService;

  @Value("${twilio.number}")
  private String from;

  @Autowired
  public ResponseResource(
      QuestionRepository questionRepository,
      ResponseRepository responseRepository,
      CandidateRepository candidateRepository) {
    this.questionRepository = questionRepository;
    this.responseRepository = responseRepository;
    this.candidateRepository = candidateRepository;
  }

  /**
   * End point that saves a question response and redirects the call to the next question, if one is
   * available.
   */
  @RequestMapping(
      value = "/save_response",
      method = RequestMethod.POST,
      produces = "application/xml")
  public void save(HttpServletRequest request, HttpServletResponse response) throws Exception {
    PrintWriter responseWriter = response.getWriter();
    this.questionService = new QuestionService(questionRepository);
    this.responseService = new ResponseService(responseRepository);
    this.candidateService = new CandidateService(candidateRepository);

    Optional<Question> currentQuestion = getQuestionFromRequest(request);
    final long cid = Long.parseLong(request.getParameter("cid"));
    final Optional<Candidate> candidate = candidateService.findCandidateById(cid);

    if (currentQuestion.isPresent() && candidate.isPresent()) {
      Screening screening = currentQuestion.get().getScreening();
      String title = screening.getTitle();

      // saving the response given by the candidate
      final Response fromUser =
          new ResponseParser(currentQuestion.get(), candidate.get(), request).parse();

      if (fromUser != null) {
        persistResponse(fromUser);
      }

      // if it is the last question of the screening
      if (screening.isLastQuestion(currentQuestion.get())) {
        String message =
            "Thank you for taking the "
                + title
                + " screening. You will get an SMS notification with a link of the test which you will have 30 minutes"
                + " to take during the agreed time. Kindly setup your time and availability either in your mobile or"
                + " laptop during that period.";
        // for a message
        if (request.getParameter("MessageSid") != null) {
          responseWriter.print(TwiMLUtil.messagingResponse(message));
        } else {
          // for a voice call
          responseWriter.print(TwiMLUtil.voiceResponse(message));

          log.info("Generating exam date and time");
          final List<String> responseDateTime = responseRepository.generateExamDate(cid);

          if (responseDateTime.size() != 0) {
            StringBuilder sms = generateInterviewDateAndTime(responseDateTime);
            log.info("Interview date and time generated successfully...");

            MessageCreator creator =
                Message.creator(
                    new PhoneNumber(candidate.get().getMobileNumber()),
                    new PhoneNumber(from),
                    sms.toString());
            creator.create();
            log.info("SMS sent successfully to " + candidate.get().getMobileNumber());

            // updating candidate interview status
            candidateRepository.updateInterviewStatus(cid);
            log.info("Interview scheduled for the candidate.");
          }
        }
      } else if (fromUser == null) {
        // redirects to the same question if user inputs invalid options.
        responseWriter.print(
            TwiMLUtil.redirect(
                cid, Integer.parseInt(String.valueOf(currentQuestion.get().getId())), screening));
      } else {
        // user keys in valid input and if not last question, then proceed to the next question
        responseWriter.print(
            TwiMLUtil.redirect(
                cid, screening.getNextQuestionNumber(currentQuestion.get()), screening));
      }
    }
    response.setContentType("application/xml");
  }

  private StringBuilder generateInterviewDateAndTime(List<String> responseDateTime) {
    String[] splitDate = responseDateTime.get(0).split(FORMAT);
    String[] splitTime = responseDateTime.get(1).split(FORMAT);

    final int year = ResponseParser.getYear(Integer.parseInt(splitDate[0]));
    final String examDate = String.format("%s-%s-%d", splitDate[0], splitDate[1], year);
    final String examTime = String.format("%s:%s", splitTime[0], splitTime[1]);

    StringBuilder sms = new StringBuilder();
    sms.append("Your interview has been scheduled on ")
        .append(examDate)
        .append(" at ")
        .append(examTime)
        .append(" Hrs.")
        .append(" Use the following link to attend the screening which will be activated 5 minutes")
        .append(" before the scheduled time:\n")
        .append(" https://m.hcl.com/2342/ivrtest");
    return sms;
  }

  private void persistResponse(Response questionResponse) {
    Question currentQuestion = questionResponse.getQuestion();
    Response previousResponse =
        responseService.findProperResponse(
            questionResponse.getSessionSid(), currentQuestion.getId());
    if (previousResponse != null) {
      // it's already answered. That's an update from Twilio API.
      questionResponse.setId(previousResponse.getId());
    }
    /* creates the question response on the db */
    responseService.save(questionResponse);
  }

  private Optional<Question> getQuestionFromRequest(HttpServletRequest request) {
    return questionService.find(Long.parseLong(request.getParameter("qid")));
  }
}

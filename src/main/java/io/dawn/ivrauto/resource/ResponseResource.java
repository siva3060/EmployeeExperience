package io.dawn.ivrauto.resource;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import io.dawn.ivrauto.model.Question;
import io.dawn.ivrauto.model.Response;
import io.dawn.ivrauto.model.Screening;
import io.dawn.ivrauto.repository.QuestionRepository;
import io.dawn.ivrauto.repository.ResponseRepository;
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
  private QuestionRepository questionRepository;
  private ResponseRepository responseRepository;

  private QuestionService questionService;
  private ResponseService responseService;

  @Value("${twilio.number}")
  private String from;

  @Autowired
  public ResponseResource(
      QuestionRepository questionRepository, ResponseRepository responseRepository) {
    this.questionRepository = questionRepository;
    this.responseRepository = responseRepository;
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

    Optional<Question> currentQuestion = getQuestionFromRequest(request);
    if (currentQuestion.isPresent()) {
      Screening screening = currentQuestion.get().getScreening();
      persistResponse(new ResponseParser(currentQuestion.get(), request).parse());

      if (screening.isLastQuestion(currentQuestion.get())) {
        String message =
            "Thank you for taking the "
                + screening.getTitle()
                + " screening. You will be receiving an SMS "
                + "shortly. Thank you!";
        if (request.getParameter("MessageSid") != null) {
          responseWriter.print(TwiMLUtil.messagingResponse(message));
        } else {
          responseWriter.print(TwiMLUtil.voiceResponse(message));

          log.info("generating exam date and time...");
          final List<String> responseDateTime = responseRepository.getDate();

          String[] splitDate = responseDateTime.get(0).split("(?<=\\G.{" + 2 + "})");
          String[] splitTime = responseDateTime.get(1).split("(?<=\\G.{" + 2 + "})");

          final String examDate = splitDate[0] + "-" + splitDate[1] + "-2020";
          final String examTime = splitTime[0] + ":" + splitTime[1];

          String sms =
              "Your interview has been scheduled on "
                  + examDate
                  + " at "
                  + examTime
                  + " Hrs. "
                  + " Use the following link to attend the screening:\n"
                  + "https://m.hcl.com/2342/ivrtest which will be activated 5 minutes before the scheduled time";

          MessageCreator creator =
              Message.creator(new PhoneNumber("+919916463143"), new PhoneNumber(from), sms);
          creator.create();
          log.info("SMS sent successfully!");
        }
      } else {
        responseWriter.print(
            TwiMLUtil.redirect(screening.getNextQuestionNumber(currentQuestion.get()), screening));
      }
    }
    response.setContentType("application/xml");
  }

  private void persistResponse(Response questionResponse) {
    Question currentQuestion = questionResponse.getQuestion();
    Response previousResponse =
        responseService.getBySessionSidAndQuestion(
            questionResponse.getSessionSid(), currentQuestion);
    if (previousResponse != null) {
      // it's already answered. That's an update from Twilio API.
      questionResponse.setId(previousResponse.getId());
    }

    /** creates the question response on the db */
    responseService.save(questionResponse);
  }

  private Optional<Question> getQuestionFromRequest(HttpServletRequest request) {
    return questionService.find(Long.parseLong(request.getParameter("qid")));
  }
}

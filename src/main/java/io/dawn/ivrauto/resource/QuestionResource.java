package io.dawn.ivrauto.resource;

import io.dawn.ivrauto.model.Question;
import io.dawn.ivrauto.model.Screening;
import io.dawn.ivrauto.repository.ScreeningRepository;
import io.dawn.ivrauto.service.ScreeningService;
import io.dawn.ivrauto.util.QuestionBuilder;
import io.dawn.ivrauto.util.SMSQuestionBuilder;
import io.dawn.ivrauto.util.VoiceQuestionBuilder;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class QuestionResource {
  private ScreeningRepository screeningRepository;

  public QuestionResource() {}

  @Autowired
  public QuestionResource(ScreeningRepository screeningRepository) {
    this.screeningRepository = screeningRepository;
  }

  /**
   * End point that returns the appropriate question response based on the parameters it receives
   */
  @RequestMapping(value = "/question", method = RequestMethod.GET, produces = "application/xml")
  public void show(HttpServletRequest request, HttpServletResponse response) throws Exception {
    ScreeningService screeningService = new ScreeningService(screeningRepository);
    final long cid = Long.parseLong(request.getParameter("cid"));
    Optional<Screening> screening =
        screeningService.find(Long.parseLong(request.getParameter("screening")));

    if (screening.isPresent()) {
      Question currentQuestion =
          screening.get().getQuestionByNumber(Integer.parseInt(request.getParameter("question")));
      QuestionBuilder builder = getQuestionHandler(currentQuestion, request);

      if (currentQuestion != null) {
        response.getWriter().print(builder.build(cid));
      } else {
        response.getWriter().print(builder.buildNoMoreQuestions());
      }
    }
    response.setContentType("application/xml");
  }

  private void createSessionForQuestion(HttpServletRequest request, Question currentQuestion) {
    if (currentQuestion == null) {
      return;
    }
    HttpSession session = request.getSession(true);
    session.setAttribute("questionId", currentQuestion.getId());
  }

  private QuestionBuilder getQuestionHandler(Question currentQuestion, HttpServletRequest request) {
    if (isVoiceRequest(request)) {
      return new VoiceQuestionBuilder(currentQuestion);
    } else {
      createSessionForQuestion(request, currentQuestion);
      return new SMSQuestionBuilder(currentQuestion);
    }
  }

  private boolean isVoiceRequest(HttpServletRequest request) {
    return request.getParameter("MessageSid") == null;
  }
}

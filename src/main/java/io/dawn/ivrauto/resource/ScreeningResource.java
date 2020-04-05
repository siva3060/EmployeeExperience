package io.dawn.ivrauto.resource;

import io.dawn.ivrauto.model.Screening;
import io.dawn.ivrauto.repository.CandidateRepository;
import io.dawn.ivrauto.repository.ScreeningRepository;
import io.dawn.ivrauto.service.ScreeningService;
import io.dawn.ivrauto.util.TwiMLUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ScreeningResource {
  private ScreeningRepository screeningRepository;
  private ScreeningService screeningService;
  private CandidateRepository candidateRepository;

  @Value("${ngrok.domain}")
  private String ngrokDomain;

  public ScreeningResource() {}

  @Autowired
  public ScreeningResource(
      ScreeningRepository screeningRepository,
      ScreeningService screeningService,
      CandidateRepository candidateRepository) {
    this.screeningRepository = screeningRepository;
    this.screeningService = screeningService;
    this.candidateRepository = candidateRepository;
  }

  /**
   * Calls endpoint; Welcomes a user and redirects to the question controller if there is a
   * screening to be answered. Otherwise it plays a message and hangs up the call if there is no
   * screening available.
   */
  @RequestMapping(value = "/screening/call", method = RequestMethod.GET)
  public void call(HttpServletRequest request, HttpServletResponse response) throws Exception {
    this.screeningService = new ScreeningService(screeningRepository);
    log.info("Retrieving your screening questions...");

    Screening lastScreening = screeningService.findLast();

    if (lastScreening != null) {
      response.getWriter().print(getFirstQuestionRedirect(lastScreening, request));
    } else {
      response.getWriter().print(getHangupResponse(request));
    }
    response.setContentType("application/xml");
  }

  /**
   * SMS endpoint; Welcomes a user and redirects to the question controller if there is a screening
   * to be answered. An SMS is just a message instead of a long running call. We store state by
   * mapping a Twilio's Cookie to a Session.
   */
  @RequestMapping(value = "/screening/sms", method = RequestMethod.GET)
  public void sms(HttpServletRequest request, HttpServletResponse response) throws Exception {
    this.screeningService = new ScreeningService(screeningRepository);

    Screening lastScreening = screeningService.findLast();
    HttpSession session = request.getSession(false);

    if (lastScreening != null) {
      if (session == null || session.isNew()) {
        // New session,
        response.getWriter().print(getFirstQuestionRedirect(lastScreening, request));
      } else {
        // Ongoing session, redirect to ResponseController to save it's answer.
        response.getWriter().print(getSaveResponseRedirect(session));
      }
    } else {
      // No screening
      response.getWriter().print(getHangupResponse(request));
    }
    response.setContentType("application/xml");
  }

  private String getSaveResponseRedirect(HttpSession session) throws Exception {
    String saveURL = "/save_response?qid=" + getQuestionIdFromSession(session);
    return TwiMLUtil.redirectPost(saveURL);
  }

  /**
   * Creates the TwiMLResponse for the first question of the screening
   *
   * @param screening Screening entity
   * @param request HttpServletRequest request
   * @return TwiMLResponse
   */
  private String getFirstQuestionRedirect(Screening screening, HttpServletRequest request)
      throws Exception {
    String skill = candidateRepository.findSkillByCandidateEmail("nithin2889@gmail.com");
    String welcomeMessage =
        "This is an automated call from "
            + screening.getTitle()
            + " for an interesting opportunity in digital space where we building a transformation"
            + " program using modern digital technologies stack with Dev Ops using Agile based execution."
            + " We are looking for professionals across modern technology streams of backend (Java 8 with Spring Boot),"
            + " front end (with Google Polymer or React J S or Angular J S) having strong web components "
            + " fundamentals, "
            + " Dev Ops (building CI, CD and release pipelines), Building digital global data lakes using Apache "
            + " frameworks and IBM tool. We found that you have experience in "
            + skill
            + " and we want to fix up an appointment with you for a formal discussion and evaluation.";
    String questionURL = ngrokDomain + "/question?screening=" + screening.getId() + "&question=1";

    if (request.getParameter("MessageSid") != null) {
      return TwiMLUtil.messagingResponseWithRedirect(welcomeMessage, questionURL);
    } else {
      return TwiMLUtil.voiceResponseWithRedirect(welcomeMessage, questionURL);
    }
  }

  /**
   * Creates a TwiMLResponse if no screenings are found on the database. For SMS, it's just a
   * message, however, for Voice, it should also send a Hangup to the ongoing call.
   *
   * @return TwiMLResponse
   */
  private String getHangupResponse(HttpServletRequest request) throws Exception {
    String errorMessage = "We are sorry, there are no screenings available. Good bye.";
    cleanSession(request);
    if (request.getParameter("MessageSid") != null) {
      return TwiMLUtil.messagingResponse(errorMessage);
    } else {
      return TwiMLUtil.voiceResponse(errorMessage);
    }
  }

  private void cleanSession(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session != null) {
      session.invalidate();
    }
  }

  private Long getQuestionIdFromSession(HttpSession session) {
    return (Long) session.getAttribute("questionId");
  }
}

package io.dawn.ivrauto.util;

import io.dawn.ivrauto.model.Candidate;
import io.dawn.ivrauto.model.Question;
import io.dawn.ivrauto.model.Response;
import java.time.LocalDate;
import javax.servlet.http.HttpServletRequest;

/** Class returns the appropriate Response model when you call the parse() method */
public class ResponseParser {
  private Question question;
  private Candidate candidate;
  private HttpServletRequest request;

  public ResponseParser(Question question, Candidate candidate, HttpServletRequest request) {
    this.question = question;
    this.candidate = candidate;
    this.request = request;
  }

  public Response parse() {
    String contentKey;
    if (hasTextContent()) {
      contentKey = isValidTranscription() ? "RecordingUrl" : "TranscriptionText";
    } else {
      contentKey = "Digits";
    }
    return buildResponse(contentKey);
  }

  private boolean isValidTranscription() {
    return request.getParameter("TranscriptionText") == null;
  }

  private boolean hasTextContent() {
    return question.getType().equals("text");
  }

  private boolean isSMS() {
    return request.getParameter("MessageSid") != null;
  }

  private Response buildResponse(String contentKey) {
    String sessionKey = isSMS() ? "MessageSid" : "CallSid";
    if (isSMS()) {
      contentKey = "Body";
    }
    String content = request.getParameter(contentKey);
    String sessionSid = request.getParameter(sessionKey);
    return new Response(content, sessionSid, question, candidate, LocalDate.now());
  }
}

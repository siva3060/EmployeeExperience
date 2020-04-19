package io.dawn.ivrauto.util;

import io.dawn.ivrauto.model.Candidate;
import io.dawn.ivrauto.model.Question;
import io.dawn.ivrauto.model.Response;
import java.time.LocalDate;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/** Class returns the appropriate Response model when you call the parse() method */
@Slf4j
public class ResponseParser {
  private Question question;
  private Candidate candidate;
  private HttpServletRequest request;

  public ResponseParser(Question question, Candidate candidate, HttpServletRequest request) {
    this.question = question;
    this.candidate = candidate;
    this.request = request;
  }

  public static int getYear(int mm) {
    LocalDate today = LocalDate.now();
    return today.getMonth().getValue() < mm ? today.getYear() : today.getYear() + 1;
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

    // response from user for date and time
    String content = request.getParameter(contentKey);
    String REGEX_FORMAT = "(?<=\\G.{" + 2 + "})";

    if (question.getValidation().equals("date")) {
      if (content.length() != 4) {
        return null;
      }
      final String[] dateContent = content.split(REGEX_FORMAT);
      int mm = Integer.parseInt(dateContent[0]);
      int dd = Integer.parseInt(dateContent[1]);

      if (mm < 1 || mm > 12) {
        return null;
      }
      int yyyy = ResponseParser.getYear(mm);

      String date = String.format("%d-%d-%d", mm, dd, yyyy);
      if (!validDate(date)) {
        return null;
      }
    }

    if (question.getValidation().equals("time")) {
      if (content.length() != 4) {
        return null;
      }
      final String[] timeContent = content.split(REGEX_FORMAT);
      int hh = Integer.parseInt(timeContent[0]);
      int mm = Integer.parseInt(timeContent[1]);
      if ((hh < 9 || hh > 18) && (mm < 00 || mm > 60)) {
        return null;
      }
    }
    String sessionSid = request.getParameter(sessionKey);
    return new Response(content, sessionSid, question, candidate, LocalDate.now());
  }

  private boolean validDate(String date) {
    DateTimeFormatter formatter = DateTimeFormat.forPattern("MM-dd-yyyy");
    try {
      formatter.parseDateTime(date);
      return true;
    } catch (org.joda.time.IllegalFieldValueException e) {
      return false;
    }
  }
}

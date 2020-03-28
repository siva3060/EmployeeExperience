package io.dawn.ivrauto.util;

import com.twilio.http.HttpMethod;
import com.twilio.twiml.voice.Gather;
import com.twilio.twiml.voice.Record;
import io.dawn.ivrauto.model.Question;
import org.springframework.beans.factory.annotation.Value;

public class RecordFactory {

  @Value("${ngrok.domain}")
  private static String ngrokDomain;

  public static Record record(Question question) {
    return new Record.Builder()
        .action(ngrokDomain + "/save_response?qid=" + question.getId())
        .method(HttpMethod.POST)
        .transcribe(true)
        .transcribeCallback(ngrokDomain + "/save_response?qid=" + question.getId())
        .maxLength(60)
        .build();
  }

  public static Gather gather(Question question) {
    return new Gather.Builder()
        .action(ngrokDomain + "/save_response?qid=" + question.getId())
        .method(HttpMethod.POST)
        .finishOnKey("#")
        .build();
  }
}

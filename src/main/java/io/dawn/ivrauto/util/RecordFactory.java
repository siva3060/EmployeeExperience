package io.dawn.ivrauto.util;

import com.twilio.http.HttpMethod;
import com.twilio.twiml.voice.Gather;
import com.twilio.twiml.voice.Record;
import io.dawn.ivrauto.model.Question;
import org.springframework.beans.factory.annotation.Value;

public class RecordFactory {

  @Value("${ngrok.domain}")
  private static String ngrokDomain;

  @Value("${aws.instance}")
  private static String awsInstance;

  public static Record record(Question question) {
    return new Record.Builder()
        .action(awsInstance + "/save_response?qid=" + question.getId())
        .method(HttpMethod.POST)
        .transcribe(true)
        .transcribeCallback(awsInstance + "/save_response?qid=" + question.getId())
        .maxLength(60)
        .build();
  }

  public static Gather gather(Question question) {
    return new Gather.Builder()
        .action(awsInstance + "/save_response?qid=" + question.getId())
        .method(HttpMethod.POST)
        .finishOnKey("#")
        .build();
  }
}

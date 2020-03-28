package io.dawn.ivrauto.util;

import com.twilio.http.HttpMethod;
import com.twilio.twiml.MessagingResponse;
import com.twilio.twiml.TwiML;
import com.twilio.twiml.TwiMLException;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.messaging.Body;
import com.twilio.twiml.messaging.Message;
import com.twilio.twiml.voice.Gather;
import com.twilio.twiml.voice.Hangup;
import com.twilio.twiml.voice.Record;
import com.twilio.twiml.voice.Say;
import com.twilio.twiml.voice.Say.Voice;
import io.dawn.ivrauto.model.Question;
import io.dawn.ivrauto.model.Screening;

public class TwiMLUtil {

  public static String redirect(int nextQuestionNumber, Screening screening) throws TwiMLException {
    String nextQuestionURL =
        "/question?screening=" + screening.getId() + "&question=" + nextQuestionNumber;
    return redirect(nextQuestionURL, HttpMethod.GET).toXml();
  }

  public static String redirectPost(String url) throws TwiMLException {
    return redirect(url, HttpMethod.POST).toXml();
  }

  private static TwiML redirect(String url, HttpMethod method) {
    return new VoiceResponse.Builder()
        .redirect(new com.twilio.twiml.voice.Redirect.Builder(url).method(method).build())
        .build();
  }

  public static Record record(Question question) {
    return new Record.Builder()
        .action("/save_response?qid=" + question.getId())
        .method(HttpMethod.POST)
        .transcribe(true)
        .transcribeCallback("/save_response?qid=" + question.getId())
        .maxLength(60)
        .finishOnKey("#")
        .build();
  }

  public static Gather gather(Question question) {
    return new Gather.Builder()
        .action("/save_response?qid=" + question.getId())
        .method(HttpMethod.POST)
        .finishOnKey("#")
        .build();
  }

  public static String voiceResponse(String message) throws TwiMLException {
    return new VoiceResponse.Builder()
        .say(new Say.Builder(message).voice(Voice.POLLY_ADITI).build())
        .hangup(new Hangup.Builder().build())
        .build()
        .toXml();
  }

  public static String messagingResponse(String message) throws TwiMLException {
    return new MessagingResponse.Builder()
        .message(new Message.Builder().body(new Body.Builder(message).build()).build())
        .build()
        .toXml();
  }

  public static String voiceResponseWithRedirect(String message, String questionUrl)
      throws TwiMLException {
    return new VoiceResponse.Builder()
        .say(new Say.Builder(message).voice(Voice.POLLY_ADITI).build())
        .redirect(
            new com.twilio.twiml.voice.Redirect.Builder(questionUrl).method(HttpMethod.GET).build())
        .build()
        .toXml();
  }

  public static String messagingResponseWithRedirect(String message, String redirectUrl)
      throws TwiMLException {
    return new MessagingResponse.Builder()
        .redirect(
            new com.twilio.twiml.messaging.Redirect.Builder(redirectUrl)
                .method(HttpMethod.GET)
                .build())
        .build()
        .toXml();
  }
}

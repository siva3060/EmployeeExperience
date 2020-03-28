package io.dawn.ivrauto.util;

import com.twilio.twiml.TwiMLException;

public interface QuestionBuilder {
  String build() throws TwiMLException;

  String buildNoMoreQuestions() throws TwiMLException;
}

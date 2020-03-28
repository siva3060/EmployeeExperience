package io.dawn.ivrauto.config;

import com.twilio.Twilio;
import com.twilio.http.TwilioRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IVRConfig {
  @Bean
  public TwilioRestClient twilioRestClient(
      @Value("${twilio.account.sid}") String accountSid,
      @Value("${twilio.auth.token}") String authToken) {
    Twilio.init(accountSid, authToken);
    return new TwilioRestClient.Builder(accountSid, authToken).build();
  }
}

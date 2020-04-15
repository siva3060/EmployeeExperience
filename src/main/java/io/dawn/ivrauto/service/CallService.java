package io.dawn.ivrauto.service;

import com.twilio.http.HttpMethod;
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.type.PhoneNumber;
import io.dawn.ivrauto.model.Candidate;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CallService {

  @Value("${twilio.number}")
  public String fromTwilio;

  private TwilioRestClient restClient;

  @Value("${ngrok.domain}")
  private String ngrokDomain;

  @Autowired
  public CallService(TwilioRestClient restClient) {
    this.restClient = restClient;
  }

  public void call(final Candidate candidate) throws URISyntaxException {
    Call call =
        Call.creator(
                new PhoneNumber(candidate.getMobileNumber()),
                new PhoneNumber(fromTwilio),
                URI.create(
                    ngrokDomain
                        + "/screening/call?cid="
                        + candidate.getId()
                        + "&number="
                        + candidate.getMobileNumber()))
            .setMethod(HttpMethod.GET)
            .create(restClient);
    log.info("Call status: " + call.getStatus());
  }
}

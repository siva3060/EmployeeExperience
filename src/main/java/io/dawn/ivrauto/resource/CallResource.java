package io.dawn.ivrauto.resource;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import io.dawn.ivrauto.model.Candidate;
import io.dawn.ivrauto.service.CallService;
import io.dawn.ivrauto.service.CandidateService;
import java.net.URISyntaxException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class CallResource {
  private final CallService callService;
  private final CandidateService candidateService;

  @Autowired
  public CallResource(CallService callService, CandidateService candidateService) {
    this.callService = callService;
    this.candidateService = candidateService;
  }

  public static boolean isPhoneNumberValid(String phoneNumber) throws NumberParseException {
    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
    Phonenumber.PhoneNumber phNumber = phoneUtil.parse(phoneNumber, "IN");
    return phoneUtil.isValidNumber(phNumber);
  }

  // Use this end point to trigger the call manually
  @GetMapping(value = "/call")
  public void call() throws URISyntaxException, NumberParseException {
    List<Candidate> candidateList = candidateService.findCandidate();

    if (!candidateList.isEmpty()) {
      for (Candidate candidate : candidateList) {
        if (CallResource.isPhoneNumberValid(candidate.getMobileNumber())) {
          if (candidate.getInterviewStatus() == null) {
            log.info("calling: " + candidate.getName() + " on " + candidate.getMobileNumber());
            callService.call(candidate);
          }
        } else {
          throw new IllegalArgumentException(
              "Phone Number [" + candidate.getMobileNumber() + "] is not valid.");
        }
      }
    } else {
      throw new RuntimeException("Please upload candidates CSV.");
    }
  }
}

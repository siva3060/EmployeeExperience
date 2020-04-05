package io.dawn.ivrauto.resource;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import io.dawn.ivrauto.service.CallService;
import io.dawn.ivrauto.service.CandidateService;
import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableScheduling
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
  @GetMapping(value = "/call/{numberToCall}")
  public void call(@PathVariable String numberToCall)
      throws URISyntaxException, NumberParseException {
    if (CallResource.isPhoneNumberValid(numberToCall)) {
      callService.call(numberToCall);
    } else {
      throw new IllegalArgumentException("Phone Number [" + numberToCall + "] is not valid.");
    }
  }

  // This cron executes at second :00, every 5 minutes starting at minute :00, of every hour
  @Scheduled(cron = "${import.data.csv.cron}")
  @Transactional
  public void call() throws NumberParseException, URISyntaxException {
    String numberToCall = candidateService.findACandidateToCall("Nithin Prasad");
    if (CallResource.isPhoneNumberValid(numberToCall)) {
      callService.call(numberToCall);
    } else {
      throw new IllegalArgumentException("Phone Number [" + numberToCall + "] is not valid.");
    }
  }
}

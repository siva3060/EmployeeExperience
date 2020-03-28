package io.dawn.ivrauto.resource;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import io.dawn.ivrauto.service.CallService;
import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CallResource {
  private final CallService callService;

  @Autowired
  public CallResource(CallService callService) {
    this.callService = callService;
  }

  public static boolean isPhoneNumberValid(String phoneNumber) throws NumberParseException {
    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
    Phonenumber.PhoneNumber phNumber = phoneUtil.parse(phoneNumber, "IN");
    return phoneUtil.isValidNumber(phNumber);
  }

  @GetMapping(value = "/call/{numberToCall}")
  public void call(@PathVariable String numberToCall)
      throws URISyntaxException, NumberParseException {
    if (CallResource.isPhoneNumberValid(numberToCall)) {
      callService.call(numberToCall);
    } else {
      throw new IllegalArgumentException("Phone Number [" + numberToCall + "] is not valid.");
    }
  }
}

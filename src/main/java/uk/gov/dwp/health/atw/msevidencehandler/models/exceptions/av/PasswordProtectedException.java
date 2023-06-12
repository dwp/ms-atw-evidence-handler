package uk.gov.dwp.health.atw.msevidencehandler.models.exceptions.av;

import org.springframework.http.HttpStatus;

public class PasswordProtectedException extends AvException {

  public PasswordProtectedException() {
    super(HttpStatus.NOT_ACCEPTABLE, "PASSWORD PROTECTED");
  }
}

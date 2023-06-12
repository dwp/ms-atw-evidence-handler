package uk.gov.dwp.health.atw.msevidencehandler.models.exceptions.av;

import org.springframework.http.HttpStatus;

public class VirusFoundException extends AvException {

  public VirusFoundException() {
    super(HttpStatus.NOT_ACCEPTABLE, "VIRUS DETECTED");
  }
}

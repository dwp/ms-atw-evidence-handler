package uk.gov.dwp.health.atw.msevidencehandler.models.exceptions.av;

import org.springframework.http.HttpStatus;

public class FileConversionToPdfException extends AvException {

  public FileConversionToPdfException() {
    super(HttpStatus.UNPROCESSABLE_ENTITY, "UNABLE TO CONVERT FILE TO PDF");
  }
}

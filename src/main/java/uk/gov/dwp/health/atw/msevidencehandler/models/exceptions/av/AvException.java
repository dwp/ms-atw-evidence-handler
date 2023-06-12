package uk.gov.dwp.health.atw.msevidencehandler.models.exceptions.av;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@ToString
public abstract class AvException extends Exception {
  private HttpStatus errorCode;
  private String errorMessage;
}

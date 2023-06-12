package uk.gov.dwp.health.atw.msevidencehandler.controllers.utils;

import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.gov.dwp.health.atw.msevidencehandler.models.exceptions.av.AvException;
import uk.gov.dwp.health.atw.msevidencehandler.models.exceptions.av.PasswordProtectedException;
import uk.gov.dwp.health.atw.msevidencehandler.models.exceptions.av.VirusFoundException;

@ControllerAdvice
public class ServiceExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(SizeLimitExceededException.class)
  @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
  public ResponseEntity<Object> handleMaxSizeException(
      SizeLimitExceededException e, WebRequest r) {

    return new ResponseEntity<>(
        "Total payload size exceeds the limit. Full error message: "
            + e.getMessage(), new HttpHeaders(), HttpStatus.PAYLOAD_TOO_LARGE);
  }

  @ExceptionHandler(FileSizeLimitExceededException.class)
  @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
  public ResponseEntity<Object> handleMaxSizeException(
      FileSizeLimitExceededException e, WebRequest r) {

    return new ResponseEntity<>(
        "An individual file within the request exceeds the "
            + "single file size limit. Full error message: "
            + e.getMessage(),
        new HttpHeaders(), HttpStatus.PAYLOAD_TOO_LARGE);
  }

  @ExceptionHandler(value = UnsupportedMediaTypeStatusException.class)
  @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
  protected ResponseEntity<Object> handleUnsupportedMediaTypeException(
      UnsupportedMediaTypeStatusException ex, WebRequest request) {

    return handleExceptionInternal(ex, ex.getReason(),
        new HttpHeaders(), HttpStatus.UNSUPPORTED_MEDIA_TYPE, request);
  }


  @ExceptionHandler(value = {AvException.class, VirusFoundException.class,
      PasswordProtectedException.class})
  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  protected ResponseEntity<Object> handleAvExceptions(AvException ex, WebRequest request) {

    return handleExceptionInternal(ex, ex.getErrorMessage(),
        new HttpHeaders(), ex.getErrorCode(), request);
  }
}

package uk.gov.dwp.health.atw.msevidencehandler.utils;

import org.springframework.http.MediaType;

public class MsWordMediaType {

  public static final String APPLICATION_MS_DOCX_VALUE =
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
  public static final String APPLICATION_MS_DOC_VALUE = "application/msword";

  public static final MediaType APPLICATION_MS_DOCX =
      new MediaType("application", "vnd.openxmlformats-officedocument.wordprocessingml.document");
  public static final MediaType APPLICATION_MS_DOC = new MediaType("application", "msword");
}
package uk.gov.dwp.health.atw.msevidencehandler.connector;

import java.io.IOException;
import java.net.URISyntaxException;
import uk.gov.dwp.health.atw.msevidencehandler.models.exceptions.av.FileConversionToPdfException;
import uk.gov.dwp.health.atw.msevidencehandler.models.requests.ConvertFileRequest;
import uk.gov.dwp.health.atw.msevidencehandler.models.responses.PdfConversionServiceResponse;

public abstract class FileConverterConnector {

  public abstract PdfConversionServiceResponse post(ConvertFileRequest body)
      throws URISyntaxException, IOException, FileConversionToPdfException;
}

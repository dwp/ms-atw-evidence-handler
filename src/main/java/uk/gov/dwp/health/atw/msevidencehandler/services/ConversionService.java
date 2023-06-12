package uk.gov.dwp.health.atw.msevidencehandler.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.health.atw.msevidencehandler.connector.FileConverterConnector;
import uk.gov.dwp.health.atw.msevidencehandler.models.exceptions.av.FileConversionToPdfException;
import uk.gov.dwp.health.atw.msevidencehandler.models.requests.ConvertFileRequest;
import uk.gov.dwp.health.atw.msevidencehandler.models.responses.PdfConversionServiceResponse;
import uk.gov.dwp.health.atw.msevidencehandler.utils.Base64Converters;

public abstract class ConversionService {

  final Logger logger = LoggerFactory.getLogger(ConversionService.class);

  private final FileConverterConnector connector;

  public ConversionService(FileConverterConnector connector) {
    this.connector = connector;
  }

  public PdfConversionServiceResponse convertToPdf(InputStream inputStream,
                                                   String multipartFileContentType)
      throws IOException, URISyntaxException, FileConversionToPdfException {

    logger.info("Converting file to pdf after determining the file content type");
    String multipartFileAsBase64 = Base64Converters.toBase64(inputStream);

    ConvertFileRequest body = new ConvertFileRequest(
        UUID.randomUUID().toString(),
        multipartFileContentType,
        multipartFileAsBase64
    );

    return connector.post(body);
  }
}

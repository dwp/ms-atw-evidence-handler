package uk.gov.dwp.health.atw.msevidencehandler.connector.impl;

import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import uk.gov.dwp.health.atw.msevidencehandler.config.ApplicationConfig;
import uk.gov.dwp.health.atw.msevidencehandler.connector.FileConverterConnector;
import uk.gov.dwp.health.atw.msevidencehandler.models.ServiceConfig;
import uk.gov.dwp.health.atw.msevidencehandler.models.exceptions.av.FileConversionToPdfException;
import uk.gov.dwp.health.atw.msevidencehandler.models.requests.ConvertFileRequest;
import uk.gov.dwp.health.atw.msevidencehandler.models.responses.AvServiceResponse;
import uk.gov.dwp.health.atw.msevidencehandler.models.responses.PdfConversionServiceResponse;

@Service
public class File2PdfConnector extends FileConverterConnector {

  final Logger logger = LoggerFactory.getLogger(File2PdfConnector.class);

  final ApplicationConfig applicationConfig;

  public File2PdfConnector(ApplicationConfig applicationConfig, RestTemplate restTemplate) {
    this.applicationConfig = applicationConfig;
    this.restTemplate = restTemplate;
  }

  final RestTemplate restTemplate;

  @Override
  public PdfConversionServiceResponse post(ConvertFileRequest body)
      throws URISyntaxException, FileConversionToPdfException {
    logger.info("Converting a valid document type file to pdf");
    HttpEntity<ConvertFileRequest> request = new HttpEntity<>(body);
    ServiceConfig config = applicationConfig.getWord2pdf();

    URI uri = URI.create(config.getBaseUri() + "/v1/convert");

    try {
      return restTemplate.postForObject(uri, request, PdfConversionServiceResponse.class);
    } catch (HttpServerErrorException e) {
      logger.error("Unable to convert file to a pdf");
      throw new FileConversionToPdfException();
    }
  }
}

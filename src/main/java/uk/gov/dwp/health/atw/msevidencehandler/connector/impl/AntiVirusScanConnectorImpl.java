package uk.gov.dwp.health.atw.msevidencehandler.connector.impl;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;
import uk.gov.dwp.health.atw.msevidencehandler.config.ApplicationConfig;
import uk.gov.dwp.health.atw.msevidencehandler.connector.AntiVirusScanConnector;
import uk.gov.dwp.health.atw.msevidencehandler.models.FileNameAwareByteArrayResource;
import uk.gov.dwp.health.atw.msevidencehandler.models.exceptions.av.AvException;
import uk.gov.dwp.health.atw.msevidencehandler.models.exceptions.av.PasswordProtectedException;
import uk.gov.dwp.health.atw.msevidencehandler.models.exceptions.av.VirusFoundException;
import uk.gov.dwp.health.atw.msevidencehandler.models.responses.AvServiceResponse;
import uk.gov.dwp.health.atw.msevidencehandler.utils.MsWordMediaType;

@Service
public class AntiVirusScanConnectorImpl extends AntiVirusScanConnector {

  final Logger logger = LoggerFactory.getLogger(AntiVirusScanConnectorImpl.class);

  final ApplicationConfig applicationConfig;

  final RestTemplate restTemplate;

  public AntiVirusScanConnectorImpl(ApplicationConfig applicationConfig,
                                    RestTemplate restTemplate) {
    this.applicationConfig = applicationConfig;
    this.restTemplate = restTemplate;
  }

  @Override
  public AvServiceResponse scanForVirus(MultipartFile file)
      throws IOException, AvException {

    logger.info("Scanning for virus");

    HttpEntity<ByteArrayResource> attachment =
        createFileEntity(file);

    MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
    HttpHeaders mainRequestHeader = new HttpHeaders();
    mainRequestHeader.setContentType(MediaType.MULTIPART_FORM_DATA);

    multipartRequest.set("file", attachment);

    URI uri = URI.create(applicationConfig.getClamAvLander().getBaseUri() + "/v1/scan");

    try {
      logger.info("Posting the file attachment to the Rest Template for AvServiceResponse type");
      return restTemplate.postForObject(uri,
          new HttpEntity<>(multipartRequest, mainRequestHeader),
          AvServiceResponse.class);
    } catch (HttpClientErrorException | HttpServerErrorException httpClientOrServerExc) {
      if (HttpStatus.NOT_ACCEPTABLE.equals(httpClientOrServerExc.getStatusCode())) {
        if (httpClientOrServerExc.getResponseBodyAsString()
            .equalsIgnoreCase("{\"message\":\"VIRUS DETECTED\"}")) {
          throw new VirusFoundException();
        } else if (httpClientOrServerExc.getResponseBodyAsString()
            .equalsIgnoreCase("{\"message\":\"PASSWORD PROTECTED\"}")) {
          throw new PasswordProtectedException();
        }
      }
      throw httpClientOrServerExc;
    }
  }

  private HttpEntity<ByteArrayResource> createFileEntity(MultipartFile file)
      throws IOException {
    logger.info("Creating file entity for Rest Template");
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(
        getContentType(file));
    FileNameAwareByteArrayResource fileAsResource =
        new FileNameAwareByteArrayResource(file.getOriginalFilename(), file.getBytes());

    return new HttpEntity<>(fileAsResource, headers);
  }

  private MediaType getContentType(MultipartFile file) {

    String contentType = file.getContentType();

    if (contentType != null) {
      try {
        return MediaType.parseMediaType(contentType);
      } catch (InvalidMediaTypeException ex) {
        throw new UnsupportedMediaTypeStatusException("Unsupported file type");
      }
    }
    return MediaType.ALL;
  }
}

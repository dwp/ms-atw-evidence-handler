package uk.gov.dwp.health.atw.msevidencehandler.connector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.net.URISyntaxException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import uk.gov.dwp.health.atw.msevidencehandler.config.ApplicationConfig;
import uk.gov.dwp.health.atw.msevidencehandler.connector.impl.File2PdfConnector;
import uk.gov.dwp.health.atw.msevidencehandler.models.ServiceConfig;
import uk.gov.dwp.health.atw.msevidencehandler.models.exceptions.av.FileConversionToPdfException;
import uk.gov.dwp.health.atw.msevidencehandler.models.requests.ConvertFileRequest;
import uk.gov.dwp.health.atw.msevidencehandler.models.responses.PdfConversionServiceResponse;

@SpringBootTest(classes = File2PdfConnector.class)
public class File2PdfConnectorTest {

  @MockBean
  private RestTemplate restTemplate;

  @MockBean
  private ApplicationConfig config;

  @Autowired
  File2PdfConnector connector;

  @Test
  @DisplayName("Success")
  void successfulConversion() throws URISyntaxException, FileConversionToPdfException {
    PdfConversionServiceResponse postForObjectResponse = new PdfConversionServiceResponse();
    ConvertFileRequest convertFileRequest = new ConvertFileRequest();
    when(restTemplate.postForObject(any(), any(), any())).thenReturn(
        postForObjectResponse);

    when(config.getWord2pdf()).thenReturn(new ServiceConfig("http://host:1234"));

    Assertions.assertEquals(connector.post(convertFileRequest), postForObjectResponse);
  }

  @Test
  @DisplayName("unable to convert file to a pdf")
  void failConvertingFileToPdf() {

    ConvertFileRequest convertFileRequest = new ConvertFileRequest();
    when(restTemplate.postForObject(any(), any(), any()))
        .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

    when(config.getWord2pdf()).thenReturn(new ServiceConfig("http://host:1234"));

    FileConversionToPdfException thrown = assertThrows(FileConversionToPdfException.class,
        () -> connector.post(convertFileRequest));

    assertEquals("UNABLE TO CONVERT FILE TO PDF", thrown.getErrorMessage());
  }
}

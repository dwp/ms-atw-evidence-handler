package uk.gov.dwp.health.atw.msevidencehandler.connector;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;
import uk.gov.dwp.health.atw.msevidencehandler.config.ApplicationConfig;
import uk.gov.dwp.health.atw.msevidencehandler.connector.impl.AntiVirusScanConnectorImpl;
import uk.gov.dwp.health.atw.msevidencehandler.models.ServiceConfig;
import uk.gov.dwp.health.atw.msevidencehandler.models.exceptions.av.AvException;
import uk.gov.dwp.health.atw.msevidencehandler.models.exceptions.av.PasswordProtectedException;
import uk.gov.dwp.health.atw.msevidencehandler.models.exceptions.av.VirusFoundException;
import uk.gov.dwp.health.atw.msevidencehandler.models.responses.AvServiceResponse;
import uk.gov.dwp.health.atw.msevidencehandler.utils.MsWordMediaType;

@SpringBootTest(classes = AntiVirusScanConnectorImpl.class)
public class AntiVirusScanConnectorTest {

  @MockBean
  private RestTemplate restTemplate;

  @MockBean
  private ApplicationConfig config;

  @Autowired
  AntiVirusScanConnectorImpl connector;

  AvServiceResponse avPassed = new AvServiceResponse(null, null, null);

  @Test
  @DisplayName("no virus found")
  void noVirusFound() throws IOException, AvException {
    MockMultipartFile filePng
        = new MockMultipartFile(
        "files",
        "hello.png",
        MediaType.IMAGE_PNG_VALUE,
        "Hello, World!".getBytes()
    );

    when(restTemplate.postForObject(any(), any(), any())).thenReturn(
        avPassed);

    when(config.getClamAvLander()).thenReturn(new ServiceConfig("http://host:1234"));

    Assertions.assertEquals(avPassed, connector.scanForVirus(filePng));
  }


  @Test
  @DisplayName("virus found")
  void virusFound() {
    MockMultipartFile filePng
        = new MockMultipartFile(
        "files",
        "hello.png",
        MediaType.IMAGE_PNG_VALUE,
        "Hello, World!".getBytes()
    );

    when(restTemplate.postForObject(any(), any(), any())).thenThrow(
        new HttpClientErrorException(HttpStatus.NOT_ACCEPTABLE, "{\"message\":\"VIRUS DETECTED\"}",
            "{\"message\":\"VIRUS DETECTED\"}".getBytes(
                StandardCharsets.UTF_8), StandardCharsets.UTF_8));

    when(config.getClamAvLander()).thenReturn(new ServiceConfig("http://host:1234"));
    Assertions.assertThrows(VirusFoundException.class,
        () -> connector.scanForVirus(filePng)
    );
  }

  @Test
  @DisplayName("password protected file")
  void passwordProtected() {
    MockMultipartFile filePng
        = new MockMultipartFile(
        "files",
        "hello.png",
        MediaType.IMAGE_PNG_VALUE,
        "Hello, World!".getBytes()
    );

    when(restTemplate.postForObject(any(), any(), any())).thenThrow(
        new HttpClientErrorException(HttpStatus.NOT_ACCEPTABLE, "{\"message\":\"PASSWORD PROTECTED\"}",
            "{\"message\":\"PASSWORD PROTECTED\"}".getBytes(
                StandardCharsets.UTF_8), StandardCharsets.UTF_8));

    when(config.getClamAvLander()).thenReturn(new ServiceConfig("http://host:1234"));
    Assertions.assertThrows(PasswordProtectedException.class,
        () -> connector.scanForVirus(filePng)
    );
  }

  @Test
  @DisplayName("pdf password protected file")
  void pdfPasswordProtected() {
    MockMultipartFile filePdf
        = new MockMultipartFile(
        "files",
        "hello.pdf",
        MediaType.APPLICATION_PDF_VALUE,
        "Hello, World!".getBytes()
    );

    when(restTemplate.postForObject(any(), any(), any())).thenThrow(
        new HttpClientErrorException(HttpStatus.NOT_ACCEPTABLE, "{\"message\":\"PASSWORD PROTECTED\"}",
            "{\"message\":\"PASSWORD PROTECTED\"}".getBytes(
                StandardCharsets.UTF_8), StandardCharsets.UTF_8));

    when(config.getClamAvLander()).thenReturn(new ServiceConfig("http://host:1234"));
    Assertions.assertThrows(PasswordProtectedException.class,
        () -> connector.scanForVirus(filePdf)
    );
  }

  @Test
  @DisplayName("doc password protected file")
  void docPasswordProtected() {
    MockMultipartFile fileDoc
        = new MockMultipartFile(
        "files",
        "hello.doc",
       MsWordMediaType.APPLICATION_MS_DOC_VALUE,
        "Hello, World!".getBytes()
    );

    when(restTemplate.postForObject(any(), any(), any())).thenThrow(
        new HttpClientErrorException(HttpStatus.NOT_ACCEPTABLE, "{\"message\":\"PASSWORD PROTECTED\"}",
            "{\"message\":\"PASSWORD PROTECTED\"}".getBytes(
                StandardCharsets.UTF_8), StandardCharsets.UTF_8));

    when(config.getClamAvLander()).thenReturn(new ServiceConfig("http://host:1234"));
    Assertions.assertThrows(PasswordProtectedException.class,
        () -> connector.scanForVirus(fileDoc)
    );
  }

  @Test
  @DisplayName("docx password protected file")
  void docxPasswordProtected() {
    MockMultipartFile fileDocx
        = new MockMultipartFile(
        "files",
        "hello.docx",
       MsWordMediaType.APPLICATION_MS_DOCX_VALUE,
        "Hello, World!".getBytes()
    );

    when(restTemplate.postForObject(any(), any(), any())).thenThrow(
        new HttpClientErrorException(HttpStatus.NOT_ACCEPTABLE, "{\"message\":\"PASSWORD PROTECTED\"}",
            "{\"message\":\"PASSWORD PROTECTED\"}".getBytes(
                StandardCharsets.UTF_8), StandardCharsets.UTF_8));

    when(config.getClamAvLander()).thenReturn(new ServiceConfig("http://host:1234"));
    Assertions.assertThrows(PasswordProtectedException.class,
        () -> connector.scanForVirus(fileDocx)
    );
  }

  @Test
  @DisplayName("invalid file content type")
  void invalidFileContentType() {
    MockMultipartFile fileDocx
        = new MockMultipartFile(
        "files",
        "hello.abw",
        "abw",
        "Hello, World!".getBytes()
    );

    when(config.getClamAvLander()).thenReturn(new ServiceConfig("http://host:1234"));
    UnsupportedMediaTypeStatusException assertThrows =
        Assertions.assertThrows(UnsupportedMediaTypeStatusException.class,
            () -> connector.scanForVirus(fileDocx)
        );
    Assertions.assertEquals("Unsupported file type", assertThrows.getReason());
  }

  @Test
  @DisplayName("OTHER_ERROR")
  void otherError() {
    MockMultipartFile filePng
        = new MockMultipartFile(
        "files",
        "hello.png",
        MediaType.IMAGE_PNG_VALUE,
        "Hello, World!".getBytes()
    );

    when(restTemplate.postForObject(any(), any(), any())).thenThrow(
        new HttpClientErrorException(HttpStatus.NOT_ACCEPTABLE, "OTHER_ERROR",
            "OTHER_ERROR".getBytes(
                StandardCharsets.UTF_8), StandardCharsets.UTF_8));

    when(config.getClamAvLander()).thenReturn(new ServiceConfig("http://host:1234"));
    Assertions.assertThrows(HttpClientErrorException.class,
        () -> connector.scanForVirus(filePng)
    );
  }

  @Test
  @DisplayName("failed to sent multipart files")
  void failedToSendMultiPartFile() {
    MockMultipartFile filePng
        = new MockMultipartFile(
        "files",
        "hello.png",
        MediaType.IMAGE_PNG_VALUE,
        "Hello, World!".getBytes()
    );

    when(restTemplate.postForObject(any(), any(), any())).thenThrow(
        new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
            "MULTIPART FAIL",
            "MULTIPART FAIL".getBytes(
                StandardCharsets.UTF_8), StandardCharsets.UTF_8));

    when(config.getClamAvLander()).thenReturn(new ServiceConfig("http://host:1234"));
    Assertions.assertThrows(HttpServerErrorException.class,
        () -> connector.scanForVirus(filePng)
    );
  }
}

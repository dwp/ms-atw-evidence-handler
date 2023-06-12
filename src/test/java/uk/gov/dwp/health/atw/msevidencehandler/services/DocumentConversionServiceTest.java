package uk.gov.dwp.health.atw.msevidencehandler.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;
import uk.gov.dwp.health.atw.msevidencehandler.connector.impl.File2PdfConnector;
import uk.gov.dwp.health.atw.msevidencehandler.models.exceptions.av.FileConversionToPdfException;
import uk.gov.dwp.health.atw.msevidencehandler.models.requests.ConvertFileRequest;
import uk.gov.dwp.health.atw.msevidencehandler.models.responses.PdfConversionServiceResponse;
import uk.gov.dwp.health.atw.msevidencehandler.services.impl.DocumentConversionService;

@SpringBootTest(classes = DocumentConversionService.class)
public class DocumentConversionServiceTest {
  @Autowired
  DocumentConversionService DocumentConversionService;

  @MockBean
  private File2PdfConnector connector;

  @ParameterizedTest
  @ValueSource(strings = {"application/msword",
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document"})
  void convertDocumentFile(String fileType)
      throws URISyntaxException, IOException, FileConversionToPdfException {
    String uuid = UUID.randomUUID().toString();

    PdfConversionServiceResponse conversionServiceResponse = new PdfConversionServiceResponse(
        uuid,
        "application/pdf",
        "ConvertedMultipartFile");

    MockMultipartFile file
        = new MockMultipartFile(
        "files",
        "hello.txt",
        fileType,
        "Hello, World!".getBytes()
    );

    when(connector.post(any(ConvertFileRequest.class))).thenReturn(
        conversionServiceResponse);

    Assertions.assertEquals(DocumentConversionService.convertToPdf(file.getInputStream(), fileType),
        conversionServiceResponse);
  }

  @Test
  @DisplayName("fail validation check not in accepted formats")
  void failToConvertDocumentFileTxtFile() throws URISyntaxException, FileConversionToPdfException {

    MockMultipartFile file
        = new MockMultipartFile(
        "files",
        "hello.txt",
        MediaType.TEXT_PLAIN_VALUE,
        "Hello, World!".getBytes()
    );

    when(connector.post(any(ConvertFileRequest.class))).thenThrow(
        UnsupportedMediaTypeStatusException.class);

    Assertions.assertThrows(UnsupportedMediaTypeStatusException.class,
        () -> DocumentConversionService.convertToPdf(file.getInputStream(),
            MediaType.TEXT_PLAIN_VALUE)
    );
  }
}

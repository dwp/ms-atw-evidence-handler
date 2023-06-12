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
import uk.gov.dwp.health.atw.msevidencehandler.connector.impl.Img2PdfConnector;
import uk.gov.dwp.health.atw.msevidencehandler.models.exceptions.av.FileConversionToPdfException;
import uk.gov.dwp.health.atw.msevidencehandler.models.requests.ConvertFileRequest;
import uk.gov.dwp.health.atw.msevidencehandler.models.responses.PdfConversionServiceResponse;
import uk.gov.dwp.health.atw.msevidencehandler.services.impl.ImageConversionService;

@SpringBootTest(classes = ImageConversionService.class)
public class ImageConversionServiceTest {
  @Autowired
  ImageConversionService imageConversionService;

  @MockBean
  private Img2PdfConnector connector;

  @ParameterizedTest
  @ValueSource(strings = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, "image/jpg"})
  @DisplayName("convert image file successfully")
  void convertImageFile(String fileType)
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

    Assertions.assertEquals(imageConversionService.convertToPdf(file.getInputStream(), fileType),
        conversionServiceResponse);
  }

  @Test
  @DisplayName("fail validation check not in accepted formats")
  void failToConvertImageFileTxtFile() throws URISyntaxException, FileConversionToPdfException {

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
        () -> imageConversionService.convertToPdf(file.getInputStream(), MediaType.TEXT_PLAIN_VALUE)
    );
  }
}

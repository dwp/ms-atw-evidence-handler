package uk.gov.dwp.health.atw.msevidencehandler.services;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.SdkClientException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
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
import uk.gov.dwp.health.atw.msevidencehandler.connector.impl.AntiVirusScanConnectorImpl;
import uk.gov.dwp.health.atw.msevidencehandler.models.exceptions.av.PasswordProtectedException;
import uk.gov.dwp.health.atw.msevidencehandler.models.exceptions.av.VirusFoundException;
import uk.gov.dwp.health.atw.msevidencehandler.models.responses.AvServiceResponse;
import uk.gov.dwp.health.atw.msevidencehandler.models.responses.PdfConversionServiceResponse;
import uk.gov.dwp.health.atw.msevidencehandler.services.impl.DocumentConversionService;
import uk.gov.dwp.health.atw.msevidencehandler.services.impl.ImageConversionService;
import uk.gov.dwp.health.atw.msevidencehandler.services.impl.S3ServiceImpl;
import uk.gov.dwp.health.atw.msevidencehandler.utils.Base64Converters;

@SpringBootTest(classes = FileToPdfService.class)
public class FileToPdfServiceTest {

  @Autowired
  private FileToPdfService fileToPdfService;

  @MockBean
  private ImageConversionService imageConversionService;

  @MockBean
  private DocumentConversionService documentConversionService;

  @MockBean
  private AntiVirusScanConnectorImpl antiVirusScanConnector;

  @MockBean
  private S3ServiceImpl s3Service;

  String uuid = UUID.randomUUID().toString();
  AvServiceResponse passedAv = new AvServiceResponse(null, null, null);

  @DisplayName("image convert successfully")
  @ParameterizedTest
  @ValueSource(strings = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, "image/jpg"})
  public void convertToPdfValidImageType(String fileType) throws Exception {

    PdfConversionServiceResponse conversionServiceResponse = new PdfConversionServiceResponse(
        uuid,
        "application/pdf",
        "ConvertedMultipartFile");

    MockMultipartFile file
        = new MockMultipartFile(
        "file",
        "hello.txt",
        fileType,
        "Hello, World!".getBytes()
    );

    when(imageConversionService.convertToPdf(any(InputStream.class),
        any(String.class))).thenReturn(conversionServiceResponse);

    assertEquals("ConvertedMultipartFile", fileToPdfService.convertToPdf(file));

    verify(imageConversionService, times(1))
        .convertToPdf(any(InputStream.class), any(String.class));
    verify(documentConversionService, never()).convertToPdf(any(), any());
  }

  @DisplayName("document convert successfully")
  @ParameterizedTest
  @ValueSource(strings = {"application/msword",
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document"})
  public void convertToPdfValidDocumentType(String fileType) throws Exception {

    PdfConversionServiceResponse conversionServiceResponse = new PdfConversionServiceResponse(
        uuid,
        "application/pdf",
        "ConvertedMultipartFile");

    MockMultipartFile file
        = new MockMultipartFile(
        "file",
        "hello.txt",
        fileType,
        "Hello, World!".getBytes()
    );

    when(documentConversionService.convertToPdf(any(InputStream.class),
        any(String.class))).thenReturn(conversionServiceResponse);

    assertEquals("ConvertedMultipartFile", fileToPdfService.convertToPdf(file));

    verify(documentConversionService, times(1))
        .convertToPdf(any(InputStream.class), any(String.class));
    verify(imageConversionService, never()).convertToPdf(any(), any());
  }

  @DisplayName("convertToPdf process PDF successfully")
  @Test
  public void convertToPdfProcessPDF() throws Exception {

    MockMultipartFile file
        = new MockMultipartFile(
        "file",
        "hello.pdf",
        MediaType.APPLICATION_PDF_VALUE,
        "Hello, World!".getBytes()
    );

    String encodedFile = Base64Converters.toBase64(file.getInputStream());
    assertEquals(encodedFile, fileToPdfService.convertToPdf(file));

    verify(documentConversionService, never()).convertToPdf(any(), any());
    verify(imageConversionService, never()).convertToPdf(any(), any());
  }

  @DisplayName("convertToPdf invalid file type throws exception")
  @Test
  public void convertToPdfInvalidFileTypeThrowsException() throws Exception {

    MockMultipartFile file
        = new MockMultipartFile(
        "file",
        "hello.pdf",
        MediaType.APPLICATION_XML_VALUE,
        "Hello, World!".getBytes()
    );

    assertThrows(UnsupportedMediaTypeStatusException.class,
        () -> fileToPdfService.convertToPdf(file));

    verify(imageConversionService, never()).convertToPdf(any(), any());
    verify(documentConversionService, never()).convertToPdf(any(), any());
  }

  @DisplayName("convertToPdf image type fails for invalid format")
  @ParameterizedTest
  @ValueSource(strings = {MediaType.IMAGE_GIF_VALUE})
  public void convertToPdfInvalidImageType(String fileType) throws Exception {
    MockMultipartFile file
        = new MockMultipartFile(
        "file",
        "invalid.txt",
        fileType,
        "Hello, World!".getBytes());

    assertThrows(UnsupportedMediaTypeStatusException.class,
        () -> fileToPdfService.convertToPdf(file));

    verify(imageConversionService, never()).convertToPdf(any(), any());
    verify(documentConversionService, never()).convertToPdf(any(), any());
  }

  @DisplayName("convertToPdf document type fails for invalid format")
  @ParameterizedTest
  @ValueSource(strings = {"application/vnd.amazon.ebook"})
  public void convertToPdfInvalidDocumentType(String fileType) throws Exception {
    MockMultipartFile file
        = new MockMultipartFile(
        "file",
        "invalid.txt",
        fileType,
        "Hello, World!".getBytes());

    assertThrows(UnsupportedMediaTypeStatusException.class,
        () -> fileToPdfService.convertToPdf(file));

    verify(imageConversionService, never()).convertToPdf(any(), any());
    verify(documentConversionService, never()).convertToPdf(any(), any());
  }

  @DisplayName("/convertToPdf - conversion to PDF failed")
  @ParameterizedTest
  @ValueSource(strings = {"application/msword",
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document"})
  public void convertToPdfConversionFails(String fileType) throws Exception {

    MockMultipartFile file
        = new MockMultipartFile(
        "file",
        "hello.txt",
        fileType,
        "Hello, World!".getBytes()
    );

    when(documentConversionService.convertToPdf(any(InputStream.class),
        any(String.class))).thenThrow(IOException.class);

    assertThrows(IOException.class,
        () -> fileToPdfService.convertToPdf(file));

    verify(imageConversionService, never()).convertToPdf(any(), any());
    verify(documentConversionService, times(1)).convertToPdf(any(), any());
  }

  @DisplayName("scanForViruses passes antivirus check")
  @ParameterizedTest
  @ValueSource(strings = {MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_PNG_VALUE,
      MediaType.IMAGE_JPEG_VALUE})
  public void scanForVirusesFilePassAntiVirusCheck(String fileType) throws Exception {
    MockMultipartFile file
        = new MockMultipartFile(
        "file",
        "invalid.txt",
        fileType,
        "Hello, World!".getBytes());

    when(antiVirusScanConnector.scanForVirus(any()))
        .thenReturn(passedAv);

    fileToPdfService.scanForViruses(file);

    verify(antiVirusScanConnector, times(1)).scanForVirus(any());
  }

  @DisplayName("scanForViruses fails on antivirus check")
  @ParameterizedTest
  @ValueSource(strings = {MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_PNG_VALUE,
      MediaType.IMAGE_JPEG_VALUE})
  public void scanForVirusesFileFailAntiVirusCheck(String fileType) throws Exception {
    MockMultipartFile file
        = new MockMultipartFile(
        "file",
        "invalid.txt",
        fileType,
        "Hello, World!".getBytes());

    when(antiVirusScanConnector.scanForVirus(any()))
        .thenThrow(new VirusFoundException());

    assertThrows(VirusFoundException.class,
        () -> fileToPdfService.scanForViruses(file));

    verify(antiVirusScanConnector, times(1)).scanForVirus(any());
  }

  @DisplayName("scanForViruses fails on password protection check")
  @ParameterizedTest
  @ValueSource(strings = {MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_PNG_VALUE,
      MediaType.IMAGE_JPEG_VALUE})
  public void scanForVirusesFileFailAntiVirusCheckPasswordProtected(String fileType)
      throws Exception {
    MockMultipartFile file
        = new MockMultipartFile(
        "file",
        "invalid.txt",
        fileType,
        "Hello, World!".getBytes());

    when(antiVirusScanConnector.scanForVirus(any()))
        .thenThrow(new PasswordProtectedException());

    assertThrows(PasswordProtectedException.class,
        () -> fileToPdfService.scanForViruses(file));

    verify(antiVirusScanConnector, times(1)).scanForVirus(any());
  }

  @DisplayName("upload file to s3 successful")
  @Test
  public void uploadFileToS3Successful() {

    when(s3Service.uploadToS3(any(String.class), any(String.class)))
        .thenReturn("uploaded file key");

    assertEquals("uploaded file key",
        fileToPdfService.upload(UUID.randomUUID().toString(), "uploaded file key"));

    verify(s3Service, times(1)).uploadToS3(any(), any());
  }

  @DisplayName("upload file to s3 failed")
  @Test
  public void uploadFileToS3failed() {

    when(s3Service.uploadToS3(any(String.class), any(String.class)))
        .thenThrow(SdkClientException.class);

    assertThrows(SdkClientException.class,
        () -> fileToPdfService.upload(UUID.randomUUID().toString(), "uploaded file key"));

    verify(s3Service, times(1)).uploadToS3(any(), any());
  }
}

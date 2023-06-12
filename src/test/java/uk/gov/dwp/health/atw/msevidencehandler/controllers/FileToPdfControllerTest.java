package uk.gov.dwp.health.atw.msevidencehandler.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.dwp.health.atw.msevidencehandler.utils.TestUtils.asJsonString;

import java.io.IOException;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import uk.gov.dwp.health.atw.msevidencehandler.controllers.utils.ServiceExceptionHandler;
import uk.gov.dwp.health.atw.msevidencehandler.models.exceptions.av.PasswordProtectedException;
import uk.gov.dwp.health.atw.msevidencehandler.models.exceptions.av.VirusFoundException;
import uk.gov.dwp.health.atw.msevidencehandler.models.responses.ConversionResponse;
import uk.gov.dwp.health.atw.msevidencehandler.services.FileToPdfService;
import uk.gov.dwp.health.atw.msevidencehandler.services.impl.S3ServiceImpl;

@SpringBootTest(classes = FileToPdfController.class)
@EnableWebMvc
@AutoConfigureMockMvc
@ImportAutoConfiguration(ServiceExceptionHandler.class)
public class FileToPdfControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private S3ServiceImpl s3Service;

  @MockBean
  private FileToPdfService fileToPdfService;

  String uuid = UUID.randomUUID().toString();

  @DisplayName("/convert convert image files successfully")
  @ParameterizedTest
  @ValueSource(strings = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, "image/jpg"})
  public void convertValidImageTypeToPdf(String fileType) throws Exception {

    ConversionResponse response = new ConversionResponse("1");

    MockMultipartFile file
        = new MockMultipartFile(
        "file",
        "hello.txt",
        fileType,
        "Hello, World!".getBytes()
    );

    when(fileToPdfService.convertToPdf(any(MultipartFile.class)))
        .thenReturn("1");

    when(fileToPdfService.upload(any(), any()))
        .thenReturn("1");

    mockMvc.perform(multipart("/convert")
            .file(file).param("userId", uuid))
        .andExpect(status().isOk())
        .andExpect(content().json(asJsonString(response)));

    verify(fileToPdfService, times(1)).scanForViruses(any());
    verify(fileToPdfService, times(1)).convertToPdf(any());
    verify(fileToPdfService, times(1)).upload(any(), any());
  }

  @DisplayName("/convert convert document files successfully")
  @ParameterizedTest
  @ValueSource(strings = {"application/msword",
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document"})
  public void convertValidDocumentTypeToPdf(String fileType) throws Exception {

    ConversionResponse response = new ConversionResponse("1");

    MockMultipartFile file
        = new MockMultipartFile(
        "file",
        "hello.txt",
        fileType,
        "Hello, World!".getBytes()
    );

    when(fileToPdfService.convertToPdf(any(MultipartFile.class)))
        .thenReturn("1");

    when(fileToPdfService.upload(any(), any()))
        .thenReturn("1");

    mockMvc.perform(multipart("/convert")
            .file(file).param("userId", uuid))
        .andExpect(status().isOk())
        .andExpect(content().json(asJsonString(response)));

    verify(fileToPdfService, times(1)).scanForViruses(any());
    verify(fileToPdfService, times(1)).convertToPdf(any());
    verify(fileToPdfService, times(1)).upload(any(), any());
  }

  @DisplayName("/convert Process PDF successfully (just upload to S3)")
  @Test
  public void processPDF() throws Exception {

    ConversionResponse response = new ConversionResponse("1");

    MockMultipartFile file
        = new MockMultipartFile(
        "file",
        "hello.pdf",
        MediaType.APPLICATION_PDF_VALUE,
        "Hello, World!".getBytes()
    );

    when(fileToPdfService.convertToPdf(any(MultipartFile.class)))
        .thenReturn("1");

    when(fileToPdfService.upload(any(), any()))
        .thenReturn("1");

    mockMvc.perform(multipart("/convert")
            .file(file).param("userId", uuid))
        .andExpect(status().isOk())
        .andExpect(content().json(asJsonString(response)));

    verify(fileToPdfService, times(1)).scanForViruses(any());
    verify(fileToPdfService, times(1)).convertToPdf(any());
    verify(fileToPdfService, times(1)).upload(any(), any());
  }

  @DisplayName("/convert - conversion to PDF failed")
  @ParameterizedTest
  @ValueSource(strings = {"application/msword",
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document"})
  public void ensurePdfIsNotUploadedIfConversionFails(String fileType) throws Exception {

    MockMultipartFile file
        = new MockMultipartFile(
        "file",
        "hello.txt",
        fileType,
        "Hello, World!".getBytes()
    );

    when(fileToPdfService.convertToPdf(any(MultipartFile.class)))
        .thenThrow(IOException.class);

    Assertions.assertThrows(IOException.class,
        () -> mockMvc.perform(multipart("/convert").file(file).param("userId", uuid)));

    verify(fileToPdfService, times(1)).scanForViruses(any());
    verify(fileToPdfService, times(1)).convertToPdf(any());
    verify(fileToPdfService, never()).upload(any(), any());
  }

  @DisplayName("/convert convert fails for invalid format")
  @ParameterizedTest
  @ValueSource(strings = {MediaType.IMAGE_GIF_VALUE})
  public void invalidImageTypeToPdfInvalidFormat(String fileType) throws Exception {
    MockMultipartFile file
        = new MockMultipartFile(
        "file",
        "invalid.txt",
        fileType,
        "Hello, World!".getBytes());

    when(fileToPdfService.convertToPdf(any(MultipartFile.class)))
        .thenThrow(UnsupportedMediaTypeStatusException.class);

    mockMvc.perform(multipart("/convert")
            .file(file).param("userId", uuid))
        .andExpect(status().isUnsupportedMediaType());

    verify(fileToPdfService, times(1)).scanForViruses(any());
    verify(fileToPdfService, times(1)).convertToPdf(any());
    verify(fileToPdfService, never()).upload(any(), any());
  }


  @DisplayName("/convert convert fails on antivirus check")
  @ParameterizedTest
  @ValueSource(strings = {MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_PNG_VALUE,
      MediaType.IMAGE_JPEG_VALUE})
  public void filesFailAntiVirusCheck(String fileType) throws Exception {
    MockMultipartFile file
        = new MockMultipartFile(
        "file",
        "invalid.txt",
        fileType,
        "Hello, World!".getBytes());

    doThrow(new VirusFoundException()).when(fileToPdfService).scanForViruses(any());

    mockMvc.perform(multipart("/convert")
        .file(file).param("userId", uuid)).andExpect(status().isNotAcceptable());

    verify(fileToPdfService, times(1)).scanForViruses(any());
    verify(fileToPdfService, never()).convertToPdf(any());
    verify(fileToPdfService, never()).upload(any(), any());
  }

  @DisplayName("/convert convert fails on password protection check")
  @ParameterizedTest
  @ValueSource(strings = {MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_PNG_VALUE,
      MediaType.IMAGE_JPEG_VALUE})
  public void filesFailAntiVirusCheckPasswordProtected(String fileType) throws Exception {
    MockMultipartFile file
        = new MockMultipartFile(
        "file",
        "invalid.txt",
        fileType,
        "Hello, World!".getBytes());

    doThrow(new PasswordProtectedException()).when(fileToPdfService).scanForViruses(any());

    mockMvc.perform(multipart("/convert")
        .file(file).param("userId", uuid)).andExpect(status().isNotAcceptable());

    verify(fileToPdfService, times(1)).scanForViruses(any());
    verify(fileToPdfService, never()).convertToPdf(any());
    verify(fileToPdfService, never()).upload(any(), any());
  }

  @DisplayName("/delete remove file from S3")
  @Test
  public void deleteFileFromS3() throws Exception {

    doNothing().when(s3Service).deleteFile(any());

    mockMvc.perform(multipart("/delete")
        .param("key", uuid)).andExpect(status().isOk());

    verify(s3Service, times(1)).deleteFile(uuid);
    verify(fileToPdfService, never()).scanForViruses(any());
    verify(fileToPdfService, never()).convertToPdf(any());
    verify(s3Service, never()).uploadToS3(any(), any());
  }
}

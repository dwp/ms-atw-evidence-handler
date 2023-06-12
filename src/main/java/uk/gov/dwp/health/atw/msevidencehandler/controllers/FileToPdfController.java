package uk.gov.dwp.health.atw.msevidencehandler.controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.dwp.health.atw.msevidencehandler.models.exceptions.av.AvException;
import uk.gov.dwp.health.atw.msevidencehandler.models.responses.ConversionResponse;
import uk.gov.dwp.health.atw.msevidencehandler.services.FileToPdfService;
import uk.gov.dwp.health.atw.msevidencehandler.services.impl.S3ServiceImpl;

@RestController
public class FileToPdfController {
  final Logger logger = LoggerFactory.getLogger(FileToPdfController.class);
  final S3ServiceImpl s3Service;
  final FileToPdfService fileToPdfService;

  public FileToPdfController(
      S3ServiceImpl s3Service,
      FileToPdfService fileToPdfService) {
    this.s3Service = s3Service;
    this.fileToPdfService = fileToPdfService;
  }

  @PostMapping(value = "/delete",
      produces = "application/json")
  public ResponseEntity<String> deleteFile(
      @RequestParam("key") String fileKey) {
    logger.info("Executing the delete endpoint");
    s3Service.deleteFile(fileKey);
    return ResponseEntity.ok().build();
  }

  @PostMapping(value = "/convert",
      produces = "application/json")
  public ResponseEntity<ConversionResponse> convertMultipleFile(
      @RequestParam("file") MultipartFile file, @RequestParam("userId") UUID userId)
      throws IOException, URISyntaxException, AvException {

    logger.info("Executing the convert endpoint");
    fileToPdfService.scanForViruses(file);
    logger.info("File scanned for viruses");
    String fileToUpload = fileToPdfService.convertToPdf(file);
    logger.info("File converted to pdf");
    String uploadFileKeys = fileToPdfService.upload(userId, fileToUpload);
    return ResponseEntity.ok(new ConversionResponse(uploadFileKeys));
  }
}

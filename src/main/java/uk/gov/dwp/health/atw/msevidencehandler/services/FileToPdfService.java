package uk.gov.dwp.health.atw.msevidencehandler.services;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;
import uk.gov.dwp.health.atw.msevidencehandler.config.FileFormats;
import uk.gov.dwp.health.atw.msevidencehandler.connector.impl.AntiVirusScanConnectorImpl;
import uk.gov.dwp.health.atw.msevidencehandler.models.exceptions.av.AvException;
import uk.gov.dwp.health.atw.msevidencehandler.models.exceptions.av.FileConversionToPdfException;
import uk.gov.dwp.health.atw.msevidencehandler.services.impl.DocumentConversionService;
import uk.gov.dwp.health.atw.msevidencehandler.services.impl.ImageConversionService;
import uk.gov.dwp.health.atw.msevidencehandler.services.impl.S3ServiceImpl;
import uk.gov.dwp.health.atw.msevidencehandler.utils.Base64Converters;

@Service
public class FileToPdfService {

  final Logger logger = LoggerFactory.getLogger(FileToPdfService.class);

  private final ImageConversionService imageService;
  private final DocumentConversionService documentService;
  private final AntiVirusScanConnectorImpl antiVirusScanConnector;
  private final S3ServiceImpl s3Service;

  public FileToPdfService(ImageConversionService imageService,
                          DocumentConversionService documentService,
                          AntiVirusScanConnectorImpl antiVirusScanConnector,
                          S3ServiceImpl s3Service) {
    this.imageService = imageService;
    this.documentService = documentService;
    this.antiVirusScanConnector = antiVirusScanConnector;
    this.s3Service = s3Service;
  }

  public String convertToPdf(MultipartFile file)
      throws IOException, URISyntaxException, FileConversionToPdfException {

    logger.info("Converting a file to pdf");
    Map<String, String> validImageContentTypes = new FileFormats().mapFormToImageFormats;
    Map<String, String> validDocumentContentTypes = new FileFormats().mapFormToDocumentFormats;
    Map<String, String> allValidContentTypes = new FileFormats().allAcceptedFormats();

    ConversionService service;
    if (validDocumentContentTypes.containsKey(file.getContentType())) {
      logger.info("Content type of the input file is - Document type");
      service = documentService;
    } else if (validImageContentTypes.containsKey(file.getContentType())) {
      logger.info("Content type of the input file is - Image type");
      service = imageService;
    } else if (MediaType.APPLICATION_PDF_VALUE.equalsIgnoreCase(file.getContentType())) {
      logger.info("Content type of the input file is - PDF type");
      return Base64Converters.toBase64(file.getInputStream());
    } else {
      throw new UnsupportedMediaTypeStatusException("Unsupported file type");
    }

    return service.convertToPdf(file.getInputStream(),
        allValidContentTypes.get(file.getContentType())).getContent();
  }

  public void scanForViruses(MultipartFile file) throws IOException, AvException {
    antiVirusScanConnector.scanForVirus(file);
  }

  public String upload(String userId, String convertedFile) {
    return s3Service.uploadToS3(convertedFile, userId);
  }
}

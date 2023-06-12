package uk.gov.dwp.health.atw.msevidencehandler.repository;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.atw.msevidencehandler.config.S3Properties;
import uk.gov.dwp.health.atw.msevidencehandler.services.impl.KmsServiceImpl;
import uk.gov.dwp.health.crypto.exception.CryptoException;

@Service
public class S3Repository {

  final Logger logger = LoggerFactory.getLogger(S3Repository.class);

  private final AmazonS3 amazonS3;

  private final S3Properties s3Properties;

  private final KmsServiceImpl kmsService;

  private final ObjectMapper objectMapper;

  public S3Repository(S3Properties s3Properties, AmazonS3 amazonS3, KmsServiceImpl kmsService,
                      ObjectMapper objectMapper) {
    this.s3Properties = s3Properties;
    this.amazonS3 = amazonS3;
    this.kmsService = kmsService;
    this.objectMapper = objectMapper;
  }

  public String uploadFile(UUID userId, String base64File) {
    try {
      byte[] bytesForFile = s3Properties.isEncryptionEnabled()
          ? objectMapper.writeValueAsBytes(kmsService.encrypt(base64File))
          : Base64.decodeBase64(
          (base64File.substring(base64File.indexOf(",") + 1)).getBytes(StandardCharsets.UTF_8));

      ObjectMetadata metadata = new ObjectMetadata();
      metadata.setContentLength(bytesForFile.length);
      if (!s3Properties.isEncryptionEnabled()) {
        metadata.setContentType("application/pdf");
        logger.info("File will be uploaded to S3 without encryption");
      } else {
        logger.info("File encrypted and will now upload to S3");
      }
      String key = "evidence/" + userId.toString() + "/" + UUID.randomUUID();
      amazonS3.putObject(s3Properties.getBucketName(),
          key,
          new ByteArrayInputStream(bytesForFile),
          metadata);
      logger.info("File uploaded successfully to S3");
      return key;
    } catch (SdkClientException | CryptoException | IOException ex) {
      final var msg =
          String.format("Fail to upload file to S3 bucket - %s", ex.getMessage());
      logger.error(msg);
      return null;
    }
  }

  public void deleteFile(String key) {
    logger.info("Deleting a file with a given key %s from S3", key);
    amazonS3.deleteObject(s3Properties.getBucketName(), key);
  }

  public boolean doesFileExist(String key) {
    logger.info("Checking if given file key %s exists at S3", key);
    return amazonS3.doesObjectExist(s3Properties.getBucketName(), key);
  }

  public ObjectMetadata getFileMeta(String key) {
    logger.info("Getting file meta data for the given file key %s", key);
    return amazonS3.getObjectMetadata(s3Properties.getBucketName(), key);
  }

}

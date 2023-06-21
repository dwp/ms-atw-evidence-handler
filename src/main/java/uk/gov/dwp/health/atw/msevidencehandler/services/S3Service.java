package uk.gov.dwp.health.atw.msevidencehandler.services;

import com.amazonaws.services.s3.model.ObjectMetadata;

public abstract class S3Service {
  public abstract String uploadToS3(String base64Content, String userId);

  public abstract void deleteFile(String fileKey);

  public abstract ObjectMetadata getFileMeta(String fileKey);
}

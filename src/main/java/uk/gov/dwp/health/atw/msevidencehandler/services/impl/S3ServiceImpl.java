package uk.gov.dwp.health.atw.msevidencehandler.services.impl;

import com.amazonaws.services.s3.model.ObjectMetadata;
import java.util.UUID;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.atw.msevidencehandler.models.exceptions.ResourceNotFoundException;
import uk.gov.dwp.health.atw.msevidencehandler.repository.S3Repository;
import uk.gov.dwp.health.atw.msevidencehandler.services.S3Service;

@Service
public class S3ServiceImpl extends S3Service {
  final S3Repository s3Repository;

  public S3ServiceImpl(S3Repository s3Repository) {
    this.s3Repository = s3Repository;
  }

  @Override
  public String uploadToS3(String base64Content, UUID userId) {
    return s3Repository.uploadFile(userId, base64Content);
  }

  @Override
  public void deleteFile(String fileKey) {
    s3Repository.deleteFile(fileKey);
  }

  @Override
  public ObjectMetadata getFileMeta(String fileKey) {
    if (s3Repository.doesFileExist(fileKey)) {
      return s3Repository.getFileMeta(fileKey);
    } else {
      throw new ResourceNotFoundException();
    }
  }
}


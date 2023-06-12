package uk.gov.dwp.health.atw.msevidencehandler.controllers.test;

import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.dwp.health.atw.msevidencehandler.models.exceptions.ResourceNotFoundException;
import uk.gov.dwp.health.atw.msevidencehandler.services.impl.S3ServiceImpl;

@RestController
@ConditionalOnExpression("${testOnly.endpoints.enabled}")
public class S3Controller {
  final S3ServiceImpl s3Service;

  public S3Controller(
      S3ServiceImpl s3Service
  ) {
    this.s3Service = s3Service;
  }

  @PostMapping(value = "/file-meta",
      produces = "application/json")
  public ResponseEntity<ObjectMetadata> deleteFile(
      @RequestParam("key") String fileKey) {
    try {
      ObjectMetadata meta = s3Service.getFileMeta(fileKey);
      return ResponseEntity.ok().body(meta);
    } catch (ResourceNotFoundException ex) {
      return ResponseEntity.notFound().build();
    }
  }
}

package uk.gov.dwp.health.atw.msevidencehandler.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.s3.model.ObjectMetadata;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import uk.gov.dwp.health.atw.msevidencehandler.models.exceptions.ResourceNotFoundException;
import uk.gov.dwp.health.atw.msevidencehandler.repository.S3Repository;
import uk.gov.dwp.health.atw.msevidencehandler.services.impl.S3ServiceImpl;

@SpringBootTest(classes = S3ServiceImpl.class)
public class S3ServiceTest {

  String FILE_KEY = "fileKey";

  @Autowired
  S3ServiceImpl s3Service;

  @MockBean
  S3Repository s3Repository;

  @Test
  @DisplayName("upload image file successfully - jpeg")
  void uploadImageFile() {
    String uuid = UUID.randomUUID().toString();

    when(s3Repository.uploadFile(any(), any())).thenReturn(
        FILE_KEY);

    Assertions.assertEquals(s3Service.uploadToS3("HelloWorld", uuid),
        FILE_KEY);
  }

  @Test
  @DisplayName("delete file")
  void deleteFile() {
    String uuid = UUID.randomUUID().toString();

    doNothing().when(s3Repository).deleteFile(any());

    s3Service.deleteFile(uuid);

    verify(s3Repository, times(1)).deleteFile(uuid);
  }


  @Test
  @DisplayName("file does not exist")
  void fileNotFound() {
    String uuid = UUID.randomUUID().toString();

    when(s3Repository.doesFileExist(any())).thenReturn(false);

    Assertions.assertThrows(ResourceNotFoundException.class,
        () -> s3Service.getFileMeta(uuid)
    );

    verify(s3Repository, never()).getFileMeta(any());
  }

  @Test
  @DisplayName("file does exist")
  void fileFound() {
    String uuid = UUID.randomUUID().toString();
    ObjectMetadata meta = new ObjectMetadata();
    meta.setContentType(MediaType.IMAGE_PNG_VALUE);

    when(s3Repository.doesFileExist(any())).thenReturn(true);
    when(s3Repository.getFileMeta(any())).thenReturn(meta);

    Assertions.assertEquals(s3Service.getFileMeta(uuid).getContentType(),
        MediaType.IMAGE_PNG_VALUE);
  }
}

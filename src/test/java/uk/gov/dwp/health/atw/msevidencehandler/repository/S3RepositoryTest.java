package uk.gov.dwp.health.atw.msevidencehandler.repository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.dwp.health.atw.msevidencehandler.config.S3Properties;
import uk.gov.dwp.health.atw.msevidencehandler.services.impl.KmsServiceImpl;
import uk.gov.dwp.health.crypto.CryptoMessage;
import uk.gov.dwp.health.crypto.exception.CryptoException;

@SpringBootTest(classes = S3Repository.class)
public class S3RepositoryTest {
  @Autowired
  S3Repository repository;

  @MockBean
  private AmazonS3 amazonS3;

  @MockBean
  private S3Properties s3Properties;

  @MockBean
  private KmsServiceImpl kmsService;

  @MockBean
  private ObjectMapper objectMapper;

  UUID uuid = UUID.randomUUID();
  String base64 = "base64";

  @Test
  @DisplayName("upload to S3 bucket")
  void uploadToS3() {
    when(amazonS3.putObject(any(String.class), any(String.class), any(InputStream.class),
        any(ObjectMetadata.class))).thenReturn(
        new PutObjectResult()
    );

    when(s3Properties.getBucketName()).thenReturn("bucket_name");

    Assertions.assertTrue(repository.uploadFile(uuid, base64).startsWith("evidence/" + uuid + "/"));
  }

  @Test
  @DisplayName("delete file")
  void deleteFile() {
    doNothing().when(amazonS3).deleteObject(any(), any());

    when(s3Properties.getBucketName()).thenReturn("bucket_name");

    repository.deleteFile(uuid.toString());

    verify(amazonS3, times(1)).deleteObject("bucket_name", uuid.toString());
  }

  @Test
  @DisplayName("File exists")
  void objectDoesExist() {
    when(amazonS3.doesObjectExist(any(), any())).thenReturn(true);

    when(s3Properties.getBucketName()).thenReturn("bucket_name");

    Assertions.assertTrue(repository.doesFileExist(uuid.toString()));

    verify(amazonS3, times(1)).doesObjectExist("bucket_name", uuid.toString());
  }

  @Test
  @DisplayName("File does not exists")
  void objectDoesNotExist() {
    when(amazonS3.doesObjectExist(any(), any())).thenReturn(false);

    when(s3Properties.getBucketName()).thenReturn("bucket_name");

    Assertions.assertFalse(repository.doesFileExist(uuid.toString()));

    verify(amazonS3, times(1)).doesObjectExist("bucket_name", uuid.toString());
  }

  @Test
  @DisplayName("getFileMeta")
  void getFileMeta() {
    ObjectMetadata meta = new ObjectMetadata();
    String key = "key";
    when(amazonS3.getObjectMetadata(anyString(), anyString())).thenReturn(meta);

    when(s3Properties.getBucketName()).thenReturn("bucket_name");

    Assertions.assertEquals(repository.getFileMeta(key), meta);

    verify(amazonS3, times(1)).getObjectMetadata("bucket_name", key);
  }

  @Test
  @DisplayName("Kms Encryption succeeds")
  void testSuccessfulEncryption() throws CryptoException, IOException {
    CryptoMessage cryptoMessage = mock(CryptoMessage.class);

    when(s3Properties.isEncryptionEnabled()).thenReturn(true);

    when(kmsService.encrypt(anyString())).thenReturn(cryptoMessage);

    when(objectMapper.writeValueAsBytes(cryptoMessage)).thenReturn("SGVsbG8gd29ybGQ=".getBytes());

    when(amazonS3.putObject(any(String.class), any(String.class), any(InputStream.class),
        any(ObjectMetadata.class))).thenReturn(
        new PutObjectResult());

    when(s3Properties.getBucketName()).thenReturn("bucket_name");

    repository.uploadFile(uuid, base64);

    InOrder inOrder = inOrder(kmsService, objectMapper, amazonS3);
    inOrder.verify(kmsService, times(1)).encrypt(anyString());
    inOrder.verify(objectMapper, times(1)).
        writeValueAsBytes(any(CryptoMessage.class));
    inOrder.verify(amazonS3, times(1))
        .putObject(any(String.class), any(String.class), any(InputStream.class),
            any(ObjectMetadata.class));
  }

  @Test
  @DisplayName("Encryption throws CryptoException")
  void testEncryptionThrowsCryptoException() throws CryptoException {
    when(s3Properties.isEncryptionEnabled()).thenReturn(true);

    when(kmsService.encrypt(anyString())).thenThrow(CryptoException.class);

    Assertions.assertEquals(null, repository.uploadFile(uuid, base64));
  }

  @Test
  @DisplayName("Encryption throws IOException")
  void testEncryptionThrowsIOException() throws CryptoException, JsonProcessingException {
    CryptoMessage cryptoMessage = mock(CryptoMessage.class);

    when(s3Properties.isEncryptionEnabled()).thenReturn(true);

    when(kmsService.encrypt(anyString())).thenReturn(cryptoMessage);

    when(objectMapper.writeValueAsBytes(cryptoMessage)).thenThrow(JsonProcessingException.class);

    Assertions.assertEquals(null, repository.uploadFile(uuid, base64));
  }
}

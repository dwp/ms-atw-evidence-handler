package uk.gov.dwp.health.atw.msevidencehandler.models;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;

public class FileNameAwareByteArrayResourceTest {

  @Test
  void testGetName() {
    FileNameAwareByteArrayResource res =
        new FileNameAwareByteArrayResource("fileName", "testString".getBytes(
            StandardCharsets.UTF_8));

    Assertions.assertEquals("fileName", res.getFilename());
  }

  @Test
  void testNotEqualsByFileName() {
    FileNameAwareByteArrayResource one =
        new FileNameAwareByteArrayResource("fileName", "testString".getBytes(
            StandardCharsets.UTF_8));
    FileNameAwareByteArrayResource two =
        new FileNameAwareByteArrayResource("differentName", "testString".getBytes(
            StandardCharsets.UTF_8));

    Assertions.assertNotEquals(one, two);
  }

  @Test
  void testEqualsByFileName() {
    FileNameAwareByteArrayResource one =
        new FileNameAwareByteArrayResource("fileName", "testString".getBytes(
            StandardCharsets.UTF_8));
    FileNameAwareByteArrayResource two =
        new FileNameAwareByteArrayResource("fileName", "testString".getBytes(
            StandardCharsets.UTF_8));

    Assertions.assertEquals(one, two);
  }

  @Test
  void testNotEqualsByBytes() {
    FileNameAwareByteArrayResource one =
        new FileNameAwareByteArrayResource("fileName", "different".getBytes(
            StandardCharsets.UTF_8));
    FileNameAwareByteArrayResource two =
        new FileNameAwareByteArrayResource("fileName", "testString".getBytes(
            StandardCharsets.UTF_8));

    Assertions.assertNotEquals(one, two);
  }

  @Test
  void testEqualsByBytes() {
    FileNameAwareByteArrayResource one =
        new FileNameAwareByteArrayResource("fileName", "testString".getBytes(
            StandardCharsets.UTF_8));
    FileNameAwareByteArrayResource two =
        new FileNameAwareByteArrayResource("fileName", "testString".getBytes(
            StandardCharsets.UTF_8));

    Assertions.assertEquals(one, two);
  }

  @Test
  void testNotEqualsDiffTypes() {
    FileNameAwareByteArrayResource one =
        new FileNameAwareByteArrayResource("fileName", "testString".getBytes(
            StandardCharsets.UTF_8));

    Assertions.assertNotEquals(one, new ByteArrayResource("testString".getBytes(
        StandardCharsets.UTF_8)));
  }

  @Test
  void testEqualsItself() {
    FileNameAwareByteArrayResource one =
        new FileNameAwareByteArrayResource("fileName", "testString".getBytes(
            StandardCharsets.UTF_8));

    Assertions.assertEquals(one, one);
  }

  @Test
  void testHashcode() {
    FileNameAwareByteArrayResource one =
        new FileNameAwareByteArrayResource("fileName", "testString".getBytes(
            StandardCharsets.UTF_8));

    FileNameAwareByteArrayResource two =
        new FileNameAwareByteArrayResource("fileName", "testString".getBytes(
            StandardCharsets.UTF_8));

    Assertions.assertEquals(one.hashCode(), two.hashCode());
  }
}

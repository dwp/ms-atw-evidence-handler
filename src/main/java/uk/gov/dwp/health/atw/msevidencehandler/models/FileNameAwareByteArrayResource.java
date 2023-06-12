package uk.gov.dwp.health.atw.msevidencehandler.models;

import java.util.Objects;
import org.springframework.core.io.ByteArrayResource;

public class FileNameAwareByteArrayResource extends ByteArrayResource {

  private final String fileName;

  public FileNameAwareByteArrayResource(String fileName, byte[] byteArray) {
    super(byteArray);
    this.fileName = fileName;
  }

  @Override
  public String getFilename() {
    return fileName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof FileNameAwareByteArrayResource)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    FileNameAwareByteArrayResource that = (FileNameAwareByteArrayResource) o;
    return fileName.equals(that.fileName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), fileName);
  }
}
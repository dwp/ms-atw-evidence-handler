package uk.gov.dwp.health.atw.msevidencehandler.utils;


import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Base64Converters {
  public static String toBase64(InputStream inputStream) throws IOException {
    byte[] bytes = inputStream.readAllBytes();
    return Base64.getEncoder().encodeToString(bytes);
  }
}

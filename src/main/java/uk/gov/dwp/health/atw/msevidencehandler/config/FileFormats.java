package uk.gov.dwp.health.atw.msevidencehandler.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.http.MediaType;

public class FileFormats {

  private static final List<String> acceptedImageFormatsForConversionService =
      Arrays.asList("img/jpeg", "img/jpg", "img/png");

  private static final List<String> acceptedFormImageContentTypes =
      Arrays.asList(MediaType.IMAGE_JPEG_VALUE, "image/jpg", MediaType.IMAGE_PNG_VALUE);

  public final Map<String, String> mapFormToImageFormats =
      IntStream.range(0, acceptedFormImageContentTypes.size())
          .boxed()
          .collect(Collectors.toMap(acceptedFormImageContentTypes::get,
              acceptedImageFormatsForConversionService::get));

  private static final List<String> acceptedDocumentFormatsForConversionService =
      Arrays.asList("doc", "docx");

  private static final List<String> acceptedDocumentContentTypes =
      Arrays.asList("application/msword",
          "application/vnd.openxmlformats-officedocument.wordprocessingml.document");

  public final Map<String, String> mapFormToDocumentFormats =
      IntStream.range(0, acceptedDocumentContentTypes.size())
          .boxed()
          .collect(Collectors.toMap(acceptedDocumentContentTypes::get,
              acceptedDocumentFormatsForConversionService::get));

  public final Map<String, String> allAcceptedFormats() {
    Map<String, String> all = new HashMap<>();
    all.putAll(mapFormToImageFormats);
    all.putAll(mapFormToDocumentFormats);
    return all;
  }
}






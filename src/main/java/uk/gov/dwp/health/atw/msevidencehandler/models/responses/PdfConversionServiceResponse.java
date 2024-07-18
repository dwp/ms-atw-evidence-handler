package uk.gov.dwp.health.atw.msevidencehandler.models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PdfConversionServiceResponse {

  @JsonProperty("docId")
  @NotNull
  @NonNull
  public String docId;

  @JsonProperty("content_type")
  @NotNull
  @NonNull
  public String contentType;

  @JsonProperty("content")
  @NotNull
  @NonNull
  public String content;
}
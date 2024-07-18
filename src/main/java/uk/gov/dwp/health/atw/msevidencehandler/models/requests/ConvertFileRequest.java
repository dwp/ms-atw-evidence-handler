package uk.gov.dwp.health.atw.msevidencehandler.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ConvertFileRequest {

  @JsonProperty("docId")
  @NotNull
  @NonNull
  private String docId;

  @JsonProperty("content_type")
  @NotNull
  @NonNull
  private String contentType;

  @JsonProperty("content")
  @NotNull
  @NonNull
  private String content;
}

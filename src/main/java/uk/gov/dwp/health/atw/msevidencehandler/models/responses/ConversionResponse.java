package uk.gov.dwp.health.atw.msevidencehandler.models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ConversionResponse {
  @JsonProperty("uploadedFileKeys")
  @NotNull
  @NonNull
  private String uploadedFileKeys;
}
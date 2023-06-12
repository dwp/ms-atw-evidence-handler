package uk.gov.dwp.health.atw.msevidencehandler.models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class AvServiceResponse {
  @JsonProperty("s3Ref")
  private String s3Ref;

  @JsonProperty("bucket")
  private String bucket;

  @JsonProperty("fileSizeKb")
  private Integer fileSizeKb;

}

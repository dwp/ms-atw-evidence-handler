package uk.gov.dwp.health.atw.msevidencehandler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class MsEvidenceHandlerApplication {
  public static void main(final String[] args) {
    SpringApplication.run(MsEvidenceHandlerApplication.class, args);
  }

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.requestFactory(SimpleClientHttpRequestFactory.class).build();
  }
}

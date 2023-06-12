package uk.gov.dwp.health.atw.msevidencehandler.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import uk.gov.dwp.health.atw.msevidencehandler.models.ServiceConfig;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "services")
@Setter
@Getter
public class ApplicationConfig {
  private ServiceConfig img2pdf;
  private ServiceConfig word2pdf;
  private ServiceConfig clamAvLander;
}

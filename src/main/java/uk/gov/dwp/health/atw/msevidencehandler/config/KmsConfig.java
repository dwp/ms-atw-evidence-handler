package uk.gov.dwp.health.atw.msevidencehandler.config;

import com.amazonaws.regions.Regions;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.dwp.health.atw.msevidencehandler.config.properties.CryptoConfigProperties;
import uk.gov.dwp.health.atw.msevidencehandler.exceptions.CryptoConfigException;
import uk.gov.dwp.health.crypto.CryptoConfig;
import uk.gov.dwp.health.crypto.CryptoDataManager;
import uk.gov.dwp.health.crypto.exception.CryptoException;

@Slf4j
@Configuration
public class KmsConfig {

  @SneakyThrows
  @Bean
  public CryptoConfig cryptoConfig(final CryptoConfigProperties properties,
                                   S3Properties s3Properties) {
    log.info("Getting KMS Data Key and KMS Override values");
    var config = new CryptoConfig(properties.getDataKey());
    if (properties.getKmsOverride() != null && !properties.getKmsOverride().isBlank()) {
      config.setKmsEndpointOverride(properties.getKmsOverride());
      config.setRegion(Regions.fromName(s3Properties.getAwsRegion()));
    }
    return config;
  }

  @SneakyThrows
  @Autowired
  @Bean
  public CryptoDataManager cryptoDataManager(final CryptoConfig cryptoConfig) {
    try {
      return new CryptoDataManager(cryptoConfig);
    } catch (CryptoException ex) {
      final String msg = String.format("kms crypto config error %s", ex.getMessage());
      log.error(msg);
      throw new CryptoConfigException(msg);
    }
  }
}

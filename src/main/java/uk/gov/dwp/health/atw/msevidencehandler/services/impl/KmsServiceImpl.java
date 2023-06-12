package uk.gov.dwp.health.atw.msevidencehandler.services.impl;

import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.atw.msevidencehandler.services.Encryption;
import uk.gov.dwp.health.crypto.CryptoDataManager;
import uk.gov.dwp.health.crypto.CryptoMessage;
import uk.gov.dwp.health.crypto.exception.CryptoException;

@Slf4j
@Service
@RequiredArgsConstructor
public class KmsServiceImpl implements Encryption<String, CryptoMessage> {

  private final CryptoDataManager cryptoDataManager;

  @Override
  public CryptoMessage encrypt(final String content) throws CryptoException {
    log.info("Encrypting file before uploading to S3");
    return cryptoDataManager.encrypt(content);
  }

  public byte[] decrypt(CryptoMessage cryptoMessage) throws CryptoException {
    try {
      log.info("Decrypt file");
      return Base64.getDecoder().decode(cryptoDataManager.decrypt(cryptoMessage));
    } catch (CryptoException e) {
      final String message = String.format("Fail decrypt file content with KMS  %s",
          e.getMessage());
      log.error(message);
      throw new CryptoException(message);
    }
  }
}

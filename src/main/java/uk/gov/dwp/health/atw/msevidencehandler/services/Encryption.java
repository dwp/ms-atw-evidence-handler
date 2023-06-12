package uk.gov.dwp.health.atw.msevidencehandler.services;

import uk.gov.dwp.health.crypto.exception.CryptoException;

@FunctionalInterface
public interface Encryption<T, S> {
  S encrypt(T clear) throws CryptoException;
}

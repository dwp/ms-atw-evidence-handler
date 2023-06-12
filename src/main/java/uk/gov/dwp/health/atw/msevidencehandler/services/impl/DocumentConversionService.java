package uk.gov.dwp.health.atw.msevidencehandler.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.atw.msevidencehandler.connector.impl.File2PdfConnector;
import uk.gov.dwp.health.atw.msevidencehandler.services.ConversionService;

@Service
public class DocumentConversionService extends ConversionService {
  @Autowired
  public DocumentConversionService(File2PdfConnector connector) {
    super(connector);
  }
}

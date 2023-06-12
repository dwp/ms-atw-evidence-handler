package uk.gov.dwp.health.atw.msevidencehandler.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.atw.msevidencehandler.connector.impl.Img2PdfConnector;
import uk.gov.dwp.health.atw.msevidencehandler.services.ConversionService;

@Service
public class ImageConversionService extends ConversionService {
  @Autowired
  public ImageConversionService(Img2PdfConnector connector) {
    super(connector);
  }
}

package uk.gov.dwp.health.atw.msevidencehandler.connector;

import java.io.IOException;
import java.net.URISyntaxException;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.dwp.health.atw.msevidencehandler.models.exceptions.av.AvException;
import uk.gov.dwp.health.atw.msevidencehandler.models.responses.AvServiceResponse;

public abstract class AntiVirusScanConnector {

  public abstract AvServiceResponse scanForVirus(MultipartFile file)
      throws URISyntaxException, IOException, AvException;

}

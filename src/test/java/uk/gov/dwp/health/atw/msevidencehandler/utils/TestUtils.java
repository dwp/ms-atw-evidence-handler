package uk.gov.dwp.health.atw.msevidencehandler.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestUtils {
  public static String asJsonString(final Object obj) {
    final Logger logger = LoggerFactory.getLogger(TestUtils.class);

    try {
      String json = new ObjectMapper().writeValueAsString(obj);
      return json;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

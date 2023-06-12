package uk.gov.dwp.health.atw.msevidencehandler.controllers.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.dwp.health.atw.msevidencehandler.utils.TestUtils.asJsonString;

import com.amazonaws.services.s3.model.ObjectMetadata;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import uk.gov.dwp.health.atw.msevidencehandler.controllers.utils.ServiceExceptionHandler;
import uk.gov.dwp.health.atw.msevidencehandler.models.exceptions.ResourceNotFoundException;
import uk.gov.dwp.health.atw.msevidencehandler.services.impl.S3ServiceImpl;

@EnableWebMvc
@AutoConfigureMockMvc
@SpringBootTest(classes = {S3Controller.class})
@ImportAutoConfiguration(ServiceExceptionHandler.class)
public class S3ControllerTest {

  String uuid = UUID.randomUUID().toString();

  @MockBean
  S3ServiceImpl service;

  @Autowired
  private MockMvc mockMvc;


  @DisplayName("returns file meta when found")
  @Test
  public void returnFileMeta() throws Exception {
    ObjectMetadata meta = new ObjectMetadata();
    when(service.getFileMeta(any())).thenReturn(meta);

    mockMvc.perform(multipart("/file-meta")
            .param("key", uuid)).andExpect(status().isOk())
        .andExpect(content().json(asJsonString(meta)));
  }

  @DisplayName("returns 404 as file not found")
  @Test
  public void returnFileNotFound() throws Exception {
    when(service.getFileMeta(any())).thenThrow(new ResourceNotFoundException());

    mockMvc.perform(multipart("/file-meta")
        .param("key", uuid)).andExpect(status().isNotFound());
  }
}

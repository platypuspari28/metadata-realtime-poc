package com.example.metadata.ingestion;

import com.example.metadata.common.model.MetadataEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(MonteCarloIngestor.class)
public class MonteCarloIngestorTest {

  @Autowired
  private MockMvc mockMvc;

  // Create a mock for the KafkaTemplate
  @MockBean
  private KafkaTemplate<String, MetadataEvent> kafkaTemplate;

  @Test
  public void testReceiveEvent() throws Exception {
    String requestBody = "{\"tableId\":\"table_123\",\"issueType\":\"data_freshness\",\"severity\":\"high\"}";

    // Perform POST request to the controller
    mockMvc.perform(post("/api/monte-carlo/event")
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isOk());

    // Capture the MetadataEvent object passed to KafkaTemplate.send(...)
    ArgumentCaptor<MetadataEvent> captor = ArgumentCaptor.forClass(MetadataEvent.class);
    verify(kafkaTemplate, times(1)).send(any(String.class), captor.capture());

    MetadataEvent sentEvent = captor.getValue();

    // Verify that the event has been constructed properly.
    assertNotNull("Event ID should not be null", sentEvent.getEventId());
    assertEquals("Source system is incorrect", "MonteCarlo", sentEvent.getSourceSystem());
    assertEquals("Asset ID is incorrect", "table_123", sentEvent.getAssetId());

    // Verify the payload
    Map<String, Object> payload = sentEvent.getPayload();
    assertNotNull("Payload should not be null", payload);
    assertEquals("Issue type in payload is incorrect", "data_freshness", payload.get("issueType"));
    assertEquals("Severity in payload is incorrect", "high", payload.get("severity"));
  }
}

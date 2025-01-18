package com.example.metadata.ingestion;

import com.example.metadata.common.model.MetadataEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/monte-carlo")
public class MonteCarloIngestor {

  private static final String INBOUND_TOPIC = "metadata.inbound";

  @Autowired
  private KafkaTemplate<String, MetadataEvent> kafkaTemplate;

  /**
   * Receives inbound metadata events (via HTTP POST) from Monte Carlo or similar systems,
   * wraps them in a MetadataEvent, and publishes to Kafka.
   *
   * @param payload A map of the incoming JSON payload
   * @return Confirmation string with the generated eventId
   */
  @PostMapping("/event")
  public String receiveEvent(@RequestBody Map<String, Object> payload) {
    // Create a new MetadataEvent
    MetadataEvent event = new MetadataEvent();
    event.setEventId(UUID.randomUUID().toString());
    event.setSourceSystem("MonteCarlo");
    event.setAssetId(payload.getOrDefault("tableId", "unknown").toString());
    event.setPayload(payload);

    // Publish the event to the inbound Kafka topic
    kafkaTemplate.send(INBOUND_TOPIC, event);

    return "Received event with ID: " + event.getEventId();
  }
}

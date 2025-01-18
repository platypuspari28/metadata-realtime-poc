package com.example.metadata.outbound;

import com.example.metadata.common.model.MetadataEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class DataGovernanceNotifier {

  /**
   * Consumes MetadataEvent messages from the 'metadata.outbound' Kafka topic.
   * Checks if the 'classification' in the payload is "PII", then triggers
   * appropriate governance enforcement.
   */
  @KafkaListener(topics = "metadata.outbound", groupId = "outbound-notifier-group")
  public void onMetadataChange(ConsumerRecord<String, MetadataEvent> record) {
    MetadataEvent event = record.value();
    System.out.println("Outbound notifier received: " + event);

    // Checking classification from the event payload
    String classification = (String) event.getPayload().get("classification");
    if ("PII".equalsIgnoreCase(classification)) {
      // Performing external API call or any custom logic to enforce data governance downstream
      enforceDataGovernance(event);
    }
  }

  /**
   * Stubbed example method to demonstrate calling out to a data governance platform
   * or any external system to enforce rules (e.g. row-level security for PII).
   */
  private void enforceDataGovernance(MetadataEvent event) {
    System.out.println("Enforcing governance for PII on asset: " + event.getAssetId());
  }
}

package com.example.metadata.processing;

import com.example.metadata.common.model.MetadataEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MetadataProcessor {

  /**
   * Listens for MetadataEvent messages on the 'metadata.inbound' Kafka topic,
   * applies any transformations, and persists the result (stubbed out here).
   */
  @KafkaListener(topics = "metadata.inbound", groupId = "metadata-processor-group")
  public void processEvent(ConsumerRecord<String, MetadataEvent> record) {
    MetadataEvent event = record.value();
    System.out.println("Processing inbound event: " + event);

    // Example transformation: add a processing timestamp & default classification
    event.getPayload().put("processedTimestamp", System.currentTimeMillis());
    event.getPayload().put("atlanClassified", "UNCLASSIFIED");

    // Persist or forward the updated event
    storeInMetadataDB(event);
  }

  /**
   * Dummy method to mimic storing the event in a metadata database.
   * Replace with actual database calls as needed.
   */
  private void storeInMetadataDB(MetadataEvent event) {
    System.out.println("Persisting event to metadata store for asset: " + event.getAssetId());
    // e.g. someDBClient.save(event);
  }
}

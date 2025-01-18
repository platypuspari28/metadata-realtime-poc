package com.example.metadata.processing;

import static org.junit.Assert.*;

import com.example.metadata.common.model.MetadataEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for MetadataProcessor to verify event processing.
 */
public class MetadataProcessorTest {

  @Test
  public void testProcessEvent() {
    // Creating a sample payload map for the event
    Map<String, Object> payload = new HashMap<>();
    payload.put("initialKey", "initialValue");

    // Building a MetadataEvent object and set its fields.
    MetadataEvent event = new MetadataEvent();
    event.setEventId("testEventId");
    event.setSourceSystem("TestSource");
    event.setAssetId("table123");
    event.setPayload(payload);

    // Creating a ConsumerRecord containing the MetadataEvent.
    ConsumerRecord<String, MetadataEvent> record =
        new ConsumerRecord<>("metadata.inbound", 0, 0L, "dummyKey", event);

    // Creating an instance of MetadataProcessor (without Spring context)
    MetadataProcessor processor = new MetadataProcessor();
    processor.processEvent(record);

    // After processing, the event's payload should contain the additional fields.
    Map<String, Object> updatedPayload = event.getPayload();
    assertTrue("Payload should contain processedTimestamp",
        updatedPayload.containsKey("processedTimestamp"));
    assertEquals("Asset classification is incorrect",
        "UNCLASSIFIED", updatedPayload.get("atlanClassified"));
  }
}

package com.example.metadata.common.model;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class MetadataEventTest {

  @Test
  public void testNoArgsConstructorAndSetters() {
    // Create an instance with no-arg constructor
    MetadataEvent event = new MetadataEvent();

    // Create a sample payload and set values using setters
    Map<String, Object> payload = new HashMap<>();
    payload.put("key1", "value1");

    event.setEventId("testEventId");
    event.setSourceSystem("TestSource");
    event.setAssetId("asset123");
    event.setPayload(payload);

    // Verify that values are set correctly
    assertEquals("testEventId", event.getEventId());
    assertEquals("TestSource", event.getSourceSystem());
    assertEquals("asset123", event.getAssetId());
    assertNotNull(event.getPayload());
    assertEquals("value1", event.getPayload().get("key1"));
  }

  @Test
  public void testAllArgsConstructor() {
    // Create a sample payload
    Map<String, Object> payload = new HashMap<>();
    payload.put("key2", "value2");

    // Create an instance using the all-args constructor
    MetadataEvent event = new MetadataEvent("id123", "SourceA", "asset456", payload);

    // Verify that the values are correctly initialized
    assertEquals("id123", event.getEventId());
    assertEquals("SourceA", event.getSourceSystem());
    assertEquals("asset456", event.getAssetId());
    assertNotNull(event.getPayload());
    assertEquals("value2", event.getPayload().get("key2"));
  }

  @Test
  public void testToStringContainsFieldValues() {
    // Create a sample payload
    Map<String, Object> payload = new HashMap<>();
    payload.put("sampleKey", "sampleValue");

    // Create an instance with known values
    MetadataEvent event = new MetadataEvent("id789", "SourceB", "asset789", payload);

    // Get the string representation
    String eventString = event.toString();

    // Verify the toString() output contains the expected field values
    assertTrue(eventString.contains("id789"));
    assertTrue(eventString.contains("SourceB"));
    assertTrue(eventString.contains("asset789"));
    assertTrue(eventString.contains("sampleKey"));
    assertTrue(eventString.contains("sampleValue"));
  }
}
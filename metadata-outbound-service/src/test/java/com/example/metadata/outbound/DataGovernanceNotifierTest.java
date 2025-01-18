package com.example.metadata.outbound;

import com.example.metadata.common.model.MetadataEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class DataGovernanceNotifierTest {

  private DataGovernanceNotifier notifier;
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;

  @Before
  public void setUp() {
    // Instantiating the class under test.
    notifier = new DataGovernanceNotifier();
    // Redirecting System.out to capture console output.
    System.setOut(new PrintStream(outContent));
  }

  @After
  public void tearDown() {
    // Restoring original System.out after test.
    System.setOut(originalOut);
  }

  @Test
  public void testOnMetadataChange_WithPII() {
    // Creating a payload with a "PII" classification.
    Map<String, Object> payload = new HashMap<>();
    payload.put("classification", "PII");

    // Creating a MetadataEvent instance.
    MetadataEvent event = new MetadataEvent("event123", "Atlan", "table123", payload);

    // Creating a dummy ConsumerRecord with this event.
    ConsumerRecord<String, MetadataEvent> record =
        new ConsumerRecord<>("metadata.outbound", 0, 0L, "dummyKey", event);

    // Invoking the method under test.
    notifier.onMetadataChange(record);

    // Converting captured output to string.
    String output = outContent.toString();

    // Verifying that the output contains the expected enforcement message.
    assertTrue("Expected enforcement message should be present",
        output.contains("Enforcing governance for PII on asset: table123"));
  }

  @Test
  public void testOnMetadataChange_WithNonPII() {
    // Creating a payload with a classification other than "PII".
    Map<String, Object> payload = new HashMap<>();
    payload.put("classification", "NON-PII");

    // Creating a MetadataEvent instance.
    MetadataEvent event = new MetadataEvent("event456", "Atlan", "table456", payload);

    // Creating a dummy ConsumerRecord with this event.
    ConsumerRecord<String, MetadataEvent> record =
        new ConsumerRecord<>("metadata.outbound", 0, 0L, "dummyKey", event);

    // Clearing the previously captured output.
    outContent.reset();

    // Invoking the method under test.
    notifier.onMetadataChange(record);

    // Converting captured output to string.
    String output = outContent.toString();

    // For a NON-PII classification, the enforcement method won't log any message.
    assertFalse("Enforcement message should NOT be present",
        output.contains("Enforcing governance for PII"));
  }
}

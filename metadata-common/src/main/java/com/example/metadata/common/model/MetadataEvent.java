package com.example.metadata.common.model;

import java.util.Map;

public class MetadataEvent {

  private String eventId;
  private String sourceSystem;
  private String assetId;
  private Map<String, Object> payload;

  // Default constructor
  public MetadataEvent() {
  }

  // Convenience constructor
  public MetadataEvent(String eventId, String sourceSystem, String assetId, Map<String, Object> payload) {
    this.eventId = eventId;
    this.sourceSystem = sourceSystem;
    this.assetId = assetId;
    this.payload = payload;
  }

  public String getEventId() {
    return eventId;
  }

  public void setEventId(String eventId) {
    this.eventId = eventId;
  }

  public String getSourceSystem() {
    return sourceSystem;
  }

  public void setSourceSystem(String sourceSystem) {
    this.sourceSystem = sourceSystem;
  }

  public String getAssetId() {
    return assetId;
  }

  public void setAssetId(String assetId) {
    this.assetId = assetId;
  }

  public Map<String, Object> getPayload() {
    return payload;
  }

  public void setPayload(Map<String, Object> payload) {
    this.payload = payload;
  }

  @Override
  public String toString() {
    return "MetadataEvent{" +
        "eventId='" + eventId + '\'' +
        ", sourceSystem='" + sourceSystem + '\'' +
        ", assetId='" + assetId + '\'' +
        ", payload=" + payload +
        '}';
  }
}

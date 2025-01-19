![image](https://github.com/user-attachments/assets/4d3fa289-b1d4-48fe-893d-774848e2e281)

# A Lily

This README provides a comprehensive guide, from setting up dependencies and running services to testing sample inbound and outbound scenarios, so that any new developer or user will understand how to build, run, and extend the project.

## Metadata Realtime POC

A near-real-time metadata ingestion and consumption system for Lily. This project demonstrates how to ingest metadata from external sources (e.g., Monte Carlo alerts) and propagate them both internally (processing) and externally (for downstream consumers) using Apache Kafka and Spring Boot.

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Setup and Configuration](#setup-and-configuration)
- [Running the Services](#running-the-services)
- [Testing Inbound and Outbound Scenarios](#testing-inbound-and-outbound-scenarios)
- [Unit Tests](#unit-tests)
- [License](#license)

## Overview

The Metadata Realtime POC project provides the following capabilities:

- **Inbound Metadata Ingestion:**  
  A REST endpoint receives real-time alerts (e.g., data quality issues from Monte Carlo) and publishes metadata events to a Kafka topic.

- **Metadata Processing:**  
  A service listens to the inbound Kafka topic, applies transformations (e.g., adding timestamps, default classifications), and persists or forwards the metadata.

- **Outbound Metadata Propagation:**  
  When metadata is updated (e.g., classified as "PII"), an outbound service listens on a different Kafka topic and calls external systems to enforce data governance.

## Architecture

          External Systems (Monte Carlo, etc.)
                     |
                     v
         +------------------------+
         |  Ingestion Service     |   <-- Receives HTTP POST events
         |  (Spring Boot REST)    |
         +------------------------+
                     |
                     v
           [Kafka Topic: metadata.inbound]
                     |
                     v
         +------------------------+
         |  Processing Service    |   <-- Consumes events, transforms payload,
         |  (Kafka Listener)      |       persists/forwards them.
         +------------------------+
                     |
                     v
           [Kafka Topic: metadata.outbound]
                     |
                     v
         +------------------------+
         |  Outbound Service      |   <-- Consumes outbound events and
         |  (Data Governance)     |       enforces data governance.
         +------------------------+


## Prerequisites

- Java 8
- Maven
- Git
- Docker & Docker Compose (for running Kafka and Zookeeper)


## Project Structure
```
metadata-realtime-poc/
├── metadata-common/           # Shared POJOs (e.g., MetadataEvent.java)
├── metadata-ingestion-service/  # Ingestion service (REST endpoint, Kafka producer)
├── metadata-processing-service/ # Processing service (Kafka consumer/transformer)
├── metadata-outbound-service/   # Outbound service (Kafka consumer for governance)
└── pom.xml                    # Root Maven POM with modules
```

## Setup and Configuration

  ### _1.) Kafka with Docker Compose_
  
  Create a docker-compose.yml file with the following content:

  ```
  version: '3.7'
  services:
    zookeeper:
      image: confluentinc/cp-zookeeper:6.2.1
      environment:
        ZOOKEEPER_CLIENT_PORT: 2181
        ZOOKEEPER_TICK_TIME: 2000
      ports:
        - "2181:2181"
    kafka:
      image: confluentinc/cp-kafka:6.2.1
      depends_on:
        - zookeeper
      environment:
        KAFKA_BROKER_ID: 1
        KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
        KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT
        KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
        KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      ports:
        - "9092:9092"
  ```
  
  Start Kafka and Zookeeper with: 
  ```docker-compose up -d```
  
  ### _2.) Application Properties_
  
  Each service (ingestion, processing, outbound) has its own src/main/resources/application.properties file. Make sure the following properties are set appropriately. For example, for the metadata-ingestion-service:

  ```
  server.port=8081
  spring.kafka.bootstrap-servers=localhost:9092

  # Producer configuration for sending MetadataEvent as JSON
  spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
  spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer

  # (Optional) Consumer configuration for testing
  spring.kafka.consumer.auto-offset-reset=earliest
  spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
  spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer
  spring.kafka.consumer.properties.spring.json.trusted.packages=com.example.metadata.common.model
  ```

## Running the Services

  Open three different terminal windows (or tabs). Build the project from the root directory:
  ```
  mvn clean install
  ```

  Then run each service:
  
  - **Ingestion Service:**
  ```  
  cd metadata-ingestion-service
  mvn spring-boot:run
  ```

  - **Processing Service:**
  ```
  cd metadata-processing-service
  mvn spring-boot:run
  ```
  
  - **Outbound Service:**
  ```
  cd metadata-outbound-service
  mvn spring-boot:run
  ```

  Watch the console logs to ensure each service starts and connects to Kafka.

## Testing Inbound and Outbound Scenarios

  ### - _Inbound Scenario_
  
  You can simulate an inbound metadata event by sending a POST request to the ingestion service.

  ```
  curl -X POST http://localhost:8081/api/monte-carlo/event \
       -H "Content-Type: application/json" \
       -d '{
             "tableId": "table_123",
             "issueType": "data_freshness",
             "severity": "high"
           }'
  ```

  #### _Expected Behavior:_
  
  - Ingestion Service logs the receipt of the event and publishes a MetadataEvent to the Kafka topic metadata.inbound.
  - Processing Service (listening to metadata.inbound) picks up the event, transforms the payload (adding, for example, a processedTimestamp and an "lilyClassified": "UNCLASSIFIED" field), and (if configured) writes to the metadata store or publishes an outbound event.
  
  ### - _Outbound Scenario_
  
  Simulate a metadata update that triggers data governance enforcement. For example, if an event is classified as "PII", the outbound service should process it.
  
  **_Option A: Manually Push an Event via Kafka CLI_**
  
  Use the Kafka console producer:
  ```
  kafka-console-producer.sh --broker-list localhost:9092 --topic metadata.outbound
  ```
  
  Then, paste the following JSON (all on one line):
  ```
  {"eventId":"test-456","sourceSystem":"Atlan","assetId":"table_abc","payload":{"classification":"PII"}}
  ```
  
  #### _Expected Behavior:_
  
  - Outbound Service (listening on metadata.outbound) picks up the event.
  - It detects that the payload’s classification is "PII" and calls its governance enforcement mechanism (which, for demo purposes, logs the action).
  
  **_Option B: Programmatically Produce the Event_** 
  
  If the processing service is set up to produce outbound events under certain conditions, simply triggering that path (for example, via an inbound test that results in a classification change to "PII") will cause the outbound service to receive and process the event.


## Unit Tests

All modules include JUnit tests. You can run: ```mvn test``` from the root or within each module to verify functionality.

## License

This project is licensed under the MIT License. See the LICENSE file for details.

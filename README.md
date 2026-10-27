# Smart Delivery Route Planner

A Java 17 project for Assignment 5 in Software Design Patterns. It will compare delivery routes using interchangeable algorithms implemented with the Strategy Pattern.

## Current status

The first step provides the Maven project and a validated delivery domain model. Route strategies, planning, comparison, and the console demonstration will be added in later commits.

## Requirements

- JDK 17 or newer (developed with JDK 27)
- Maven

## Run tests

```powershell
mvn test
```

Maven compiles with Java 17 language and bytecode compatibility through `maven.compiler.release`, even when the local JDK is newer.

## Domain model

- `Location` stores a name and geographic coordinates.
- `Route` stores endpoints, distance, estimated time, base cost, estimated emissions, traffic delay, and availability.
- `DeliveryRequest` stores a priority, maximum budget, delivery deadline, and environmental preference.
- `DeliveryPriority` defines urgent, economy, eco, and standard requests.

Invalid domain values are rejected when records are created. Route selection rules will be introduced with the strategies.

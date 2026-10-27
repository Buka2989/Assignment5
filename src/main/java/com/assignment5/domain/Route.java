package com.assignment5.domain;

import java.util.Objects;

public record Route(
        String id,
        Location origin,
        Location destination,
        double distanceKm,
        int estimatedMinutes,
        double baseCost,
        double carbonEmissionKg,
        int trafficDelayMinutes,
        boolean available
) {
    public Route {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Route ID is required");
        }
        Objects.requireNonNull(origin, "Route origin is required");
        Objects.requireNonNull(destination, "Route destination is required");
        if (!Double.isFinite(distanceKm) || distanceKm < 0) {
            throw new IllegalArgumentException("Distance must be non-negative and finite");
        }
        if (estimatedMinutes < 0) {
            throw new IllegalArgumentException("Estimated time must be non-negative");
        }
        if (!Double.isFinite(baseCost) || baseCost < 0) {
            throw new IllegalArgumentException("Base cost must be non-negative and finite");
        }
        if (!Double.isFinite(carbonEmissionKg) || carbonEmissionKg < 0) {
            throw new IllegalArgumentException("Carbon emissions must be non-negative and finite");
        }
        if (trafficDelayMinutes < 0) {
            throw new IllegalArgumentException("Traffic delay must be non-negative");
        }
    }
}

package com.assignment5.domain;

import java.util.Objects;

public record DeliveryRequest(
        DeliveryPriority priority,
        double maxBudget,
        int maxDeliveryMinutes,
        boolean ecoPreferred
) {
    public DeliveryRequest {
        Objects.requireNonNull(priority, "Delivery priority is required");
        if (!Double.isFinite(maxBudget) || maxBudget < 0) {
            throw new IllegalArgumentException("Maximum budget must be non-negative and finite");
        }
        if (maxDeliveryMinutes < 0) {
            throw new IllegalArgumentException("Maximum delivery time must be non-negative");
        }
    }
}

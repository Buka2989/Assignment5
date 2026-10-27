package com.assignment5.selector;

import com.assignment5.domain.DeliveryPriority;
import com.assignment5.domain.DeliveryRequest;
import com.assignment5.strategy.RouteStrategy;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class StrategySelector {
    private final Map<DeliveryPriority, RouteStrategy> strategies;

    public StrategySelector(Map<DeliveryPriority, RouteStrategy> strategies) {
        Objects.requireNonNull(strategies, "Strategy mapping is required");
        this.strategies = new EnumMap<>(DeliveryPriority.class);
        for (Map.Entry<DeliveryPriority, RouteStrategy> entry : strategies.entrySet()) {
            this.strategies.put(Objects.requireNonNull(entry.getKey(), "Priority is required"),
                    Objects.requireNonNull(entry.getValue(), "Strategy is required"));
        }
    }

    public RouteStrategy select(DeliveryRequest request) {
        Objects.requireNonNull(request, "Delivery request is required");
        DeliveryPriority priority = request.priority();
        if (priority == DeliveryPriority.STANDARD && request.ecoPreferred()) {
            priority = DeliveryPriority.ECO;
        }
        RouteStrategy strategy = strategies.get(priority);
        if (strategy == null) {
            throw new IllegalStateException("No strategy configured for priority " + priority);
        }
        return strategy;
    }
}

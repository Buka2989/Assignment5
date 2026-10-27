package com.assignment5.planner;

import com.assignment5.domain.DeliveryRequest;
import com.assignment5.domain.Route;
import com.assignment5.domain.RouteDecision;
import com.assignment5.strategy.RouteStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StrategyComparison {
    private final List<RouteStrategy> strategies;

    public StrategyComparison(List<RouteStrategy> strategies) {
        this.strategies = List.copyOf(Objects.requireNonNull(strategies, "Strategies are required"));
        if (this.strategies.isEmpty()) {
            throw new IllegalArgumentException("At least one strategy is required");
        }
    }

    public List<RouteDecision> compare(List<Route> routes, DeliveryRequest request) {
        Objects.requireNonNull(routes, "Routes are required");
        Objects.requireNonNull(request, "Delivery request is required");
        List<RouteDecision> decisions = new ArrayList<>();
        for (RouteStrategy strategy : strategies) {
            decisions.add(strategy.selectRoute(routes, request));
        }
        return List.copyOf(decisions);
    }
}

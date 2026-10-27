package com.assignment5.planner;

import com.assignment5.domain.DeliveryRequest;
import com.assignment5.domain.Route;
import com.assignment5.strategy.RouteStrategy;
import java.util.List;
import java.util.Objects;

public class RoutePlanner {
    private RouteStrategy strategy;

    public RoutePlanner(RouteStrategy strategy) {
        setStrategy(strategy);
    }

    public void setStrategy(RouteStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "Route strategy is required");
    }

    public Route plan(List<Route> routes, DeliveryRequest request) {
        Objects.requireNonNull(routes, "Routes are required");
        Objects.requireNonNull(request, "Delivery request is required");
        return strategy.selectRoute(routes, request);
    }
}

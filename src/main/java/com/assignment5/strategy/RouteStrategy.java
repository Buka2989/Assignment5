package com.assignment5.strategy;

import com.assignment5.domain.DeliveryRequest;
import com.assignment5.domain.Route;
import com.assignment5.domain.RouteDecision;
import java.util.List;

public interface RouteStrategy {
    RouteDecision selectRoute(List<Route> routes, DeliveryRequest request);

    String getStrategyName();
}

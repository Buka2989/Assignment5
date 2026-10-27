package com.assignment5.strategy;

import com.assignment5.domain.DeliveryRequest;
import com.assignment5.domain.Route;
import java.util.List;

public interface RouteStrategy {
    Route selectRoute(List<Route> routes, DeliveryRequest request);

    String getStrategyName();
}

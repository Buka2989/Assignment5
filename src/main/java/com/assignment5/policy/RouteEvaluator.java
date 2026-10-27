package com.assignment5.policy;

import com.assignment5.domain.DeliveryPriority;
import com.assignment5.domain.DeliveryRequest;
import com.assignment5.domain.NoSuitableRouteException;
import com.assignment5.domain.Route;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class RouteEvaluator {
    public static final double COST_PER_KM = 60.0;
    public static final double URGENT_SURCHARGE = 500.0;

    private RouteEvaluator() {
    }

    public static RouteEvaluation evaluate(List<Route> routes, DeliveryRequest request) {
        Objects.requireNonNull(routes, "Routes are required");
        Objects.requireNonNull(request, "Delivery request is required");
        if (routes.isEmpty()) {
            throw new NoSuitableRouteException("No candidate routes were provided");
        }

        List<RouteCandidate> candidates = new ArrayList<>();
        Map<String, String> rejected = new LinkedHashMap<>();
        for (Route route : routes) {
            Objects.requireNonNull(route, "Route list cannot contain null");
            int effectiveMinutes = Math.addExact(route.estimatedMinutes(), route.trafficDelayMinutes());
            double surcharge = request.priority() == DeliveryPriority.URGENT ? URGENT_SURCHARGE : 0;
            double totalCost = route.baseCost() + route.distanceKm() * COST_PER_KM
                    + route.additionalFees() + surcharge;
            if (!Double.isFinite(totalCost)) {
                throw new IllegalArgumentException("Calculated route cost must be finite");
            }

            if (!route.available()) {
                rejected.put(route.id(), "Route is unavailable");
            } else if (totalCost > request.maxBudget()) {
                rejected.put(route.id(), "Budget exceeded");
            } else if (effectiveMinutes > request.maxDeliveryMinutes()) {
                rejected.put(route.id(), "Delivery deadline exceeded");
            } else {
                candidates.add(new RouteCandidate(route, effectiveMinutes, totalCost));
            }
        }
        if (candidates.isEmpty()) {
            throw new NoSuitableRouteException("No route satisfies the request constraints: " + rejected);
        }
        return new RouteEvaluation(candidates, rejected);
    }
}

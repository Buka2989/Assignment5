package com.assignment5.strategy;

import com.assignment5.domain.DeliveryRequest;
import com.assignment5.domain.Route;
import com.assignment5.domain.RouteDecision;
import com.assignment5.policy.RouteCandidate;
import com.assignment5.policy.RouteEvaluation;
import com.assignment5.policy.RouteEvaluator;
import java.util.Comparator;
import java.util.List;

public class FastestRouteStrategy implements RouteStrategy {
    @Override
    public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
        RouteEvaluation evaluation = RouteEvaluator.evaluate(routes, request);
        RouteCandidate selected = evaluation.candidates().stream()
                .min(Comparator.comparingInt(RouteCandidate::effectiveMinutes)
                        .thenComparing(candidate -> candidate.route().id()))
                .orElseThrow();
        String explanation = "Base time " + selected.route().estimatedMinutes()
                + " min + traffic delay " + selected.route().trafficDelayMinutes()
                + " min = " + selected.effectiveMinutes() + " min";
        return new RouteDecision(selected.route(), getStrategyName(), selected.effectiveMinutes(),
                explanation, evaluation.rejectedRoutes());
    }

    @Override
    public String getStrategyName() {
        return "Fastest";
    }
}

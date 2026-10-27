package com.assignment5.strategy;

import com.assignment5.domain.DeliveryRequest;
import com.assignment5.domain.Route;
import com.assignment5.domain.RouteDecision;
import com.assignment5.policy.RouteCandidate;
import com.assignment5.policy.RouteEvaluation;
import com.assignment5.policy.RouteEvaluator;
import java.util.Comparator;
import java.util.List;

public class DistanceFirstRouteStrategy implements RouteStrategy {
    @Override
    public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
        RouteEvaluation evaluation = RouteEvaluator.evaluate(routes, request);
        RouteCandidate selected = evaluation.candidates().stream()
                .min(Comparator.comparingDouble((RouteCandidate candidate) -> candidate.route().distanceKm())
                        .thenComparingDouble(RouteCandidate::totalCost)
                        .thenComparing(candidate -> candidate.route().id()))
                .orElseThrow();
        String explanation = "Shortest eligible distance: " + selected.route().distanceKm()
                + " km; total cost " + selected.totalCost() + " breaks distance ties";
        return new RouteDecision(selected.route(), getStrategyName(), selected.route().distanceKm(),
                explanation, evaluation.rejectedRoutes());
    }

    @Override
    public String getStrategyName() {
        return "Distance First";
    }
}

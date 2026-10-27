package com.assignment5.strategy;

import com.assignment5.domain.DeliveryRequest;
import com.assignment5.domain.Route;
import com.assignment5.domain.RouteDecision;
import com.assignment5.policy.RouteCandidate;
import com.assignment5.policy.RouteEvaluation;
import com.assignment5.policy.RouteEvaluator;
import java.util.Comparator;
import java.util.List;

public class EcoFriendlyRouteStrategy implements RouteStrategy {
    public static final double EMISSION_WEIGHT = 1.0;
    public static final double DISTANCE_WEIGHT = 0.05;

    @Override
    public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
        RouteEvaluation evaluation = RouteEvaluator.evaluate(routes, request);
        RouteCandidate selected = evaluation.candidates().stream()
                .min(Comparator.comparingDouble(this::score)
                        .thenComparing(candidate -> candidate.route().id()))
                .orElseThrow();
        String explanation = "Emissions " + selected.route().carbonEmissionKg() + " kg x " + EMISSION_WEIGHT
                + " + distance " + selected.route().distanceKm() + " km x " + DISTANCE_WEIGHT
                + " = " + score(selected);
        return new RouteDecision(selected.route(), getStrategyName(), score(selected),
                explanation, evaluation.rejectedRoutes());
    }

    private double score(RouteCandidate candidate) {
        return candidate.route().carbonEmissionKg() * EMISSION_WEIGHT
                + candidate.route().distanceKm() * DISTANCE_WEIGHT;
    }

    @Override
    public String getStrategyName() {
        return "Eco Friendly";
    }
}

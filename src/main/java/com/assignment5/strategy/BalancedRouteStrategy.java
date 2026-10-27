package com.assignment5.strategy;

import com.assignment5.domain.DeliveryRequest;
import com.assignment5.domain.Route;
import com.assignment5.domain.RouteDecision;
import com.assignment5.policy.RouteCandidate;
import com.assignment5.policy.RouteEvaluation;
import com.assignment5.policy.RouteEvaluator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.ToDoubleFunction;

public class BalancedRouteStrategy implements RouteStrategy {
    private final double timeWeight;
    private final double costWeight;
    private final double distanceWeight;
    private final double emissionWeight;

    public BalancedRouteStrategy(double timeWeight, double costWeight,
                                 double distanceWeight, double emissionWeight) {
        double[] weights = {timeWeight, costWeight, distanceWeight, emissionWeight};
        for (double weight : weights) {
            if (!Double.isFinite(weight) || weight < 0 || weight > 1) {
                throw new IllegalArgumentException("Weights must be finite and between 0 and 1");
            }
        }
        if (Math.abs(timeWeight + costWeight + distanceWeight + emissionWeight - 1.0) > 1e-9) {
            throw new IllegalArgumentException("Weights must sum to 1.0");
        }
        this.timeWeight = timeWeight;
        this.costWeight = costWeight;
        this.distanceWeight = distanceWeight;
        this.emissionWeight = emissionWeight;
    }

    @Override
    public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
        RouteEvaluation evaluation = RouteEvaluator.evaluate(routes, request);
        List<RouteCandidate> candidates = evaluation.candidates();
        RouteCandidate selected = candidates.stream()
                .min(Comparator.comparingDouble((RouteCandidate candidate) -> score(candidate, candidates))
                        .thenComparing(candidate -> candidate.route().id()))
                .orElseThrow();
        String explanation = String.format(Locale.ROOT,
                "Normalized time %.3f x %.2f + cost %.3f x %.2f + distance %.3f x %.2f + emissions %.3f x %.2f = %.3f",
                normalized(selected, candidates, RouteCandidate::effectiveMinutes), timeWeight,
                normalized(selected, candidates, RouteCandidate::totalCost), costWeight,
                normalized(selected, candidates, candidate -> candidate.route().distanceKm()), distanceWeight,
                normalized(selected, candidates, candidate -> candidate.route().carbonEmissionKg()), emissionWeight,
                score(selected, candidates));
        return new RouteDecision(selected.route(), getStrategyName(), score(selected, candidates),
                explanation, evaluation.rejectedRoutes());
    }

    private double score(RouteCandidate candidate, List<RouteCandidate> candidates) {
        return timeWeight * normalized(candidate, candidates, RouteCandidate::effectiveMinutes)
                + costWeight * normalized(candidate, candidates, RouteCandidate::totalCost)
                + distanceWeight * normalized(candidate, candidates, item -> item.route().distanceKm())
                + emissionWeight * normalized(candidate, candidates, item -> item.route().carbonEmissionKg());
    }

    private double normalized(RouteCandidate candidate, List<RouteCandidate> candidates,
                              ToDoubleFunction<RouteCandidate> metric) {
        double min = candidates.stream().mapToDouble(metric).min().orElseThrow();
        double max = candidates.stream().mapToDouble(metric).max().orElseThrow();
        return max == min ? 0 : (metric.applyAsDouble(candidate) - min) / (max - min);
    }

    @Override
    public String getStrategyName() {
        return "Balanced";
    }
}

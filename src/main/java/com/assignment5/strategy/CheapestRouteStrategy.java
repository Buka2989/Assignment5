package com.assignment5.strategy;

import com.assignment5.domain.DeliveryRequest;
import com.assignment5.domain.DeliveryPriority;
import com.assignment5.domain.Route;
import com.assignment5.domain.RouteDecision;
import com.assignment5.policy.RouteCandidate;
import com.assignment5.policy.RouteEvaluation;
import com.assignment5.policy.RouteEvaluator;
import java.util.Comparator;
import java.util.List;

public class CheapestRouteStrategy implements RouteStrategy {
    @Override
    public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
        RouteEvaluation evaluation = RouteEvaluator.evaluate(routes, request);
        RouteCandidate selected = evaluation.candidates().stream()
                .min(Comparator.comparingDouble(RouteCandidate::totalCost)
                        .thenComparing(candidate -> candidate.route().id()))
                .orElseThrow();
        double surcharge = request.priority() == DeliveryPriority.URGENT
                ? RouteEvaluator.URGENT_SURCHARGE : 0;
        String explanation = "Base cost " + selected.route().baseCost()
                + " + distance " + selected.route().distanceKm() + " km x " + RouteEvaluator.COST_PER_KM
                + " + fees " + selected.route().additionalFees()
                + " + priority surcharge " + surcharge + " = " + selected.totalCost();
        return new RouteDecision(selected.route(), getStrategyName(), selected.totalCost(),
                explanation, evaluation.rejectedRoutes());
    }

    @Override
    public String getStrategyName() {
        return "Cheapest";
    }
}

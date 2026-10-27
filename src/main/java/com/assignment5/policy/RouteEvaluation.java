package com.assignment5.policy;

import java.util.List;
import java.util.Map;

public record RouteEvaluation(List<RouteCandidate> candidates, Map<String, String> rejectedRoutes) {
    public RouteEvaluation {
        candidates = List.copyOf(candidates);
        rejectedRoutes = Map.copyOf(rejectedRoutes);
    }
}

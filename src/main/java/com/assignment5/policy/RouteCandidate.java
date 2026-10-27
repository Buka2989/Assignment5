package com.assignment5.policy;

import com.assignment5.domain.Route;

public record RouteCandidate(Route route, int effectiveMinutes, double totalCost) {
}

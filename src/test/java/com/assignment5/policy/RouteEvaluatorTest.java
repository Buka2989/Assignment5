package com.assignment5.policy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.assignment5.domain.DeliveryPriority;
import com.assignment5.domain.DeliveryRequest;
import com.assignment5.domain.Location;
import com.assignment5.domain.NoSuitableRouteException;
import com.assignment5.domain.Route;
import java.util.List;
import org.junit.jupiter.api.Test;

class RouteEvaluatorTest {
    private final Location warehouse = new Location("Warehouse", 44.85, 65.50);
    private final Location hospital = new Location("Hospital", 44.87, 65.52);

    @Test
    void costIncludesDistanceFeesAndUrgentSurcharge() {
        Route route = route("R1", 20, 2000, 100, 4, true);
        DeliveryRequest request = new DeliveryRequest(DeliveryPriority.URGENT, 5000, 30, false);

        RouteCandidate candidate = RouteEvaluator.evaluate(List.of(route), request).candidates().get(0);

        assertEquals(24, candidate.effectiveMinutes());
        assertEquals(3200, candidate.totalCost());
    }

    @Test
    void rejectsUnavailableOverBudgetAndLateRoutes() {
        Route unavailable = route("R1", 20, 1000, 0, 0, false);
        Route costly = route("R2", 20, 5000, 0, 0, true);
        Route late = route("R3", 40, 1000, 0, 5, true);
        Route valid = route("R4", 20, 1000, 0, 0, true);
        DeliveryRequest request = new DeliveryRequest(DeliveryPriority.STANDARD, 2500, 30, false);

        RouteEvaluation evaluation = RouteEvaluator.evaluate(List.of(unavailable, costly, late, valid), request);

        assertEquals(List.of(valid), evaluation.candidates().stream().map(RouteCandidate::route).toList());
        assertEquals("Route is unavailable", evaluation.rejectedRoutes().get("R1"));
        assertEquals("Budget exceeded", evaluation.rejectedRoutes().get("R2"));
        assertEquals("Delivery deadline exceeded", evaluation.rejectedRoutes().get("R3"));
    }

    @Test
    void reportsWhenNoRouteIsSuitable() {
        DeliveryRequest request = new DeliveryRequest(DeliveryPriority.STANDARD, 500, 30, false);

        assertThrows(NoSuitableRouteException.class, () -> RouteEvaluator.evaluate(List.of(), request));
        assertThrows(NoSuitableRouteException.class,
                () -> RouteEvaluator.evaluate(List.of(route("R1", 20, 1000, 0, 0, true)), request));
    }

    private Route route(String id, int minutes, double baseCost, double fees, int delay, boolean available) {
        return new Route(id, warehouse, hospital, 10, minutes, baseCost, fees, 1.5, delay, available);
    }
}

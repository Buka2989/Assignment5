package com.assignment5.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.assignment5.domain.DeliveryPriority;
import com.assignment5.domain.DeliveryRequest;
import com.assignment5.domain.Location;
import com.assignment5.domain.Route;
import com.assignment5.domain.RouteDecision;
import java.util.List;
import org.junit.jupiter.api.Test;

class EcoFriendlyRouteStrategyTest {
    private final Location warehouse = new Location("Warehouse", 44.85, 65.50);
    private final Location hospital = new Location("Hospital", 44.87, 65.52);
    private final EcoFriendlyRouteStrategy strategy = new EcoFriendlyRouteStrategy();

    @Test
    void choosesLowerCombinedEnvironmentalScore() {
        Route shortPolluting = route("R1", 5, 3.0, 25);
        Route longerCleaner = route("R2", 10, 1.0, 30);
        DeliveryRequest request = new DeliveryRequest(DeliveryPriority.ECO, 5000, 45, true);

        RouteDecision decision = strategy.selectRoute(List.of(shortPolluting, longerCleaner), request);

        assertEquals("R2", decision.route().id());
        assertEquals(1.5, decision.score());
        assertTrue(decision.explanation().contains("Emissions 1.0 kg"));
    }

    @Test
    void rejectsLowEmissionRouteAfterDeadline() {
        Route late = route("R1", 5, 0.5, 60);
        Route onTime = route("R2", 10, 1.0, 30);
        DeliveryRequest request = new DeliveryRequest(DeliveryPriority.ECO, 5000, 45, true);

        RouteDecision decision = strategy.selectRoute(List.of(late, onTime), request);

        assertEquals("R2", decision.route().id());
        assertEquals("Delivery deadline exceeded", decision.rejectedRoutes().get("R1"));
    }

    private Route route(String id, double distance, double emissions, int minutes) {
        return new Route(id, warehouse, hospital, distance, minutes, 1000, 0, emissions, 0, true);
    }
}

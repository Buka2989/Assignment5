package com.assignment5.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.assignment5.domain.DeliveryPriority;
import com.assignment5.domain.DeliveryRequest;
import com.assignment5.domain.Location;
import com.assignment5.domain.Route;
import com.assignment5.domain.RouteDecision;
import java.util.List;
import org.junit.jupiter.api.Test;

class DistanceFirstRouteStrategyTest {
    private final Location warehouse = new Location("Warehouse", 44.85, 65.50);
    private final Location hospital = new Location("Hospital", 44.87, 65.52);
    private final DistanceFirstRouteStrategy strategy = new DistanceFirstRouteStrategy();

    @Test
    void choosesShortestEligibleRoute() {
        Route shortRoute = route("Short", 5, 1000, true);
        Route longRoute = route("Long", 12, 1000, true);
        DeliveryRequest request = new DeliveryRequest(DeliveryPriority.STANDARD, 5000, 50, false);

        RouteDecision decision = strategy.selectRoute(List.of(longRoute, shortRoute), request);

        assertEquals("Short", decision.route().id());
        assertEquals(5, decision.score());
    }

    @Test
    void breaksEqualDistanceTiesByTotalCost() {
        Route expensive = route("Expensive", 5, 2000, true);
        Route cheaper = route("Cheaper", 5, 1000, true);
        DeliveryRequest request = new DeliveryRequest(DeliveryPriority.STANDARD, 5000, 50, false);

        assertEquals("Cheaper", strategy.selectRoute(List.of(expensive, cheaper), request).route().id());
    }

    @Test
    void ignoresUnavailableShortRoute() {
        Route unavailable = route("Short", 5, 1000, false);
        Route available = route("Long", 12, 1000, true);
        DeliveryRequest request = new DeliveryRequest(DeliveryPriority.STANDARD, 5000, 50, false);

        assertEquals("Long", strategy.selectRoute(List.of(unavailable, available), request).route().id());
    }

    private Route route(String id, double distance, double cost, boolean available) {
        return new Route(id, warehouse, hospital, distance, 25, cost, 0, 1.0, 0, available);
    }
}

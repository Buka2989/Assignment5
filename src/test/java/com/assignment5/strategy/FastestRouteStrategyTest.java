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

class FastestRouteStrategyTest {
    private final Location warehouse = new Location("Warehouse", 44.85, 65.50);
    private final Location hospital = new Location("Hospital", 44.87, 65.52);
    private final FastestRouteStrategy strategy = new FastestRouteStrategy();

    @Test
    void choosesEffectiveTimeRatherThanBaseTime() {
        Route congested = route("R1", 15, 20, 1000);
        Route clear = route("R2", 25, 2, 1000);
        DeliveryRequest request = new DeliveryRequest(DeliveryPriority.URGENT, 5000, 40, false);

        RouteDecision decision = strategy.selectRoute(List.of(congested, clear), request);

        assertEquals("R2", decision.route().id());
        assertEquals(27, decision.score());
        assertTrue(decision.explanation().contains("traffic delay 2 min"));
    }

    @Test
    void rejectsFastRouteThatExceedsBudget() {
        Route expensive = route("R1", 12, 0, 5000);
        Route affordable = route("R2", 25, 0, 1000);
        DeliveryRequest request = new DeliveryRequest(DeliveryPriority.STANDARD, 3000, 40, false);

        RouteDecision decision = strategy.selectRoute(List.of(expensive, affordable), request);

        assertEquals("R2", decision.route().id());
        assertEquals("Budget exceeded", decision.rejectedRoutes().get("R1"));
    }

    private Route route(String id, int minutes, int delay, double cost) {
        return new Route(id, warehouse, hospital, 10, minutes, cost, 0, 1.5, delay, true);
    }
}

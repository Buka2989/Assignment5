package com.assignment5.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.assignment5.domain.DeliveryPriority;
import com.assignment5.domain.DeliveryRequest;
import com.assignment5.domain.Location;
import com.assignment5.domain.Route;
import com.assignment5.domain.RouteDecision;
import java.util.List;
import org.junit.jupiter.api.Test;

class BalancedRouteStrategyTest {
    private final Location warehouse = new Location("Warehouse", 44.85, 65.50);
    private final Location hospital = new Location("Hospital", 44.87, 65.52);
    private final Route fast = new Route("Fast", warehouse, hospital, 20, 15, 3000, 0, 3, 0, true);
    private final Route green = new Route("Green", warehouse, hospital, 8, 35, 1000, 0, 0.5, 0, true);
    private final DeliveryRequest request = new DeliveryRequest(DeliveryPriority.STANDARD, 5000, 60, false);

    @Test
    void configuredWeightsChangeTheSelectedRoute() {
        RouteDecision timeFirst = new BalancedRouteStrategy(0.7, 0.1, 0.1, 0.1)
                .selectRoute(List.of(fast, green), request);
        RouteDecision environmentFirst = new BalancedRouteStrategy(0.1, 0.2, 0.2, 0.5)
                .selectRoute(List.of(fast, green), request);

        assertEquals("Fast", timeFirst.route().id());
        assertEquals("Green", environmentFirst.route().id());
        assertTrue(timeFirst.explanation().contains("Normalized time"));
    }

    @Test
    void rejectsInvalidWeights() {
        assertThrows(IllegalArgumentException.class, () -> new BalancedRouteStrategy(0.4, 0.3, 0.1, 0.1));
        assertThrows(IllegalArgumentException.class, () -> new BalancedRouteStrategy(-0.1, 0.6, 0.3, 0.2));
        assertThrows(IllegalArgumentException.class, () -> new BalancedRouteStrategy(Double.NaN, 0.5, 0.3, 0.2));
    }

    @Test
    void equalMetricValuesDoNotCauseDivisionByZero() {
        BalancedRouteStrategy strategy = new BalancedRouteStrategy(0.4, 0.3, 0.1, 0.2);

        RouteDecision decision = strategy.selectRoute(List.of(fast), request);

        assertEquals("Fast", decision.route().id());
        assertEquals(0, decision.score());
    }
}

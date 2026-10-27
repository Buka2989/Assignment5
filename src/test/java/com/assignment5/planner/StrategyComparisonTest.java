package com.assignment5.planner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.assignment5.domain.DeliveryPriority;
import com.assignment5.domain.DeliveryRequest;
import com.assignment5.domain.Location;
import com.assignment5.domain.Route;
import com.assignment5.domain.RouteDecision;
import com.assignment5.strategy.CheapestRouteStrategy;
import com.assignment5.strategy.FastestRouteStrategy;
import java.util.List;
import org.junit.jupiter.api.Test;

class StrategyComparisonTest {
    private final Location warehouse = new Location("Warehouse", 44.85, 65.50);
    private final Location hospital = new Location("Hospital", 44.87, 65.52);

    @Test
    void strategiesCanChooseDifferentRoutesForTheSameRequest() {
        Route fast = new Route("Fast", warehouse, hospital, 10, 15, 3000, 0, 2.0, 0, true);
        Route cheap = new Route("Cheap", warehouse, hospital, 10, 35, 1000, 0, 1.0, 0, true);
        DeliveryRequest request = new DeliveryRequest(DeliveryPriority.STANDARD, 5000, 45, false);
        StrategyComparison comparison = new StrategyComparison(
                List.of(new FastestRouteStrategy(), new CheapestRouteStrategy()));

        List<RouteDecision> decisions = comparison.compare(List.of(fast, cheap), request);

        assertEquals(List.of("Fast", "Cheap"), decisions.stream().map(decision -> decision.route().id()).toList());
        assertEquals(List.of("Fastest", "Cheapest"),
                decisions.stream().map(RouteDecision::strategyName).toList());
    }

    @Test
    void emptyStrategyListIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new StrategyComparison(List.of()));
    }
}

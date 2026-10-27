package com.assignment5.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.assignment5.domain.DeliveryPriority;
import com.assignment5.domain.DeliveryRequest;
import com.assignment5.domain.Location;
import com.assignment5.domain.NoSuitableRouteException;
import com.assignment5.domain.Route;
import com.assignment5.domain.RouteDecision;
import java.util.List;
import org.junit.jupiter.api.Test;

class CheapestRouteStrategyTest {
    private final Location warehouse = new Location("Warehouse", 44.85, 65.50);
    private final Location hospital = new Location("Hospital", 44.87, 65.52);
    private final CheapestRouteStrategy strategy = new CheapestRouteStrategy();

    @Test
    void choosesTotalCostInsteadOfBaseCost() {
        Route longRoute = new Route("R1", warehouse, hospital, 30, 25, 1000, 300, 2.0, 0, true);
        Route shortRoute = new Route("R2", warehouse, hospital, 10, 25, 1800, 0, 1.5, 0, true);
        DeliveryRequest request = new DeliveryRequest(DeliveryPriority.ECONOMY, 5000, 40, false);

        RouteDecision decision = strategy.selectRoute(List.of(longRoute, shortRoute), request);

        assertEquals("R2", decision.route().id());
        assertEquals(2400, decision.score());
        assertTrue(decision.explanation().contains("priority surcharge 0.0"));
    }

    @Test
    void urgentSurchargeCanMakeEveryRouteExceedBudget() {
        Route route = new Route("R1", warehouse, hospital, 10, 25, 1000, 0, 1.5, 0, true);
        DeliveryRequest request = new DeliveryRequest(DeliveryPriority.URGENT, 2000, 40, false);

        assertThrows(NoSuitableRouteException.class,
                () -> strategy.selectRoute(List.of(route), request));
    }
}

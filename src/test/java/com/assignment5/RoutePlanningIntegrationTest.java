package com.assignment5;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.assignment5.domain.DeliveryPriority;
import com.assignment5.domain.DeliveryRequest;
import com.assignment5.domain.Location;
import com.assignment5.domain.Route;
import com.assignment5.planner.RoutePlanner;
import com.assignment5.policy.RouteEvaluator;
import com.assignment5.strategy.BalancedRouteStrategy;
import com.assignment5.strategy.CheapestRouteStrategy;
import com.assignment5.strategy.EcoFriendlyRouteStrategy;
import com.assignment5.strategy.FastestRouteStrategy;
import java.util.List;
import org.junit.jupiter.api.Test;

class RoutePlanningIntegrationTest {
    private final Location warehouse = new Location("Warehouse", 44.85, 65.50);
    private final Location hospital = new Location("Hospital", 44.87, 65.52);

    @Test
    void onePlannerRunsFourRealAlgorithmsAfterThreeSwitches() {
        Route fast = route("Fast", 10, 15, 3000, 2.5);
        Route cheap = route("Cheap", 10, 35, 1000, 1.5);
        Route green = route("Green", 6, 42, 2600, 0.3);
        List<Route> routes = List.of(fast, cheap, green);
        DeliveryRequest request = new DeliveryRequest(DeliveryPriority.STANDARD, 5000, 50, false);
        RoutePlanner planner = new RoutePlanner(new FastestRouteStrategy());

        assertEquals("Fast", planner.plan(routes, request).route().id());
        planner.setStrategy(new CheapestRouteStrategy());
        assertEquals("Cheap", planner.plan(routes, request).route().id());
        planner.setStrategy(new EcoFriendlyRouteStrategy());
        assertEquals("Green", planner.plan(routes, request).route().id());
        planner.setStrategy(new BalancedRouteStrategy(0.7, 0.1, 0.1, 0.1));
        assertEquals("Fast", planner.plan(routes, request).route().id());
    }

    @Test
    void exactBudgetAndDeadlineAreAccepted() {
        Route route = route("Boundary", 10, 25, 1000, 1.0);
        DeliveryRequest request = new DeliveryRequest(DeliveryPriority.STANDARD, 1600, 25, false);

        assertEquals(1600, RouteEvaluator.totalCost(route, request));
        assertEquals("Boundary", new FastestRouteStrategy()
                .selectRoute(List.of(route), request).route().id());
    }

    private Route route(String id, double distance, int minutes, double cost, double emissions) {
        return new Route(id, warehouse, hospital, distance, minutes, cost, 0, emissions, 0, true);
    }
}

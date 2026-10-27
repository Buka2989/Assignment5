package com.assignment5.planner;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.assignment5.domain.DeliveryPriority;
import com.assignment5.domain.DeliveryRequest;
import com.assignment5.domain.Location;
import com.assignment5.domain.Route;
import com.assignment5.strategy.RouteStrategy;
import java.util.List;
import org.junit.jupiter.api.Test;

class RoutePlannerTest {
    private final Location origin = new Location("Warehouse", 44.85, 65.50);
    private final Location destination = new Location("Hospital", 44.87, 65.52);
    private final Route firstRoute = new Route("R1", origin, destination, 12, 25, 2500, 1.8, 5, true);
    private final Route secondRoute = new Route("R2", origin, destination, 16, 20, 3200, 2.3, 2, true);
    private final DeliveryRequest request = new DeliveryRequest(DeliveryPriority.STANDARD, 5000, 45, false);

    @Test
    void plannerDelegatesSelectionToItsStrategy() {
        RecordingRouteStrategy strategy = new RecordingRouteStrategy(firstRoute);
        RoutePlanner planner = new RoutePlanner(strategy);
        List<Route> routes = List.of(firstRoute, secondRoute);

        assertSame(firstRoute, planner.plan(routes, request));
        assertSame(routes, strategy.receivedRoutes);
        assertSame(request, strategy.receivedRequest);
    }

    @Test
    void plannerUsesTheNewStrategyAfterSwitching() {
        RoutePlanner planner = new RoutePlanner(new FixedRouteStrategy(firstRoute));
        planner.setStrategy(new FixedRouteStrategy(secondRoute));

        assertSame(secondRoute, planner.plan(List.of(firstRoute, secondRoute), request));
    }

    @Test
    void plannerRejectsMissingStrategyAndInputs() {
        assertThrows(NullPointerException.class, () -> new RoutePlanner(null));

        RoutePlanner planner = new RoutePlanner(new FixedRouteStrategy(firstRoute));
        assertThrows(NullPointerException.class, () -> planner.setStrategy(null));
        assertThrows(NullPointerException.class, () -> planner.plan(null, request));
        assertThrows(NullPointerException.class, () -> planner.plan(List.of(firstRoute), null));
    }

    private record FixedRouteStrategy(Route selectedRoute) implements RouteStrategy {
        @Override
        public Route selectRoute(List<Route> routes, DeliveryRequest request) {
            return selectedRoute;
        }

        @Override
        public String getStrategyName() {
            return "Fixed";
        }
    }

    private static class RecordingRouteStrategy implements RouteStrategy {
        private final Route selectedRoute;
        private List<Route> receivedRoutes;
        private DeliveryRequest receivedRequest;

        private RecordingRouteStrategy(Route selectedRoute) {
            this.selectedRoute = selectedRoute;
        }

        @Override
        public Route selectRoute(List<Route> routes, DeliveryRequest request) {
            receivedRoutes = routes;
            receivedRequest = request;
            return selectedRoute;
        }

        @Override
        public String getStrategyName() {
            return "Recording";
        }
    }
}

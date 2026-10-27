package com.assignment5;

import com.assignment5.domain.DeliveryPriority;
import com.assignment5.domain.DeliveryRequest;
import com.assignment5.domain.Location;
import com.assignment5.domain.NoSuitableRouteException;
import com.assignment5.domain.Route;
import com.assignment5.domain.RouteDecision;
import com.assignment5.planner.RoutePlanner;
import com.assignment5.planner.StrategyComparison;
import com.assignment5.policy.RouteEvaluator;
import com.assignment5.selector.StrategySelector;
import com.assignment5.strategy.BalancedRouteStrategy;
import com.assignment5.strategy.CheapestRouteStrategy;
import com.assignment5.strategy.DistanceFirstRouteStrategy;
import com.assignment5.strategy.EcoFriendlyRouteStrategy;
import com.assignment5.strategy.FastestRouteStrategy;
import com.assignment5.strategy.RouteStrategy;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class Main {
    public static void main(String[] args) {
        List<Route> routes = sampleRoutes();
        RouteStrategy fastest = new FastestRouteStrategy();
        RouteStrategy cheapest = new CheapestRouteStrategy();
        RouteStrategy eco = new EcoFriendlyRouteStrategy();
        RouteStrategy balanced = new BalancedRouteStrategy(0.40, 0.30, 0.10, 0.20);
        RouteStrategy distanceFirst = new DistanceFirstRouteStrategy();
        List<RouteStrategy> strategies = List.of(fastest, cheapest, eco, balanced, distanceFirst);
        DeliveryRequest standard = new DeliveryRequest(DeliveryPriority.STANDARD, 6000, 50, false);

        System.out.println("SMART DELIVERY ROUTE PLANNER");
        System.out.println("Candidate routes: " + routes.size());
        System.out.println("Runtime strategy switching");
        RoutePlanner planner = new RoutePlanner(fastest);
        printDecision(planner.plan(routes, standard), standard);
        planner.setStrategy(cheapest);
        printDecision(planner.plan(routes, standard), standard);
        planner.setStrategy(eco);
        printDecision(planner.plan(routes, standard), standard);
        planner.setStrategy(balanced);
        printDecision(planner.plan(routes, standard), standard);

        StrategySelector selector = new StrategySelector(Map.of(
                DeliveryPriority.URGENT, fastest,
                DeliveryPriority.ECONOMY, cheapest,
                DeliveryPriority.ECO, eco,
                DeliveryPriority.STANDARD, balanced));
        System.out.println("Automatic strategy selection");
        List<DeliveryRequest> requests = List.of(
                new DeliveryRequest(DeliveryPriority.URGENT, 6000, 40, false),
                new DeliveryRequest(DeliveryPriority.ECONOMY, 3000, 50, false),
                new DeliveryRequest(DeliveryPriority.ECO, 6000, 50, true),
                standard);
        for (DeliveryRequest request : requests) {
            planner.setStrategy(selector.select(request));
            System.out.println("Request priority: " + request.priority());
            printDecision(planner.plan(routes, request), request);
        }

        System.out.println("Strategy comparison for the same STANDARD request");
        for (RouteDecision decision : new StrategyComparison(strategies).compare(routes, standard)) {
            printDecision(decision, standard);
        }

        System.out.println("No suitable route example");
        try {
            planner.plan(routes, new DeliveryRequest(DeliveryPriority.STANDARD, 100, 10, false));
        } catch (NoSuitableRouteException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private static void printDecision(RouteDecision decision, DeliveryRequest request) {
        Route route = decision.route();
        System.out.printf(Locale.ROOT, "%s: %s | time: %d min | cost: %.2f | CO2: %.2f kg | score: %.3f%n",
                decision.strategyName(), route.id(), route.estimatedMinutes() + route.trafficDelayMinutes(),
                RouteEvaluator.totalCost(route, request), route.carbonEmissionKg(), decision.score());
        System.out.println("Reason: " + decision.explanation());
        new TreeMap<>(decision.rejectedRoutes()).forEach((id, reason) ->
                System.out.println("Rejected " + id + ": " + reason));
    }

    private static List<Route> sampleRoutes() {
        Location warehouse = new Location("Central Warehouse", 44.85, 65.50);
        Location hospital = new Location("City Hospital", 44.87, 65.52);
        return List.of(
                new Route("R1", warehouse, hospital, 10, 20, 2600, 100, 2.2, 10, true),
                new Route("R2", warehouse, hospital, 14, 18, 3300, 0, 2.8, 3, true),
                new Route("R3", warehouse, hospital, 8, 30, 1800, 50, 1.4, 4, true),
                new Route("R4", warehouse, hospital, 12, 36, 2000, 0, 0.6, 2, true),
                new Route("R5", warehouse, hospital, 18, 24, 1600, 200, 3.1, 8, true),
                new Route("R6", warehouse, hospital, 6, 42, 2600, 0, 0.4, 2, true),
                new Route("R7", warehouse, hospital, 9, 19, 1000, 0, 1.1, 0, false),
                new Route("R8", warehouse, hospital, 5, 14, 6500, 0, 3.5, 0, true));
    }
}

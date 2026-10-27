# Smart Delivery Route Planner

This project selects a delivery route from several candidates. Different requests can favor speed, cost, environmental impact, or a balance of all criteria. The program uses the Strategy Pattern so a route selection algorithm can change without changing the planner.

The implementation uses Java 17 language compatibility. It was developed and tested with JDK 27, Maven, and JUnit 5. All calculations use demonstration data; the project does not connect to a map service or process real deliveries.

## Run the project

```powershell
mvn test
mvn exec:java
```

The demo creates eight candidate routes. It shows four algorithms on the same `RoutePlanner` object, three runtime switches, automatic selection for four delivery priorities, a comparison of all five strategies, and a request with no suitable route.

## Architecture

`RoutePlanner` is the Context. Its `plan` method delegates to the current `RouteStrategy`, and `setStrategy` changes the algorithm at runtime. The Context contains no route scoring rules. `RouteStrategy` is the Strategy interface. Its implementations are `FastestRouteStrategy`, `CheapestRouteStrategy`, `EcoFriendlyRouteStrategy`, `BalancedRouteStrategy`, and `DistanceFirstRouteStrategy`.

`StrategySelector` chooses a strategy for a request from an injected priority-to-strategy mapping. `URGENT` uses Fastest, `ECONOMY` uses Cheapest, `ECO` uses Eco Friendly, and `STANDARD` uses Balanced in the demo. A standard request with `ecoPreferred=true` uses Eco Friendly. Urgent priority takes precedence over the eco preference. This component decides which algorithm to use; a strategy decides how to rank routes.

`RouteEvaluator` calculates effective time and total cost, then rejects routes that are unavailable, over budget, or after the deadline. Every strategy uses the same eligible candidates. An empty route list or a request with no eligible route throws `NoSuitableRouteException`. A `RouteDecision` carries the selected route, strategy name, score, explanation, and rejection reasons. `StrategyComparison` runs the same request through each supplied strategy.

The domain model contains `Location`, `Route`, `DeliveryRequest`, and `DeliveryPriority`. Record constructors reject missing required values, invalid coordinates, negative measurements, and non-finite numeric inputs.

## Selection calculations

The following calculations are deterministic for the demonstration:

| Strategy | Rule |
| --- | --- |
| Fastest | Minimize `estimatedMinutes + trafficDelayMinutes`. This accounts for traffic instead of using base time alone. |
| Cheapest | Minimize `baseCost + distanceKm × 60 + additionalFees + urgentSurcharge`, where the surcharge is 500 for `URGENT` and 0 otherwise. |
| Eco Friendly | Minimize `carbonEmissionKg × 1.0 + distanceKm × 0.05`. Environmental scoring belongs here because this strategy owns the environmental objective. |
| Balanced | Minimize `normalizedTime × timeWeight + normalizedCost × costWeight + normalizedDistance × distanceWeight + normalizedEmission × emissionWeight`. |
| Distance First | Minimize distance; use total cost and then route ID to break ties. This fifth strategy was introduced after the first four worked. |

Balanced normalization is `(value - minimum) / (maximum - minimum)` among eligible routes. If all eligible routes have the same value for a metric, its normalized value is 0. The four weights must be finite, non-negative, no greater than 1, and sum to 1 within `1e-9`. The demo uses `0.40` for time, `0.30` for cost, `0.10` for distance, and `0.20` for emissions. Invalid configurations are rejected.

The cost and environmental constants are illustrative, not real tariffs or measured environmental coefficients. A route that violates a hard constraint is filtered before any score can make it win.

## Example console output

The standard request in the demo has a budget of 6000 and a deadline of 50 minutes. Selected lines from `mvn exec:java` are:

```text
Candidate routes: 8
Runtime strategy switching
Fastest: R2 | time: 21 min | cost: 4140.00 | CO2: 2.80 kg | score: 21.000
Cheapest: R3 | time: 34 min | cost: 2330.00 | CO2: 1.40 kg | score: 2330.000
Eco Friendly: R6 | time: 44 min | cost: 2960.00 | CO2: 0.40 kg | score: 0.700
Balanced: R3 | time: 34 min | cost: 2330.00 | CO2: 1.40 kg | score: 0.317
```

The full output also includes the calculation behind each selection and reasons such as `Budget exceeded`, `Delivery deadline exceeded`, and `Route is unavailable` for rejected routes. With a budget of 100 and a 10-minute deadline, the program prints the explicit no-suitable-route error.

## UML diagrams

- [Class diagram](docs/class-diagram.png) ([PlantUML source](docs/class-diagram.puml)) shows the Context, Strategy interface, five concrete strategies, selector, domain model, and comparison component.
- [Sequence diagram](docs/sequence-diagram.png) ([PlantUML source](docs/sequence-diagram.puml)) follows automatic selection of Fastest for an urgent request.

The diagrams are PlantUML source files and correspond to the Java classes in this repository.

## Tests

`mvn test` runs 30 JUnit 5 tests. They cover all five strategies, configurable weights, route selection with traffic and cost formulas, automatic selection, runtime switching, strategy comparison, constraints, invalid domain values, empty routes, no suitable route, and boundary cases. The latest complete run passed with 30 tests, 0 failures, 0 errors, and 0 skipped.

## Why Strategy

The selection algorithms implement one interface and can be exchanged in the same planner object. A new algorithm can be added as another `RouteStrategy` class, then supplied to `RoutePlanner` or a comparison list. The planner does not need an `instanceof` chain or a `switch` over algorithm names. To use a new strategy in automatic selection, the application adds a mapping in the selector configuration. `DistanceFirstRouteStrategy` was added in its own Git commit without changing `RoutePlanner`.

In Assignment 3, Bridge separated notification types from delivery channels so two dimensions could vary independently. Here, Strategy varies one route selection algorithm used by one planner Context. Both rely on an interface and composition, but the design purposes differ.

The code demonstrates SRP by separating planning, algorithm implementations, request-to-strategy selection, and eligibility checks. It demonstrates OCP because additional strategies require little existing code change. It demonstrates DIP because `RoutePlanner` depends on `RouteStrategy`, not a concrete class.

The current architecture has fixed demonstration coefficients and in-memory routes. For a real logistics system, costs, traffic, emissions, and availability would need data sources and domain-specific validation. It also reports the first failed constraint for each rejected route rather than every possible violation.

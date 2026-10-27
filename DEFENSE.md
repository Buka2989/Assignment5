# Assignment 5 Defense Guide

## Short explanation

I built a program that compares delivery routes using interchangeable selection algorithms. `RoutePlanner` is my Context, `RouteStrategy` is the Strategy interface, and five classes provide concrete algorithms. I can change the algorithm with `setStrategy` without recreating the planner.

## What each component does

- `RoutePlanner` delegates to the current strategy. It does not know how a route is scored.
- `StrategySelector` selects which strategy to use for a delivery priority. This is separate from how that strategy ranks routes.
- `RouteEvaluator` calculates total cost and traffic-adjusted time, then applies availability, budget, and deadline restrictions.
- `RouteDecision` explains the selected route and carries reasons for rejected candidates.
- `StrategyComparison` runs one request through every supplied strategy.

## Common questions

### Why is this Strategy instead of a large if/else block?

Each algorithm lives in one class implementing `RouteStrategy`. The planner calls the interface. Adding an algorithm does not require adding a branch inside the planner.

### What is different from Bridge in Assignment 3?

Bridge separated two independently growing dimensions: notification types and sender channels. This assignment changes one algorithm within a planner Context. The two patterns both use composition, but solve different design problems.

### What happens if a route has the best score but exceeds the budget?

`RouteEvaluator` removes it before a strategy ranks candidates. The decision records `Budget exceeded` for that route.

### What happens if no route qualifies?

`NoSuitableRouteException` states that no candidate routes were provided or that no route satisfies the constraints. The program never returns `null` as a route selection.

### How are Balanced scores calculated?

Each criterion is normalized against eligible routes to a value between 0 and 1. The score is the weighted sum of normalized time, cost, distance, and emissions. The weights are constructor parameters and must sum to 1.0.

### Why does Eco Friendly use its own formula?

Environmental impact is its selection objective. The weighted emissions and distance formula belongs to that algorithm, while the planner only delegates.

### How was the fifth strategy added?

`DistanceFirstRouteStrategy` was added in a separate commit. The new class implements `RouteStrategy`; only the demo registration and tests were added. `RoutePlanner` did not change.

## Possible live modifications

- Change a Balanced weight in `Main` while keeping the sum at 1.0, then run `mvn exec:java`.
- Change the `ECO` mapping in `Main` and explain why the selector changes but the planner does not.
- Add a sixth strategy by implementing `RouteStrategy`, registering it in the demo, and adding focused tests.
- Mark a sample route unavailable or lower a request budget and explain the rejection output.

## Commands

```powershell
mvn test
mvn exec:java
```

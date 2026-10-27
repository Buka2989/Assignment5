package com.assignment5.selector;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.assignment5.domain.DeliveryPriority;
import com.assignment5.domain.DeliveryRequest;
import com.assignment5.strategy.BalancedRouteStrategy;
import com.assignment5.strategy.CheapestRouteStrategy;
import com.assignment5.strategy.EcoFriendlyRouteStrategy;
import com.assignment5.strategy.FastestRouteStrategy;
import com.assignment5.strategy.RouteStrategy;
import java.util.Map;
import org.junit.jupiter.api.Test;

class StrategySelectorTest {
    private final RouteStrategy fastest = new FastestRouteStrategy();
    private final RouteStrategy cheapest = new CheapestRouteStrategy();
    private final RouteStrategy eco = new EcoFriendlyRouteStrategy();
    private final RouteStrategy balanced = new BalancedRouteStrategy(0.4, 0.3, 0.1, 0.2);
    private final StrategySelector selector = new StrategySelector(Map.of(
            DeliveryPriority.URGENT, fastest,
            DeliveryPriority.ECONOMY, cheapest,
            DeliveryPriority.ECO, eco,
            DeliveryPriority.STANDARD, balanced));

    @Test
    void selectsStrategyFromPriority() {
        assertSame(fastest, selector.select(request(DeliveryPriority.URGENT, false)));
        assertSame(cheapest, selector.select(request(DeliveryPriority.ECONOMY, false)));
        assertSame(eco, selector.select(request(DeliveryPriority.ECO, false)));
        assertSame(balanced, selector.select(request(DeliveryPriority.STANDARD, false)));
    }

    @Test
    void ecoPreferenceOverridesStandardButNotUrgentPriority() {
        assertSame(eco, selector.select(request(DeliveryPriority.STANDARD, true)));
        assertSame(fastest, selector.select(request(DeliveryPriority.URGENT, true)));
    }

    @Test
    void missingMappingFailsExplicitly() {
        StrategySelector incomplete = new StrategySelector(Map.of(DeliveryPriority.URGENT, fastest));

        assertThrows(IllegalStateException.class,
                () -> incomplete.select(request(DeliveryPriority.STANDARD, false)));
    }

    private DeliveryRequest request(DeliveryPriority priority, boolean ecoPreferred) {
        return new DeliveryRequest(priority, 5000, 60, ecoPreferred);
    }
}

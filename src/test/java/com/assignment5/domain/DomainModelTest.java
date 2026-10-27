package com.assignment5.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class DomainModelTest {
    private final Location origin = new Location("Warehouse", 44.85, 65.50);
    private final Location destination = new Location("Hospital", 44.87, 65.52);

    @Test
    void locationRejectsMissingName() {
        assertThrows(IllegalArgumentException.class, () -> new Location(" ", 44.85, 65.50));
    }

    @Test
    void locationRejectsOutOfRangeCoordinates() {
        assertThrows(IllegalArgumentException.class, () -> new Location("Invalid", 91, 65.50));
        assertThrows(IllegalArgumentException.class, () -> new Location("Invalid", 44.85, -181));
    }

    @Test
    void routeStoresMeaningfulDeliveryData() {
        Route route = new Route("R1", origin, destination, 12.5, 25, 2500, 100, 1.8, 5, true);

        assertEquals("R1", route.id());
        assertEquals(destination, route.destination());
        assertEquals(5, route.trafficDelayMinutes());
    }

    @Test
    void routeRejectsNegativeAndNonFiniteMeasurements() {
        assertThrows(IllegalArgumentException.class,
                () -> new Route("R1", origin, destination, -1, 25, 2500, 100, 1.8, 5, true));
        assertThrows(IllegalArgumentException.class,
                () -> new Route("R1", origin, destination, 12.5, 25, Double.NaN, 100, 1.8, 5, true));
    }

    @Test
    void routeRejectsMissingEndpoint() {
        assertThrows(NullPointerException.class,
                () -> new Route("R1", origin, null, 12.5, 25, 2500, 100, 1.8, 5, true));
    }

    @Test
    void requestRejectsInvalidLimits() {
        assertThrows(IllegalArgumentException.class,
                () -> new DeliveryRequest(DeliveryPriority.URGENT, -1, 30, false));
        assertThrows(IllegalArgumentException.class,
                () -> new DeliveryRequest(DeliveryPriority.URGENT, 5000, -1, false));
    }

    @Test
    void requestRequiresPriority() {
        assertThrows(NullPointerException.class, () -> new DeliveryRequest(null, 5000, 30, false));
    }
}

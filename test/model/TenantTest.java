package model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TenantTest {

    private Tenant tenant;
    private Property property;

    @BeforeEach
    void setUp() {
        tenant = new Tenant(
                1,
                "Huy",
                "Hoang",
                "huy@example.com");

        property = new Property(
                101,
                "Apartment near university",
                500.00);
    }

    @Test
    void testRentPropertySuccessfully() {
        RentalContract contract = tenant.rentProperty(
                1,
                property,
                LocalDate.of(2026, 11, 1),
                LocalDate.of(2026, 12, 1));

        assertNotNull(contract);
        assertEquals(1, tenant.getRentalContracts().size());
        assertSame(contract, tenant.getRentalContracts().get(0));
        assertFalse(property.isAvailable());
    }

    @Test
    void testRentUnavailableProperty() {
        // Another tenant rents the property first
        Tenant anotherTenant = new Tenant(
                2,
                "An",
                "Nguyen",
                "an@example.com");

        anotherTenant.rentProperty(
                2,
                property,
                LocalDate.of(2026, 11, 1),
                LocalDate.of(2026, 12, 1));

        // Current tenant cannot rent an unavailable property
        assertThrows(IllegalStateException.class, () -> {
            tenant.rentProperty(
                    3,
                    property,
                    LocalDate.of(2026, 11, 1),
                    LocalDate.of(2026, 12, 1));
        });

        assertEquals(0, tenant.getRentalContracts().size());
    }

    @Test
    void testRentPropertyWithReversedDates() {
        assertThrows(IllegalArgumentException.class, () -> {
            tenant.rentProperty(
                    4,
                    property,
                    LocalDate.of(2026, 12, 1),
                    LocalDate.of(2026, 11, 1));
        });

        // Contract must not be added when creation fails
        assertEquals(0, tenant.getRentalContracts().size());

        // Property should remain available
        assertTrue(property.isAvailable());
    }

    @Test
    void testRentPropertyWithSameStartAndEndDate() {
        assertThrows(IllegalArgumentException.class, () -> {
            tenant.rentProperty(
                    5,
                    property,
                    LocalDate.of(2026, 11, 1),
                    LocalDate.of(2026, 11, 1));
        });

        assertEquals(0, tenant.getRentalContracts().size());
        assertTrue(property.isAvailable());
    }
}
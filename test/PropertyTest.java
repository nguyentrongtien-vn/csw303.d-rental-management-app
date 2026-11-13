
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import model.Property;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Property Model Unit Tests")
class PropertyTest {

    private Property property;

    @BeforeEach
    void setUp() {
        property = new Property(101, "Apartment A101", 5000.50);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create property with valid values")
        void validProperty_createsSuccessfully() {
            assertEquals(101, property.getPropertyID());
            assertEquals("Apartment A101", property.getDescription());
            assertEquals(5000.50, property.getRentalRate(), 0.000001);
            assertTrue(property.isAvailable());
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1, -100})
        @DisplayName("Should reject non-positive property ID")
        void invalidPropertyId_throwsException(int invalidId) {
            assertThrows(IllegalArgumentException.class,
                    () -> new Property(invalidId, "Apartment A101", 5000.50));
        }

        @Test
        @DisplayName("Should reject null description")
        void nullDescription_throwsException() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Property(101, null, 5000.50));
        }

        @Test
        @DisplayName("Should reject empty or blank description")
        void blankDescription_throwsException() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Property(101, "", 5000.50));

            assertThrows(IllegalArgumentException.class,
                    () -> new Property(101, "   ", 5000.50));
        }

        @Test
        @DisplayName("Should reject invalid rental rates")
        void invalidRentalRate_throwsException() {
            assertAll(
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> new Property(101, "Apartment A101", 0)),
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> new Property(101, "Apartment A101", -100)),
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> new Property(101, "Apartment A101", Double.NaN)),
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> new Property(101, "Apartment A101", Double.POSITIVE_INFINITY)),
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> new Property(101, "Apartment A101", Double.NEGATIVE_INFINITY))
            );
        }
    }

    @Nested
    @DisplayName("Description Update Tests")
    class DescriptionUpdateTests {

        @Test
        @DisplayName("Should update description with valid value")
        void validDescription_updatesSuccessfully() {
            property.UpdateDescription("New Apartment A102");

            assertEquals("New Apartment A102", property.getDescription());
        }

        @Test
        @DisplayName("Should preserve old description after invalid update")
        void invalidDescription_preservesOldValue() {
            assertThrows(IllegalArgumentException.class,
                    () -> property.UpdateDescription(null));

            assertThrows(IllegalArgumentException.class,
                    () -> property.UpdateDescription("   "));

            assertEquals("Apartment A101", property.getDescription());
        }
    }

    @Nested
    @DisplayName("Rental Rate Update Tests")
    class RentalRateUpdateTests {

        @Test
        @DisplayName("Should update rental rate with valid value")
        void validRentalRate_updatesSuccessfully() {
            property.UpdateRentalRate(6500.00);

            assertEquals(6500.00, property.getRentalRate(), 0.000001);
        }

        @Test
        @DisplayName("Should preserve old rental rate after invalid update")
        void invalidRentalRate_preservesOldValue() {
            assertAll(
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> property.UpdateRentalRate(0)),
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> property.UpdateRentalRate(-100)),
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> property.UpdateRentalRate(Double.NaN)),
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> property.UpdateRentalRate(Double.POSITIVE_INFINITY))
            );

            assertEquals(5000.50, property.getRentalRate(), 0.000001);
        }
    }

    @Nested
    @DisplayName("Availability Tests")
    class AvailabilityTests {

        @Test
        @DisplayName("Should update property availability")
        void setAvailable_updatesStateCorrectly() {
            property.setAvailable(false);
            assertFalse(property.isAvailable());

            property.setAvailable(true);
            assertTrue(property.isAvailable());
        }
    }
}

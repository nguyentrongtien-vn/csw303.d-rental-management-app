package model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import model.Property;
import model.PropertyOwner;

import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PropertyOwner Model Unit Tests")
class PropertyOwnerTest {

    private PropertyOwner owner;

    @BeforeEach
    void setUp() {
        // Arrange: Prepare a valid initial PropertyOwner before each test
        owner = new PropertyOwner(1, "John", "Doe", "johndoe@example.com");
    }

    @Nested
    @DisplayName("Constructor & Inheritance Tests")
    class InheritanceTests {

        @Test
        @DisplayName("Should correctly inherit and initialize User fields and empty property list")
        void constructor_initializesCorrectly() {
            // Assert: Verify inherited fields from User class
            assertEquals(1, owner.getUserId());
            assertEquals("John", owner.getFirstName());
            assertEquals("Doe", owner.getLastName());
            assertEquals("johndoe@example.com", owner.getEmail());
            
            // Verify ownedProperties list is initially empty
            assertTrue(owner.getProperties().isEmpty());
        }
    }

    @Nested
    @DisplayName("CreateProperty Valid Cases (a > 0)")
    class ValidCreationTests {

        @Test
        @DisplayName("Should successfully create property, add to list, and return it")
        void validCreation_addsToListAndReturnsProperty() {
            // Action
            Property newProperty = owner.createProperty(101, "Apartment A101", 5000.50);

            // Assert
            assertNotNull(newProperty);
            assertEquals(101, newProperty.getPropertyID());
            assertEquals("Apartment A101", newProperty.getDescription());
            assertEquals(5000.50, newProperty.getRentalRate());
            
            // Verify list state has mutated correctly
            List<Property> properties = owner.getProperties();
            assertEquals(1, properties.size());
            assertTrue(properties.contains(newProperty));
        }
    }

    @Nested
    @DisplayName("CreateProperty Invalid Cases (a <= 0 & Invalid Strings)")
    class InvalidCreationTests {

        @ParameterizedTest
        @ValueSource(ints = {0, -1, -100})
        @DisplayName("Should throw IllegalArgumentException and preserve list state when propertyID is 0 or negative")
        void invalidPropertyId_throwsExceptionAndPreservesState(int invalidId) {
            // Action & Assert: Expect IllegalArgumentException
            assertThrows(IllegalArgumentException.class, 
                () -> owner.createProperty(invalidId, "Valid Description", 5000.0),
                "Expected IllegalArgumentException when propertyID is <= 0"
            );
            
            // Critical: Ensure no garbage data was added to the list
            assertTrue(owner.getProperties().isEmpty(), "OwnedProperties list should remain empty");
        }

        @ParameterizedTest
        @ValueSource(doubles = {0.0, -0.1, -500.0})
        @DisplayName("Should throw IllegalArgumentException and preserve list state when rentalRate is 0 or negative")
        void invalidRentalRate_throwsExceptionAndPreservesState(double invalidRate) {
            // Action & Assert
            assertThrows(IllegalArgumentException.class, 
                () -> owner.createProperty(102, "Valid Description", invalidRate),
                "Expected IllegalArgumentException when rentalRate is <= 0"
            );
            assertTrue(owner.getProperties().isEmpty(), "OwnedProperties list should remain empty");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "}) 
        @DisplayName("Should throw IllegalArgumentException and preserve list state when description is null or empty")
        void invalidDescription_throwsExceptionAndPreservesState(String invalidDescription) {
            // Action & Assert
            assertThrows(IllegalArgumentException.class, 
                () -> owner.createProperty(103, invalidDescription, 5000.0),
                "Expected IllegalArgumentException when description is invalid"
            );
            assertTrue(owner.getProperties().isEmpty(), "OwnedProperties list should remain empty");
        }
    }

    @Nested
    @DisplayName("Encapsulation & Security Tests")
    class EncapsulationTests {

        @Test
        @DisplayName("getProperties should return an unmodifiable list")
        void getProperties_returnsUnmodifiableList() {
            // Arrange
            owner.createProperty(101, "Apartment A101", 5000.50);
            List<Property> properties = owner.getProperties();

            // Act & Assert: Attempting to modify the list externally should throw UnsupportedOperationException
            assertThrows(UnsupportedOperationException.class, 
                () -> properties.add(new Property(999, "Hacker Apartment", 1.0)),
                "Should not allow external modification of the properties list"
            );
        }
    }
}
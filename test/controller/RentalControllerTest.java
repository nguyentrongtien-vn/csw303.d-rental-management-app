package controller;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import model.Property;
import model.PropertyOwner;
import model.RentalContract;
import model.Tenant;

@DisplayName("RentalController Unit Tests")
class RentalControllerTest {

    private RentalController controller;
    private PropertyOwner owner;
    private Tenant tenant;
    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        controller = new RentalController();
        owner = new PropertyOwner(1, "Anna", "Nguyen", "anna@example.com");
        tenant = new Tenant(2, "Minh", "Tran", "minh@example.com");
        startDate = LocalDate.of(2026, 11, 1);
        endDate = LocalDate.of(2027, 10, 31);
    }

    @Nested
    @DisplayName("createProperty Operation Tests")
    class CreatePropertyTests {

        @Test
        @DisplayName("Should successfully create property and register it with owner")
        void createProperty_successfulCreation() {
            Property property = controller.createProperty(owner, 101, "Apartment A101, two bedrooms", 5000.0);

            assertNotNull(property);
            assertEquals(101, property.getPropertyID());
            assertEquals("Apartment A101, two bedrooms", property.getDescription());
            assertEquals(5000.0, property.getRentalRate());
            assertTrue(property.isAvailable());
            assertTrue(owner.getProperties().contains(property));
            assertEquals(1, owner.getProperties().size());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when owner is null")
        void createProperty_nullOwner_throwsException() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> controller.createProperty(null, 101, "Apartment A101", 5000.0));
            assertEquals("PropertyOwner must not be null.", ex.getMessage());
        }

        @ParameterizedTest
        @ValueSource(ints = { 0, -1, -50 })
        @DisplayName("Should delegate invalid property ID validation to Model and preserve owner state")
        void createProperty_invalidId_throwsException(int invalidId) {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.createProperty(owner, invalidId, "Apartment A101", 5000.0));
            assertTrue(owner.getProperties().isEmpty());
        }

        @Test
        @DisplayName("Should delegate empty description validation to Model and preserve owner state")
        void createProperty_blankDescription_throwsException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.createProperty(owner, 101, "   ", 5000.0));
            assertTrue(owner.getProperties().isEmpty());
        }

        @ParameterizedTest
        @ValueSource(doubles = { 0.0, -100.0 })
        @DisplayName("Should delegate invalid rental rate validation to Model and preserve owner state")
        void createProperty_invalidRentalRate_throwsException(double invalidRate) {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.createProperty(owner, 101, "Apartment A101", invalidRate));
            assertTrue(owner.getProperties().isEmpty());
        }
    }

    @Nested
    @DisplayName("rentProperty Operation Tests")
    class RentPropertyTests {

        private Property property;

        @BeforeEach
        void setUpProperty() {
            property = controller.createProperty(owner, 101, "Apartment A101", 5000.0);
        }

        @Test
        @DisplayName("Should successfully create rental contract and update property availability")
        void rentProperty_successfulRental() {
            RentalContract contract = controller.rentProperty(tenant, 1001, property, startDate, endDate);

            assertNotNull(contract);
            assertEquals(1001, contract.getContractID());
            assertSame(property, contract.getProperty());
            assertSame(tenant, contract.getTenant());
            assertEquals(startDate, contract.getStartDate());
            assertEquals(endDate, contract.getEndDate());
            assertEquals(5000.0, contract.getRentAmount());
            assertTrue(contract.isActive());
            assertFalse(property.isAvailable());
            assertTrue(tenant.getRentalContracts().contains(contract));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when tenant is null")
        void rentProperty_nullTenant_throwsException() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> controller.rentProperty(null, 1001, property, startDate, endDate));
            assertEquals("Tenant must not be null.", ex.getMessage());
            assertTrue(property.isAvailable());
        }

        @Test
        @DisplayName("Should delegate unavailable property check to Model and reject second rental")
        void rentProperty_unavailableProperty_throwsException() {
            // First rental succeeds
            controller.rentProperty(tenant, 1001, property, startDate, endDate);

            Tenant anotherTenant = new Tenant(3, "Bob", "Smith", "bob@example.com");

            // Second rental must fail
            assertThrows(IllegalStateException.class,
                    () -> controller.rentProperty(anotherTenant, 1002, property, startDate, endDate));

            assertTrue(anotherTenant.getRentalContracts().isEmpty());
            assertEquals(1, tenant.getRentalContracts().size());
        }

        @Test
        @DisplayName("Should delegate reversed dates check to Model without mutating property state")
        void rentProperty_reversedDates_throwsException() {
            LocalDate invalidStart = LocalDate.of(2027, 10, 31);
            LocalDate invalidEnd = LocalDate.of(2026, 11, 1);

            assertThrows(IllegalArgumentException.class,
                    () -> controller.rentProperty(tenant, 1001, property, invalidStart, invalidEnd));

            assertTrue(tenant.getRentalContracts().isEmpty());
            assertTrue(property.isAvailable());
        }

        @Test
        @DisplayName("Should delegate null property check to Model")
        void rentProperty_nullProperty_throwsException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.rentProperty(tenant, 1001, null, startDate, endDate));
            assertTrue(tenant.getRentalContracts().isEmpty());
        }

        @ParameterizedTest
        @ValueSource(ints = { 0, -1, -99 })
        @DisplayName("Should delegate invalid contract ID to Model and preserve property availability")
        void rentProperty_invalidContractId_throwsException(int invalidContractId) {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.rentProperty(tenant, invalidContractId, property, startDate, endDate));

            assertTrue(tenant.getRentalContracts().isEmpty());
            assertTrue(property.isAvailable());
        }
    }

    @Nested
    @DisplayName("terminateContract Operation Tests")
    class TerminateContractTests {

        private Property property;
        private RentalContract contract;

        @BeforeEach
        void setUpContract() {
            property = controller.createProperty(owner, 101, "Apartment A101", 5000.0);
            contract = controller.rentProperty(tenant, 1001, property, startDate, endDate);
        }

        @Test
        @DisplayName("Should successfully terminate active contract and make property available again")
        void terminateContract_successfulTermination() {
            assertTrue(contract.isActive());
            assertFalse(property.isAvailable());

            controller.terminateContract(contract);

            assertFalse(contract.isActive());
            assertTrue(property.isAvailable());
            assertTrue(tenant.getRentalContracts().contains(contract));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when contract is null")
        void terminateContract_nullContract_throwsException() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> controller.terminateContract(null));
            assertEquals("RentalContract must not be null.", ex.getMessage());
        }

        @Test
        @DisplayName("Terminating an already inactive contract should not affect property state")
        void terminateContract_alreadyInactive_idempotent() {
            controller.terminateContract(contract);
            assertFalse(contract.isActive());
            assertTrue(property.isAvailable());

            // Re-rent the property with a new contract
            Tenant newTenant = new Tenant(3, "Linh", "Vu", "linh@example.com");
            RentalContract contract2 = controller.rentProperty(newTenant, 1002, property, startDate, endDate);
            assertFalse(property.isAvailable());
            assertTrue(contract2.isActive());

            // Terminating the old inactive contract must not release the property
            controller.terminateContract(contract);
            assertFalse(property.isAvailable());
            assertTrue(contract2.isActive());
        }
    }

    @Nested
    @DisplayName("MVC Full Flow & Resilience Tests")
    class FullFlowTests {

        @Test
        @DisplayName("Demonstrate complete flow: create property -> rent it -> terminate contract")
        void completeFlow_availabilityTransitions() {
            // Step 1: Create property (availability = true)
            Property property = controller.createProperty(owner, 101, "Villa Beachfront", 8000.0);
            assertTrue(property.isAvailable(), "Property should be available after creation");

            // Step 2: Rent property (availability: true -> false)
            RentalContract contract = controller.rentProperty(tenant, 2001, property, startDate, endDate);
            assertFalse(property.isAvailable(), "Property should be unavailable after renting");
            assertTrue(contract.isActive());

            // Step 3: Terminate contract (availability: false -> true)
            controller.terminateContract(contract);
            assertTrue(property.isAvailable(), "Property should be available after termination");
            assertFalse(contract.isActive());
            assertTrue(tenant.getRentalContracts().contains(contract), "Contract should remain in history");
        }

        @Test
        @DisplayName("Demonstrate rejection of invalid operations without corrupting state")
        void invalidOperations_rejectedWithoutCorruptingState() {
            // Invalid rate = 0
            assertThrows(IllegalArgumentException.class,
                    () -> controller.createProperty(owner, 102, "Studio", 0.0));
            assertEquals(0, owner.getProperties().size());

            // Valid property created
            Property property = controller.createProperty(owner, 102, "Studio", 3000.0);
            assertEquals(1, owner.getProperties().size());

            // Invalid dates: endDate before startDate
            LocalDate invalidStart = LocalDate.of(2027, 5, 1);
            LocalDate invalidEnd = LocalDate.of(2027, 4, 1);
            assertThrows(IllegalArgumentException.class,
                    () -> controller.rentProperty(tenant, 2002, property, invalidStart, invalidEnd));

            // State remains uncorrupted: property is still available, tenant has 0
            // contracts
            assertTrue(property.isAvailable());
            assertEquals(0, tenant.getRentalContracts().size());
        }
    }
}

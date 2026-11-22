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
    @DisplayName("Owner and tenant registration")
    class RegistrationTests {

        @Test
        @DisplayName("Should register owner and tenant by their own user ID")
        void register_success() {
            controller.addOwner(owner);
            controller.addTenant(tenant);

            assertSame(owner, controller.getOwner(1));
            assertSame(tenant, controller.getTenant(2));
            assertEquals(1, controller.getAllOwners().size());
            assertEquals(1, controller.getAllTenants().size());
        }

        @Test
        @DisplayName("Should return null for unknown owner/tenant ID")
        void get_unknownId_returnsNull() {
            assertNull(controller.getOwner(999));
            assertNull(controller.getTenant(999));
        }

        @Test
        @DisplayName("Should throw NullPointerException with message when owner is null")
        void addOwner_null_throwsException() {
            NullPointerException ex = assertThrows(NullPointerException.class,
                    () -> controller.addOwner(null));
            assertEquals("PropertyOwner must not be null.", ex.getMessage());
        }

        @Test
        @DisplayName("Should throw NullPointerException with message when tenant is null")
        void addTenant_null_throwsException() {
            NullPointerException ex = assertThrows(NullPointerException.class,
                    () -> controller.addTenant(null));
            assertEquals("Tenant must not be null.", ex.getMessage());
        }

        @Test
        @DisplayName("Should reject duplicate owner ID without overwriting the existing one")
        void addOwner_duplicateId_throwsException() {
            controller.addOwner(owner);
            PropertyOwner other = new PropertyOwner(1, "Other", "Person", "other@example.com");

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> controller.addOwner(other));
            assertEquals("PropertyOwner with ID 1 already exists.", ex.getMessage());
            assertSame(owner, controller.getOwner(1));
        }

        @Test
        @DisplayName("Should reject duplicate tenant ID without overwriting the existing one")
        void addTenant_duplicateId_throwsException() {
            controller.addTenant(tenant);
            Tenant other = new Tenant(2, "Other", "Person", "other@example.com");

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> controller.addTenant(other));
            assertEquals("Tenant with ID 2 already exists.", ex.getMessage());
            assertSame(tenant, controller.getTenant(2));
        }

        @Test
        @DisplayName("Returned lists should be unmodifiable")
        void getAll_isUnmodifiable() {
            controller.addOwner(owner);
            controller.addTenant(tenant);

            assertThrows(UnsupportedOperationException.class,
                    () -> controller.getAllOwners().clear());
            assertThrows(UnsupportedOperationException.class,
                    () -> controller.getAllTenants().clear());
        }
    }

    @Nested
    @DisplayName("createProperty Operation Tests")
    class CreatePropertyTests {

        @Test
        @DisplayName("Should create property, register it with owner and store it in controller")
        void createProperty_successfulCreation() {
            Property property = controller.createProperty(owner, 101, "Apartment A101, two bedrooms", 5000.0);

            assertNotNull(property);
            assertEquals(101, property.getPropertyID());
            assertEquals("Apartment A101, two bedrooms", property.getDescription());
            assertEquals(5000.0, property.getRentalRate());
            assertTrue(property.isAvailable());
            assertTrue(owner.getProperties().contains(property));
            assertEquals(1, owner.getProperties().size());

            assertSame(property, controller.getProperty(101));
            assertEquals(1, controller.getAllProperties().size());
        }

        @Test
        @DisplayName("Should keep insertion order in getAllProperties")
        void createProperty_keepsInsertionOrder() {
            Property p1 = controller.createProperty(owner, 103, "C", 3000.0);
            Property p2 = controller.createProperty(owner, 101, "A", 1000.0);
            Property p3 = controller.createProperty(owner, 102, "B", 2000.0);

            assertEquals(java.util.List.of(p1, p2, p3), controller.getAllProperties());
        }

        @Test
        @DisplayName("Should throw NullPointerException with message when owner is null")
        void createProperty_nullOwner_throwsException() {
            NullPointerException ex = assertThrows(NullPointerException.class,
                    () -> controller.createProperty(null, 101, "Apartment A101", 5000.0));
            assertEquals("PropertyOwner must not be null.", ex.getMessage());
            assertTrue(controller.getAllProperties().isEmpty());
        }

        @Test
        @DisplayName("Should reject duplicate property ID before calling the Model (no orphan object)")
        void createProperty_duplicateId_throwsException() {
            controller.createProperty(owner, 101, "First", 5000.0);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> controller.createProperty(owner, 101, "Second", 6000.0));
            assertEquals("Property with ID 101 already exists.", ex.getMessage());

            assertEquals(1, owner.getProperties().size());
            assertEquals("First", controller.getProperty(101).getDescription());
            assertEquals(1, controller.getAllProperties().size());
        }

        @ParameterizedTest
        @ValueSource(ints = { 0, -1, -50 })
        @DisplayName("Should delegate invalid property ID to Model and store nothing")
        void createProperty_invalidId_throwsException(int invalidId) {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.createProperty(owner, invalidId, "Apartment A101", 5000.0));
            assertTrue(owner.getProperties().isEmpty());
            assertTrue(controller.getAllProperties().isEmpty());
        }

        @Test
        @DisplayName("Should delegate blank description to Model and store nothing")
        void createProperty_blankDescription_throwsException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.createProperty(owner, 101, "   ", 5000.0));
            assertTrue(owner.getProperties().isEmpty());
            assertTrue(controller.getAllProperties().isEmpty());
        }

        @ParameterizedTest
        @ValueSource(doubles = { 0.0, -100.0 })
        @DisplayName("Should delegate invalid rental rate to Model and store nothing")
        void createProperty_invalidRentalRate_throwsException(double invalidRate) {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.createProperty(owner, 101, "Apartment A101", invalidRate));
            assertTrue(owner.getProperties().isEmpty());
            assertTrue(controller.getAllProperties().isEmpty());
        }

        @Test
        @DisplayName("A failed creation should not block reusing the same ID later")
        void createProperty_failedAttemptDoesNotReserveId() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.createProperty(owner, 101, "Apartment", 0.0));

            Property property = controller.createProperty(owner, 101, "Apartment", 5000.0);
            assertSame(property, controller.getProperty(101));
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
        @DisplayName("Should create contract, update availability and store contract in controller")
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

            assertSame(contract, controller.getContract(1001));
            assertEquals(1, controller.getAllContracts().size());
        }

        @Test
        @DisplayName("Should throw NullPointerException with message when tenant is null")
        void rentProperty_nullTenant_throwsException() {
            NullPointerException ex = assertThrows(NullPointerException.class,
                    () -> controller.rentProperty(null, 1001, property, startDate, endDate));
            assertEquals("Tenant must not be null.", ex.getMessage());
            assertTrue(property.isAvailable());
            assertTrue(controller.getAllContracts().isEmpty());
        }

        @Test
        @DisplayName("Should reject duplicate contract ID before calling the Model")
        void rentProperty_duplicateContractId_throwsException() {
            controller.rentProperty(tenant, 1001, property, startDate, endDate);
            Property another = controller.createProperty(owner, 102, "Apartment A102", 4000.0);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> controller.rentProperty(tenant, 1001, another, startDate, endDate));
            assertEquals("RentalContract with ID 1001 already exists.", ex.getMessage());

            assertTrue(another.isAvailable());
            assertEquals(1, tenant.getRentalContracts().size());
            assertEquals(1, controller.getAllContracts().size());
        }

        @Test
        @DisplayName("Should delegate unavailable property check to Model and store nothing")
        void rentProperty_unavailableProperty_throwsException() {
            controller.rentProperty(tenant, 1001, property, startDate, endDate);
            Tenant anotherTenant = new Tenant(3, "Bob", "Smith", "bob@example.com");

            assertThrows(IllegalStateException.class,
                    () -> controller.rentProperty(anotherTenant, 1002, property, startDate, endDate));

            assertTrue(anotherTenant.getRentalContracts().isEmpty());
            assertEquals(1, tenant.getRentalContracts().size());
            assertNull(controller.getContract(1002));
            assertEquals(1, controller.getAllContracts().size());
        }

        @Test
        @DisplayName("Should delegate reversed dates check to Model without changing state")
        void rentProperty_reversedDates_throwsException() {
            LocalDate invalidStart = LocalDate.of(2027, 10, 31);
            LocalDate invalidEnd = LocalDate.of(2026, 11, 1);

            assertThrows(IllegalArgumentException.class,
                    () -> controller.rentProperty(tenant, 1001, property, invalidStart, invalidEnd));

            assertTrue(tenant.getRentalContracts().isEmpty());
            assertTrue(property.isAvailable());
            assertTrue(controller.getAllContracts().isEmpty());
        }

        @Test
        @DisplayName("Should delegate null property check to Model")
        void rentProperty_nullProperty_throwsException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.rentProperty(tenant, 1001, null, startDate, endDate));
            assertTrue(tenant.getRentalContracts().isEmpty());
            assertTrue(controller.getAllContracts().isEmpty());
        }

        @ParameterizedTest
        @ValueSource(ints = { 0, -1, -99 })
        @DisplayName("Should delegate invalid contract ID to Model and preserve availability")
        void rentProperty_invalidContractId_throwsException(int invalidContractId) {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.rentProperty(tenant, invalidContractId, property, startDate, endDate));

            assertTrue(tenant.getRentalContracts().isEmpty());
            assertTrue(property.isAvailable());
            assertTrue(controller.getAllContracts().isEmpty());
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
        @DisplayName("Should terminate active contract, free the property and keep contract as history")
        void terminateContract_successfulTermination() {
            controller.terminateContract(contract);

            assertFalse(contract.isActive());
            assertTrue(property.isAvailable());
            assertTrue(tenant.getRentalContracts().contains(contract));
            assertSame(contract, controller.getContract(1001));
        }

        @Test
        @DisplayName("Should throw NullPointerException with message when contract is null")
        void terminateContract_nullContract_throwsException() {
            NullPointerException ex = assertThrows(NullPointerException.class,
                    () -> controller.terminateContract(null));
            assertEquals("RentalContract must not be null.", ex.getMessage());
        }

        @Test
        @DisplayName("Terminating an old inactive contract must not release a re-rented property")
        void terminateContract_alreadyInactive_idempotent() {
            controller.terminateContract(contract);
            assertTrue(property.isAvailable());

            Tenant newTenant = new Tenant(3, "Linh", "Vu", "linh@example.com");
            RentalContract contract2 = controller.rentProperty(newTenant, 1002, property, startDate, endDate);
            assertFalse(property.isAvailable());

            controller.terminateContract(contract);
            assertFalse(property.isAvailable());
            assertTrue(contract2.isActive());
        }
    }

    @Nested
    @DisplayName("Lookup and list tests")
    class LookupTests {

        @Test
        @DisplayName("Should return null for unknown property/contract ID and empty lists initially")
        void lookups_whenEmpty() {
            assertNull(controller.getProperty(1));
            assertNull(controller.getContract(1));
            assertTrue(controller.getAllOwners().isEmpty());
            assertTrue(controller.getAllTenants().isEmpty());
            assertTrue(controller.getAllProperties().isEmpty());
            assertTrue(controller.getAllContracts().isEmpty());
        }

        @Test
        @DisplayName("Returned property and contract lists should be unmodifiable")
        void getAll_isUnmodifiable() {
            Property property = controller.createProperty(owner, 101, "A", 1000.0);
            controller.rentProperty(tenant, 1001, property, startDate, endDate);

            assertThrows(UnsupportedOperationException.class,
                    () -> controller.getAllProperties().clear());
            assertThrows(UnsupportedOperationException.class,
                    () -> controller.getAllContracts().clear());
        }

        @Test
        @DisplayName("Returned list is a snapshot and does not change when storage changes later")
        void getAll_isSnapshot() {
            controller.createProperty(owner, 101, "A", 1000.0);
            var snapshot = controller.getAllProperties();

            controller.createProperty(owner, 102, "B", 2000.0);

            assertEquals(1, snapshot.size());
            assertEquals(2, controller.getAllProperties().size());
        }
    }

    @Nested
    @DisplayName("Deprecated PascalCase methods")
    @SuppressWarnings("deprecation")
    class DeprecatedMethodTests {

        @Test
        @DisplayName("Deprecated methods should delegate to the new methods")
        void deprecatedMethods_delegate() {
            Property property = controller.CreateProperty(owner, 101, "Apartment", 5000.0);
            assertSame(property, controller.getProperty(101));

            RentalContract contract = controller.RentProperty(tenant, 1001, property, startDate, endDate);
            assertSame(contract, controller.getContract(1001));
            assertFalse(property.isAvailable());

            controller.TerminateContract(contract);
            assertFalse(contract.isActive());
            assertTrue(property.isAvailable());
        }
    }

    @Nested
    @DisplayName("MVC Full Flow Tests")
    class FullFlowTests {

        @Test
        @DisplayName("Complete flow: create property -> rent it -> terminate contract")
        void completeFlow_availabilityTransitions() {
            Property property = controller.createProperty(owner, 101, "Villa Beachfront", 8000.0);
            assertTrue(property.isAvailable());

            RentalContract contract = controller.rentProperty(tenant, 2001, property, startDate, endDate);
            assertFalse(property.isAvailable());
            assertTrue(contract.isActive());

            controller.terminateContract(contract);
            assertTrue(property.isAvailable());
            assertFalse(contract.isActive());
            assertTrue(tenant.getRentalContracts().contains(contract));
        }

        @Test
        @DisplayName("Invalid operations are rejected without corrupting state")
        void invalidOperations_rejectedWithoutCorruptingState() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.createProperty(owner, 102, "Studio", 0.0));
            assertEquals(0, owner.getProperties().size());

            Property property = controller.createProperty(owner, 102, "Studio", 3000.0);
            assertEquals(1, owner.getProperties().size());

            LocalDate invalidStart = LocalDate.of(2027, 5, 1);
            LocalDate invalidEnd = LocalDate.of(2027, 4, 1);
            assertThrows(IllegalArgumentException.class,
                    () -> controller.rentProperty(tenant, 2002, property, invalidStart, invalidEnd));

            assertTrue(property.isAvailable());
            assertEquals(0, tenant.getRentalContracts().size());
            assertTrue(controller.getAllContracts().isEmpty());
        }
    }
}
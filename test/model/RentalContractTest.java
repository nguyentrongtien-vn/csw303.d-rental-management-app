package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RentalContract Model Unit Tests")
class RentalContractTest {

    private Property property;
    private Tenant tenant;
    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        // Prepare valid initial test fixtures before each test execution
        property = new Property(101, "Apartment A101", 5000.50);
        tenant = new Tenant(1, "Anna", "Nguyen", "anna@example.com");
        startDate = LocalDate.of(2026, 11, 1);
        endDate = LocalDate.of(2027, 10, 31);
    }

    @Nested
    @DisplayName("Constructor Successful Creation Tests")
    class ValidCreationTests {

        @Test
        @DisplayName("Should create contract and properly update property state")
        void validCreation_updatesStatesCorrectly() {
            RentalContract contract = new RentalContract(1, property, tenant, startDate, endDate);

            // Verify contract fields and side-effect on property availability
            assertEquals(1, contract.getContractID());
            assertTrue(contract.isActive());
            assertFalse(property.isAvailable());
            assertEquals(5000.50, contract.getRentAmount()); // Copied from property.rentalRate
        }
    }

    @Nested
    @DisplayName("Constructor Validation & State Preservation Tests")
    class InvalidCreationTests {

        @ParameterizedTest
        @ValueSource(ints = { 0, -1, -100 })
        @DisplayName("Should throw exception for non-positive contractID and preserve property state")
        void invalidContractId_throwsExceptionAndPreservesState(int invalidId) {
            assertThrows(IllegalArgumentException.class,
                    () -> new RentalContract(invalidId, property, tenant, startDate, endDate));

            // Critical: Ensure property state is not mutated on failure
            assertTrue(property.isAvailable());
        }

        @Test
        @DisplayName("Should throw exception when endDate is not strictly after startDate")
        void endDateNotAfterStartDate_throwsExceptionAndPreservesState() {
            // Case 1: Equal dates
            assertThrows(IllegalArgumentException.class,
                    () -> new RentalContract(1, property, tenant, startDate, startDate));

            // Case 2: Reversed dates (endDate before startDate)
            assertThrows(IllegalArgumentException.class,
                    () -> new RentalContract(1, property, tenant, endDate, startDate));

            // Critical: Ensure property state is preserved
            assertTrue(property.isAvailable());
        }

        @Test
        @DisplayName("Should throw exception when target property is already unavailable")
        void alreadyRentedProperty_throwsException() {
            // Arrange: Create first contract to occupy the property
            new RentalContract(1, property, tenant, startDate, endDate);
            assertFalse(property.isAvailable());

            // Catch the specific exception defined in your codebase
            // (IllegalArgumentException or IllegalStateException)
            assertThrows(Exception.class,
                    () -> new RentalContract(2, property, tenant, startDate, endDate));
        }

        @Test
        @DisplayName("Should throw exception when property or tenant is null")
        void nullPropertyOrTenant_throwsException() {
            assertThrows(IllegalArgumentException.class,
                    () -> new RentalContract(1, null, tenant, startDate, endDate));

            assertThrows(IllegalArgumentException.class,
                    () -> new RentalContract(1, property, null, startDate, endDate));

            assertTrue(property.isAvailable());
        }

        @Test
        @DisplayName("Should throw exception when startDate or endDate is null")
        void nullDates_throwsException() {
            assertThrows(IllegalArgumentException.class,
                    () -> new RentalContract(1, property, tenant, null, endDate));

            assertThrows(IllegalArgumentException.class,
                    () -> new RentalContract(1, property, tenant, startDate, null));

            assertTrue(property.isAvailable());
        }
    }

    @Nested
    @DisplayName("Contract Termination Tests")
    class TerminationTests {

        @Test
        @DisplayName("Should set isActive to false and revert property availability to true")
        void terminate_activeContract_success() {
            RentalContract contract = new RentalContract(1, property, tenant, startDate, endDate);
            assertFalse(property.isAvailable());

            contract.terminate();

            // Verify contract is deactivated and property becomes available again
            assertFalse(contract.isActive());
            assertTrue(property.isAvailable());
        }
    }
}
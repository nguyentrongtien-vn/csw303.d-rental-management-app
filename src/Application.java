import java.time.LocalDate;

import controller.RentalController;
import model.Property;
import model.PropertyOwner;
import model.RentalContract;
import model.Tenant;

public class Application {
        public static void main(String[] args) {
                RentalController controller = new RentalController();

                PropertyOwner owner = new PropertyOwner(1, "Anna", "Nguyen", "anna@example.com");
                Tenant tenant = new Tenant(2, "Minh", "Tran", "minh@example.com");
                LocalDate start = LocalDate.of(2026, 11, 1);
                LocalDate end = LocalDate.of(2027, 10, 31);

                // createProperty via controller: the property is registered with its owner
                Property property = controller.createProperty(owner, 101, "Apartment A101, two bedrooms", 5000);
                require(owner.getProperties().contains(property), "createProperty should register the property.");
                require(property.isAvailable(), "New property should be available.");

                // rentProperty via controller: contract is created, stored, and the property
                // becomes unavailable
                RentalContract contract = controller.rentProperty(tenant, 1001, property, start, end);
                require(tenant.getRentalContracts().contains(contract), "rentProperty should store the contract.");
                require(contract.isActive(), "A new contract should be active.");
                require(!property.isAvailable(), "Renting should make the property unavailable.");
                require(contract.getRentAmount() == 5000, "RentAmount should copy the property rate.");

                // UpdateRentalRate: the agreed contract rent must not change
                property.UpdateRentalRate(5500);
                require(contract.getRentAmount() == 5000, "RentAmount should stay 5000.");

                // terminateContract via controller: property is released, contract stays in
                // history
                controller.terminateContract(contract);
                require(!contract.isActive(), "terminateContract should deactivate the contract.");
                require(property.isAvailable(), "terminateContract should release the property.");
                require(tenant.getRentalContracts().contains(contract), "Contract should stay in history.");

                // rentProperty on an unavailable property must fail
                RentalContract contract2 = controller.rentProperty(tenant, 1002, property, start, end);
                boolean rejectedUnavailable = false;
                try {
                        controller.rentProperty(tenant, 1003, property, start, end);
                } catch (RuntimeException e) {
                        rejectedUnavailable = true;
                }
                require(rejectedUnavailable, "An unavailable property must not be rented again.");
                require(tenant.getRentalContracts().size() == 2, "A failed rental must not be stored.");

                // terminateContract on an old contract must not release a re-rented property
                controller.terminateContract(contract);
                require(!property.isAvailable(), "Old contract must not release the property.");
                require(contract2.isActive(), "The new contract should still be active.");

                // Terminate active contract2 to release property
                controller.terminateContract(contract2);
                require(!contract2.isActive(), "terminateContract should deactivate contract2.");
                require(property.isAvailable(), "terminateContract should release property again.");

                // Demonstrate invalid operations rejected by model through controller (Week 4
                // requirement)
                boolean rejectedInvalidRate = false;
                try {
                        controller.createProperty(owner, 102, "Studio", 0);
                } catch (IllegalArgumentException e) {
                        rejectedInvalidRate = true;
                }
                require(rejectedInvalidRate, "Invalid rental rate must be rejected.");

                boolean rejectedReversedDates = false;
                try {
                        controller.rentProperty(tenant, 1004, property, end, start);
                } catch (IllegalArgumentException e) {
                        rejectedReversedDates = true;
                }
                require(rejectedReversedDates, "Reversed rental dates must be rejected.");
                require(property.isAvailable(), "Failed rental must not corrupt property availability.");

                System.out.println("All checks passed.");
        }

        private static void require(boolean condition, String message) {
                if (!condition) {
                        throw new AssertionError(message);
                }
        }
}

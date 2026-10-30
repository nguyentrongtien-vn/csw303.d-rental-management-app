import java.time.LocalDate;

public class Application {
        public static void main(String[] args) {
                PropertyOwner owner = new PropertyOwner(1, "Anna", "Nguyen", "anna@example.com");
                Tenant tenant = new Tenant(2, "Minh", "Tran", "minh@example.com");
                LocalDate start = LocalDate.of(2026, 11, 1);
                LocalDate end = LocalDate.of(2027, 10, 31);

                // CreateProperty: the property is registered with its owner
                Property property = owner.CreateProperty(101, "Apartment A101, two bedrooms", 5000);
                require(owner.GetProperties().contains(property), "CreateProperty should register the property.");

                // rentProperty: contract is created, stored, and the property becomes
                // unavailable
                RentalContract contract = tenant.rentProperty(1001, property, start, end);
                require(tenant.getRentalContracts().contains(contract), "rentProperty should store the contract.");
                require(contract.isActive(), "A new contract should be active.");
                require(!property.isAvailable(), "Renting should make the property unavailable.");
                require(contract.getRentAmount() == 5000, "RentAmount should copy the property rate.");

                // UpdateRentalRate: the agreed contract rent must not change
                property.UpdateRentalRate(5500);
                require(contract.getRentAmount() == 5000, "RentAmount should stay 5000.");

                // terminate: property is released, contract stays in history
                contract.terminate();
                require(!contract.isActive(), "terminate should deactivate the contract.");
                require(property.isAvailable(), "terminate should release the property.");
                require(tenant.getRentalContracts().contains(contract), "Contract should stay in history.");

                // rentProperty on an unavailable property must fail
                RentalContract contract2 = tenant.rentProperty(1002, property, start, end);
                boolean rejected = false;
                try {
                        tenant.rentProperty(1003, property, start, end);
                } catch (RuntimeException e) {
                        rejected = true;
                }
                require(rejected, "An unavailable property must not be rented again.");
                require(tenant.getRentalContracts().size() == 2, "A failed rental must not be stored.");

                // terminate on an old contract must not release a re-rented property
                contract.terminate();
                require(!property.isAvailable(), "Old contract must not release the property.");
                require(contract2.isActive(), "The new contract should still be active.");

                System.out.println("All checks passed.");
        }

        private static void require(boolean condition, String message) {
                if (!condition) {
                        throw new AssertionError(message);
                }
        }
}
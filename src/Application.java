import java.time.LocalDate;

public class Application {
        public static void main(String[] args) {
                
                // 1. Create a property owner
                PropertyOwner owner = new PropertyOwner(
                                1, "Anna", "Nguyen", "anna@example.com");

                // 2. Create a tenant
                Tenant tenant = new Tenant(
                                2, "Minh", "Tran", "minh@example.com");

                // 3. Create a property
                Property property = owner.CreateProperty(
                                101, "Apartment A101, two bedrooms", 5000);

                if (property == null) {
                        System.out.println("Failed to create property.");
                        return;
                }

                // 4. Create a rental contract
                RentalContract contract = tenant.rentProperty(
                                1001,
                                property,
                                LocalDate.of(2026, 11, 1),
                                LocalDate.of(2027, 10, 31));

                // 5. Display information
                System.out.println("Owner: " + owner.getFullName());
                System.out.println("Tenant: " + tenant.getFullName());

                System.out.println(
                                "Number of properties: " + owner.GetProperties().size());

                System.out.println(
                                "Number of rental contracts: "
                                                + tenant.getRentalContracts().size());

                System.out.println("Property available: " + property.isAvailable());
                System.out.println("Contract active: " + contract.isActive());
                System.out.println("Rent amount: " + contract.getRentAmount());

                // 6. Update the property's rental rate
                property.UpdateRentalRate(5500);

                System.out.println(
                                "Updated property rate: " + property.getRentalRate());

                System.out.println(
                                "Contract rent amount: " + contract.getRentAmount());

                // 7. Terminate the contract
                contract.terminate();

                System.out.println(
                                "Contract active after termination: " + contract.isActive());

                System.out.println(
                                "Property available after termination: "
                                                + property.isAvailable());

                System.out.println(
                                "Contracts after termination: "
                                                + tenant.getRentalContracts().size());
        }
}
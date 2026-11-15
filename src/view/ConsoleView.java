package view;

import controller.RentalController;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import model.Property;
import model.PropertyOwner;
import model.RentalContract;
import model.Tenant;

public class ConsoleView {
    private final RentalController controller;
    private final Scanner scanner;

    // Object references kept in memory (persistence is not required)
    private final PropertyOwner owner;
    private final Tenant tenant;
    private final List<Property> properties = new ArrayList<>();
    private final List<RentalContract> contracts = new ArrayList<>();

    public ConsoleView(RentalController controller) {
        this(controller,
                new PropertyOwner(1, "Default", "Owner", "owner@example.com"),
                new Tenant(1, "Default", "Tenant", "tenant@example.com"),
                System.in);
    }

    public ConsoleView(RentalController controller, PropertyOwner owner, Tenant tenant) {
        this(controller, owner, tenant, System.in);
    }

    public ConsoleView(RentalController controller, PropertyOwner owner, Tenant tenant,
            java.io.InputStream in) {
        this.controller = controller;
        this.owner = owner;
        this.tenant = tenant;
        this.scanner = new Scanner(in);
    }

    public void start() {
        boolean running = true;
        while (running) {
            if (!scanner.hasNextLine()) {
                break;
            }
            try {
                System.out.println("1. Create property 2. Rent 3. Terminate 4. Exit");
                System.out.print("Choice: ");
                String choice = scanner.nextLine().trim();
                switch (choice) {
                    case "1" -> createProperty();
                    case "2" -> rentProperty();
                    case "3" -> terminateContract();
                    case "4" -> {
                        running = false;
                        System.out.println("Goodbye.");
                    }
                    default -> System.out.println("Invalid choice. Enter 1-4.");
                }
            } catch (NoSuchElementException e) {
                break;
            } catch (Exception e) {
                // Model rejected the operation: show the error message, keep running
                String msg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
                System.out.println("Error: " + msg);
            }
        }
    }

    private void createProperty() {
        int id = readInt("Property id: ");
        System.out.print("Description: ");
        String description = scanner.nextLine().trim();
        double rate = readDouble("Rental rate: ");

        Property p = controller.createProperty(owner, id, description, rate);
        if (p == null) {
            System.out.println("Error: could not create property.");
            return;
        }
        properties.add(p);
        System.out.println("Created property " + id + ". Available: " + p.isAvailable());
    }

    private void rentProperty() {
        if (properties.isEmpty()) {
            System.out.println("No properties yet. Create one first.");
            return;
        }
        int contractId = readInt("Contract id: ");
        Property p = pickProperty();
        if (p == null) {
            return;
        }

        LocalDate start = readDate("Start date (yyyy-mm-dd): ");
        LocalDate end = readDate("End date (yyyy-mm-dd): ");

        RentalContract c = controller.rentProperty(tenant, contractId, p, start, end);
        if (c == null) {
            System.out.println("Error: could not create rental contract.");
            return;
        }
        contracts.add(c);
        System.out.println("Rented contract #" + contractId
                + ". Available: " + p.isAvailable());
    }

    private void terminateContract() {
        if (contracts.isEmpty()) {
            System.out.println("No contracts yet.");
            return;
        }
        for (RentalContract c : contracts) {
            System.out.println("[" + c.getContractID() + "] Property #" + c.getProperty().getPropertyID()
                    + " (" + c.getProperty().getDescription() + ")"
                    + " - Rent: " + c.getRentAmount()
                    + ", Active: " + c.isActive());
        }
        int input = readInt("Contract id: ");
        RentalContract target = null;
        for (RentalContract c : contracts) {
            if (c.getContractID() == input) {
                target = c;
                break;
            }
        }
        if (target == null && input >= 0 && input < contracts.size()) {
            target = contracts.get(input);
        }
        if (target == null) {
            System.out.println("Invalid contract number.");
            return;
        }
        controller.terminateContract(target);
        System.out.println("Contract terminated.");
        showProperties(); // shows IsAvailable after termination
    }

    private void showProperties() {
        for (Property p : properties) {
            System.out.println("[" + p.getPropertyID() + "] " + p.getDescription()
                    + " (rate: " + p.getRentalRate() + ", available: " + p.isAvailable() + ")");
        }
    }

    private Property pickProperty() {
        if (properties.isEmpty()) {
            System.out.println("No properties yet. Create one first.");
            return null;
        }
        for (Property p : properties) {
            System.out.println("[" + p.getPropertyID() + "] " + p.getDescription()
                    + " (rate: " + p.getRentalRate() + ", available: " + p.isAvailable() + ")");
        }
        int input = readInt("Property id: ");
        for (Property p : properties) {
            if (p.getPropertyID() == input) {
                return p;
            }
        }
        if (input >= 0 && input < properties.size()) {
            return properties.get(input);
        }
        System.out.println("Invalid property number.");
        return null;
    }

    // ---- Input helpers: ask again on malformed input ----
    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return LocalDate.parse(scanner.nextLine().trim());
            } catch (DateTimeParseException | IllegalArgumentException e) {
                System.out.println("Use format yyyy-mm-dd.");
            }
        }
    }
}

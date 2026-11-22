package view;

import controller.RentalController;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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

    // The current user session; all other data lives in the Controller.
    private final PropertyOwner owner;
    private final Tenant tenant;

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
        registerUsers();
    }

    // Registers the session users with the Controller, skipping ones that are
    // already registered so that creating several views does not fail on duplicate IDs.
    private void registerUsers() {
        if (controller.getOwner(owner.getUserId()) == null) {
            controller.addOwner(owner);
        }
        if (controller.getTenant(tenant.getUserId()) == null) {
            controller.addTenant(tenant);
        }
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
                // Controller/Model rejected the operation: show the message, keep running
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
        System.out.println("Created property " + id + ". Available: " + p.isAvailable());
    }

    private void rentProperty() {
        if (controller.getAllProperties().isEmpty()) {
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
        System.out.println("Rented contract #" + c.getContractID()
                + ". Available: " + p.isAvailable());
    }

    private void terminateContract() {
        List<RentalContract> contracts = controller.getAllContracts();
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
        RentalContract target = controller.getContract(input);
        if (target == null && input >= 0 && input < contracts.size()) {
            target = contracts.get(input); // fallback: treat input as list position
        }
        if (target == null) {
            System.out.println("Invalid contract number.");
            return;
        }
        controller.terminateContract(target);
        System.out.println("Contract terminated.");
        showProperties(); // shows availability after termination
    }

    private void showProperties() {
        for (Property p : controller.getAllProperties()) {
            System.out.println("[" + p.getPropertyID() + "] " + p.getDescription()
                    + " (rate: " + p.getRentalRate() + ", available: " + p.isAvailable() + ")");
        }
    }

    private Property pickProperty() {
        List<Property> properties = controller.getAllProperties();
        if (properties.isEmpty()) {
            System.out.println("No properties yet. Create one first.");
            return null;
        }
        showProperties();
        int input = readInt("Property id: ");
        Property p = controller.getProperty(input);
        if (p == null && input >= 0 && input < properties.size()) {
            p = properties.get(input); // fallback: treat input as list position
        }
        if (p == null) {
            System.out.println("Invalid property number.");
        }
        return p;
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
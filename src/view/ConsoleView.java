package view;

import controller.RentalController;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import model.Property;
import model.PropertyOwner;
import model.RentalContract;
import model.Tenant;

public class ConsoleView {
    private final RentalController controller;
    private final Scanner scanner;

    // Object references kept in memory
    private final PropertyOwner owner;
    private final Tenant tenant;
    private final List<Property> properties = new ArrayList<>();
    private final List<RentalContract> contracts = new ArrayList<>();

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
            if (!scanner.hasNextLine())
                break;
            System.out.println("\n1. Create property  2. Rent  3. Terminate  4. Exit");
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim();
            try {
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
            } catch (Exception e) {
                // Model rejected the operation: show the error, keep running
                String msg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
                System.out.println("Error: " + msg);
            }
        }
    }

    private void createProperty() {
        int id = readInt("Property id: ");
        System.out.print("Description: ");
        String description = scanner.nextLine();
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
        Property p = pickProperty();
        if (p == null) return;
        int contractId = readInt("Contract id: ");
        LocalDate start = readDate("Start date (yyyy-mm-dd): ");
        LocalDate end = readDate("End date (yyyy-mm-dd): ");

        RentalContract c = controller.rentProperty(tenant, contractId, p, start, end);
        if (c == null) {
            System.out.println("Error: could not create rental contract.");
            return;
        }
        contracts.add(c);
        System.out.println("Rented. Contract #" + (contracts.size() - 1)
                + ". Available: " + p.isAvailable());
    }

    private void terminateContract() {
        if (contracts.isEmpty()) {
            System.out.println("No contracts yet.");
            return;
        }
        for (int i = 0; i < contracts.size(); i++) {
            System.out.println("[" + i + "] " + contracts.get(i));
        }
        int idx = readInt("Contract number: ");
        if (idx < 0 || idx >= contracts.size()) {
            System.out.println("Invalid contract number.");
            return;
        }
        controller.terminateContract(contracts.get(idx));
        System.out.println("Contract terminated.");
        showProperties(); // shows IsAvailable after termination
    }

    private void showProperties() {
        for (Property p : properties) {
            System.out.println(p + " (available: " + p.isAvailable() + ")");
        }
    }

    private Property pickProperty() {
        if (properties.isEmpty()) {
            System.out.println("No properties yet. Create one first.");
            return null;
        }
        for (int i = 0; i < properties.size(); i++) {
            Property p = properties.get(i);
            System.out.println("[" + i + "] " + p + " (available: " + p.isAvailable() + ")");
        }
        int idx = readInt("Property number: ");
        if (idx < 0 || idx >= properties.size()) {
            System.out.println("Invalid property number.");
            return null;
        }
        return properties.get(idx);
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
            } catch (Exception e) {
                System.out.println("Use format yyyy-mm-dd.");
            }
        }
    }
}
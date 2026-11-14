import controller.RentalController;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import model.Property;
import model.PropertyOwner;
import model.Tenant;
import view.ConsoleView;

public class ViewCheck {
    static PropertyOwner owner;
    static Tenant tenant;
    static String lastOutput = "";

    static final String S = "2026-11-01";
    static final String E = "2027-10-31";
    static final String[] CREATE = { "1", "101", "Apartment A101", "5000" };

    public static void main(String[] args) {
        String out;

        // Valid flow: create -> rent -> terminate (IsAvailable true -> false -> true)
        out = run("1", "101", "Apartment A101", "5000.50",
                "2", "0", "1001", S, E,
                "3", "0");
        require(out.contains("Created property 101. Available: true"), "Create should show Available: true.");
        require(out.contains("Rented. Contract #0. Available: false"), "Rent should show Available: false.");
        require(out.contains("Contract terminated."), "Terminate should confirm.");
        require(out.contains("(available: true)"), "Property should be available after terminate.");
        require(owner.getProperties().size() == 1, "One property should be stored.");
        require(tenant.getRentalContracts().size() == 1, "Contract should stay in history.");
        require(firstProperty().isAvailable(), "Property should be available at the end.");

        // Invalid property: model rejects, no Property appended, View shows error and
        // survives
        String[][] badProperties = {
                { "0", "Apartment A101", "5000" }, // ID = 0
                { "-1", "Apartment A101", "5000" }, // negative ID
                { "101", "   ", "5000" }, // blank description
                { "101", "Apartment A101", "0" }, // rate = 0
                { "101", "Apartment A101", "-50" }, // negative rate
                { "101", "Apartment A101", "NaN" }, // non-finite
                { "101", "Apartment A101", "Infinity" }
        };
        for (String[] row : badProperties) {
            out = run("1", row[0], row[1], row[2]);
            String label = String.join(" | ", row);
            require(out.contains("Error"), "View should show an error for: " + label);
            require(owner.getProperties().isEmpty(), "No property may be appended for: " + label);
        }

        // Invalid rental: model rejects, no contract appended, IsAvailable unchanged
        String[][] badRentals = {
                { "1001", E, S }, // reversed dates
                { "1001", S, S }, // endDate == startDate
                { "-4", S, E } // negative contract ID
        };
        for (String[] row : badRentals) {
            out = run(concat(CREATE, "2", "0", row[0], row[1], row[2]));
            String label = String.join(" | ", row);
            require(out.contains("Error"), "View should show an error for rental: " + label);
            require(tenant.getRentalContracts().isEmpty(), "No contract may be appended for: " + label);
            require(firstProperty().isAvailable(), "Failed rental must not change IsAvailable: " + label);
        }

        // Already rented property
        out = run(concat(CREATE, "2", "0", "1001", S, E, "2", "0", "1002", S, E));
        require(out.contains("Error"), "Renting a rented property should show an error.");
        require(tenant.getRentalContracts().size() == 1, "Failed rental must not be stored.");
        require(!firstProperty().isAvailable(), "Property should stay rented.");

        // Input-format handling inside the View (re-prompt, no crash)
        out = run("1", "abc", "101", "Apartment A101", "5000");
        require(out.contains("Please enter a whole number."), "Non-numeric ID should re-prompt.");
        require(owner.getProperties().size() == 1, "Property should be created after re-prompt.");

        out = run("1", "101", "Apartment A101", "abc", "5000");
        require(out.contains("Please enter a number."), "Non-numeric rate should re-prompt.");
        require(owner.getProperties().size() == 1, "Property should be created after re-prompt.");

        out = run(concat(CREATE, "2", "0", "1001", "01/11/2026", S, E));
        require(out.contains("Use format yyyy-mm-dd."), "Bad date format should re-prompt.");
        require(tenant.getRentalContracts().size() == 1, "Rental should succeed after re-prompt.");

        // Menu edge cases
        require(run("9").contains("Invalid choice"), "Unknown menu choice should be reported.");
        require(run("2").contains("No properties yet"), "Rent without properties should be reported.");
        require(run("3").contains("No contracts yet"), "Terminate without contracts should be reported.");

        System.out.println("All view checks passed.");
    }

    /**
     * Runs ConsoleView with the given input lines (then Exit) and returns what it
     * printed.
     */
    static String run(String... lines) {
        owner = new PropertyOwner(1, "Anna", "Nguyen", "anna@example.com");
        tenant = new Tenant(2, "Minh", "Tran", "minh@example.com");
        String input = String.join("\n", lines) + "\n4\n";

        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
            new ConsoleView(new RentalController(), owner, tenant,
                    new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))).start();
        } finally {
            System.setOut(original);
        }
        lastOutput = buffer.toString(StandardCharsets.UTF_8);
        return lastOutput;
    }

    static String[] concat(String[] first, String... more) {
        String[] all = new String[first.length + more.length];
        System.arraycopy(first, 0, all, 0, first.length);
        System.arraycopy(more, 0, all, first.length, more.length);
        return all;
    }

    static Property firstProperty() {
        return owner.getProperties().iterator().next();
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message + "\n--- View output ---\n" + lastOutput);
        }
    }
}
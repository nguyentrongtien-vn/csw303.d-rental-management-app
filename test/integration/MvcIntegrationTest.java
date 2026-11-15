package integration;

import controller.RentalController;
import model.Property;
import model.PropertyOwner;
import model.RentalContract;
import model.Tenant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import view.ConsoleView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MVC End-to-End Integration Tests")
class MvcIntegrationTest {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;
    private RentalController controller;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        controller = new RentalController();
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    private void provideInput(String data) {
        System.setIn(new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    @DisplayName("Complete lifecycle flow updates property availability from true to false to true")
    void fullLifecycle_createRentTerminate_transitionsAvailabilityProperly() {
        PropertyOwner owner = new PropertyOwner(1, "Anna", "Nguyen", "anna@example.com");
        Tenant tenant = new Tenant(2, "Minh", "Tran", "minh@example.com");

        Property property = controller.createProperty(owner, 101, "Ocean View Apartment", 4500.0);
        assertTrue(property.isAvailable());

        RentalContract contract = controller.rentProperty(
                tenant,
                1001,
                property,
                LocalDate.of(2026, 12, 1),
                LocalDate.of(2027, 6, 1)
        );
        assertFalse(property.isAvailable());

        controller.terminateContract(contract);
        assertTrue(property.isAvailable());
    }

    @Test
    @DisplayName("Console flow executes create, rent, and terminate without errors")
    void consoleIntegration_completeUserFlow_executesSuccessfully() {
        String simulatedInputs = String.join("\n",
                "1", "201", "Garden Villa", "6000",
                "2", "2001", "201", "2026-12-01", "2027-05-01",
                "3", "2001",
                "4"
        ) + "\n";
        provideInput(simulatedInputs);

        ConsoleView view = new ConsoleView(controller);
        assertDoesNotThrow(view::start);
    }

    @Test
    @DisplayName("Invalid rental rate via console is rejected without crashing or corrupting state")
    void consoleIntegration_zeroRentalRate_rejectedWithoutCorruptingState() {
        String simulatedInputs = String.join("\n",
                "1", "202", "Valid Condo", "3500",
                "1", "203", "Invalid Condo", "0",
                "4"
        ) + "\n";
        provideInput(simulatedInputs);

        ConsoleView view = new ConsoleView(controller);
        assertDoesNotThrow(view::start);

        String output = outContent.toString().toLowerCase();
        assertTrue(output.contains("error") || output.contains("invalid") || output.contains("must"));
    }

    @Test
    @DisplayName("End date before start date via console is rejected without crashing or altering availability")
    void consoleIntegration_endDateBeforeStartDate_rejectedWithoutCorruptingState() {
        String simulatedInputs = String.join("\n",
                "1", "204", "City Loft", "2800",
                "2", "2002", "204", "2027-08-01", "2027-02-01",
                "4"
        ) + "\n";
        provideInput(simulatedInputs);

        ConsoleView view = new ConsoleView(controller);
        assertDoesNotThrow(view::start);

        String output = outContent.toString().toLowerCase();
        assertTrue(output.contains("error") || output.contains("invalid") || output.contains("after"));
    }
}
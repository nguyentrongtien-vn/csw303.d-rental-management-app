package view;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import controller.RentalController;
import model.Property;
import model.PropertyOwner;
import model.RentalContract;
import model.Tenant;

@DisplayName("ConsoleView Unit Tests")
class ConsoleViewTest {

    private static final String RENT_DATES = "2026-11-01\n2027-10-31\n";

    private final ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
    private PrintStream originalOut;

    private RentalController controller;
    private PropertyOwner owner;
    private Tenant tenant;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        System.setOut(new PrintStream(outBuffer, true, StandardCharsets.UTF_8));

        controller = new RentalController();
        owner = new PropertyOwner(1, "Anna", "Nguyen", "anna@example.com");
        tenant = new Tenant(2, "Minh", "Tran", "minh@example.com");
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    // Runs the view with the given console input and returns everything it printed.
    private String run(String input) {
        outBuffer.reset();
        ConsoleView view = new ConsoleView(controller, owner, tenant,
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        view.start();
        return outBuffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("Constructor should register owner and tenant in the controller")
    void constructor_registersUsers() {
        run("4\n");

        assertSame(owner, controller.getOwner(1));
        assertSame(tenant, controller.getTenant(2));
    }

    @Test
    @DisplayName("Creating several views on the same controller should not fail on duplicate users")
    void constructor_twoViewsSameController_noException() {
        run("4\n");
        assertDoesNotThrow(() -> run("4\n"));
        assertEquals(1, controller.getAllOwners().size());
        assertEquals(1, controller.getAllTenants().size());
    }

    @Test
    @DisplayName("Exit option should print goodbye")
    void exit_printsGoodbye() {
        String out = run("4\n");
        assertTrue(out.contains("Goodbye."));
    }

    @Test
    @DisplayName("Invalid menu choice should print a hint and keep running")
    void invalidChoice_printsHint() {
        String out = run("9\n4\n");
        assertTrue(out.contains("Invalid choice. Enter 1-4."));
        assertTrue(out.contains("Goodbye."));
    }

    @Test
    @DisplayName("Empty input should end the loop without exception")
    void emptyInput_endsGracefully() {
        assertDoesNotThrow(() -> run(""));
    }

    @Test
    @DisplayName("Input ending in the middle of a prompt should end the loop without exception")
    void inputEndsMidPrompt_endsGracefully() {
        assertDoesNotThrow(() -> run("1\n"));
    }

    // ----- Create property -----

    @Test
    @DisplayName("Create property should store it in the controller and show availability")
    void createProperty_success() {
        String out = run("1\n101\nApartment A101\n5000\n4\n");

        assertTrue(out.contains("Created property 101. Available: true"));
        Property p = controller.getProperty(101);
        assertNotNull(p);
        assertEquals("Apartment A101", p.getDescription());
        assertEquals(5000.0, p.getRentalRate());
        assertTrue(owner.getProperties().contains(p));
    }

    @Test
    @DisplayName("Malformed numbers should be asked again")
    void createProperty_malformedInput_asksAgain() {
        String out = run("1\nabc\n101\nApartment\nxyz\n5000\n4\n");

        assertTrue(out.contains("Please enter a whole number."));
        assertTrue(out.contains("Please enter a number."));
        assertNotNull(controller.getProperty(101));
    }

    @Test
    @DisplayName("Model rejection (invalid rate) should show an error and store nothing")
    void createProperty_invalidRate_showsError() {
        String out = run("1\n101\nApartment\n0\n4\n");

        assertTrue(out.contains("Error:"));
        assertTrue(controller.getAllProperties().isEmpty());
        assertTrue(out.contains("Goodbye."));
    }

    @Test
    @DisplayName("Duplicate property ID should show the controller error and keep the original")
    void createProperty_duplicateId_showsError() {
        String out = run("1\n101\nFirst\n5000\n1\n101\nSecond\n6000\n4\n");

        assertTrue(out.contains("Error: Property with ID 101 already exists."));
        assertEquals(1, controller.getAllProperties().size());
        assertEquals("First", controller.getProperty(101).getDescription());
    }

    // ----- Rent property -----

    @Test
    @DisplayName("Rent with no properties should print a hint")
    void rent_noProperties() {
        String out = run("2\n4\n");
        assertTrue(out.contains("No properties yet. Create one first."));
        assertTrue(controller.getAllContracts().isEmpty());
    }

    @Test
    @DisplayName("Rent should create a contract and make the property unavailable")
    void rent_success() {
        String out = run("1\n101\nApartment\n5000\n"
                + "2\n1001\n101\n" + RENT_DATES + "4\n");

        assertTrue(out.contains("Rented contract #1001. Available: false"));
        RentalContract c = controller.getContract(1001);
        assertNotNull(c);
        assertSame(tenant, c.getTenant());
        assertTrue(c.isActive());
        assertFalse(controller.getProperty(101).isAvailable());
    }

    @Test
    @DisplayName("Malformed date should be asked again")
    void rent_malformedDate_asksAgain() {
        String out = run("1\n101\nApartment\n5000\n"
                + "2\n1001\n101\nnot-a-date\n" + RENT_DATES + "4\n");

        assertTrue(out.contains("Use format yyyy-mm-dd."));
        assertNotNull(controller.getContract(1001));
    }

    @Test
    @DisplayName("Unknown property ID should print an error and create no contract")
    void rent_unknownProperty() {
        String out = run("1\n101\nApartment\n5000\n"
                + "2\n1001\n999\n4\n");

        assertTrue(out.contains("Invalid property number."));
        assertTrue(controller.getAllContracts().isEmpty());
        assertTrue(controller.getProperty(101).isAvailable());
    }

    @Test
    @DisplayName("Renting an already rented property should show the Model error")
    void rent_alreadyRented_showsError() {
        String out = run("1\n101\nApartment\n5000\n"
                + "2\n1001\n101\n" + RENT_DATES
                + "2\n1002\n101\n" + RENT_DATES + "4\n");

        assertTrue(out.contains("Error:"));
        assertEquals(1, controller.getAllContracts().size());
        assertNull(controller.getContract(1002));
    }

    @Test
    @DisplayName("Reversed dates should show the Model error and keep the property available")
    void rent_reversedDates_showsError() {
        String out = run("1\n101\nApartment\n5000\n"
                + "2\n1001\n101\n2027-10-31\n2026-11-01\n4\n");

        assertTrue(out.contains("Error:"));
        assertTrue(controller.getAllContracts().isEmpty());
        assertTrue(controller.getProperty(101).isAvailable());
    }

    // ----- Terminate contract -----

    @Test
    @DisplayName("Terminate with no contracts should print a hint")
    void terminate_noContracts() {
        String out = run("3\n4\n");
        assertTrue(out.contains("No contracts yet."));
    }

    @Test
    @DisplayName("Terminate should deactivate the contract and free the property")
    void terminate_success() {
        String out = run("1\n101\nApartment\n5000\n"
                + "2\n1001\n101\n" + RENT_DATES
                + "3\n1001\n4\n");

        assertTrue(out.contains("Contract terminated."));
        assertTrue(out.contains("available: true"));
        assertFalse(controller.getContract(1001).isActive());
        assertTrue(controller.getProperty(101).isAvailable());
    }

    @Test
    @DisplayName("Terminate with an unknown contract ID should print an error and change nothing")
    void terminate_unknownContract() {
        String out = run("1\n101\nApartment\n5000\n"
                + "2\n1001\n101\n" + RENT_DATES
                + "3\n999\n4\n");

        assertTrue(out.contains("Invalid contract number."));
        assertTrue(controller.getContract(1001).isActive());
        assertFalse(controller.getProperty(101).isAvailable());
    }

    // ----- Full flow -----

    @Test
    @DisplayName("Full flow: create -> rent -> terminate in one session")
    void fullFlow() {
        String out = run("1\n101\nVilla\n8000\n"
                + "2\n2001\n101\n" + RENT_DATES
                + "3\n2001\n4\n");

        assertTrue(out.contains("Created property 101. Available: true"));
        assertTrue(out.contains("Rented contract #2001. Available: false"));
        assertTrue(out.contains("Contract terminated."));
        assertTrue(out.contains("Goodbye."));
        assertTrue(controller.getProperty(101).isAvailable());
        assertFalse(controller.getContract(2001).isActive());
    }
}
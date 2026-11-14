package view;

import controller.RentalController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ConsoleView Unit Tests")
class ConsoleViewTest {

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
    @DisplayName("Menu displays all four required options and exits on 4")
    void displayMenu_showsOptionsAndExitsCleanly() {
        provideInput("4\n");

        ConsoleView view = new ConsoleView(controller);
        assertDoesNotThrow(view::start);

        String output = outContent.toString();
        assertTrue(output.contains("1"));
        assertTrue(output.contains("2"));
        assertTrue(output.contains("3"));
        assertTrue(output.contains("4"));
    }

    @Test
    @DisplayName("View handles out-of-range menu options without crashing")
    void menuSelection_invalidOption_continuesWithoutCrash() {
        provideInput("0\n9\n4\n");

        ConsoleView view = new ConsoleView(controller);
        assertDoesNotThrow(view::start);
    }

    @Test
    @DisplayName("View handles non-numeric input gracefully without crashing")
    void menuSelection_nonNumericInput_continuesWithoutCrash() {
        provideInput("abc\n4\n");

        ConsoleView view = new ConsoleView(controller);
        assertDoesNotThrow(view::start);
    }

    @Test
    @DisplayName("View displays error message when given invalid property rate")
    void createPropertyInput_zeroRate_displaysErrorMessage() {
        String input = String.join("\n",
                "1", "101", "Studio Room", "0",
                "4"
        ) + "\n";
        provideInput(input);

        ConsoleView view = new ConsoleView(controller);
        assertDoesNotThrow(view::start);

        String output = outContent.toString().toLowerCase();
        assertTrue(output.contains("error") || output.contains("invalid") || output.contains("must"));
    }
}
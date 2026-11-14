
import controller.RentalController;
import view.ConsoleView;

public class Main {

    public static void main(String[] args) {
        // Initialize Controller
        RentalController controller = new RentalController();

        // Initialize View and connect it to Controller
        ConsoleView view = new ConsoleView(controller);

        // Start the application menu
        view.start();
    }
}

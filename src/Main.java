import javafx.application.Application;
import javafx.stage.Stage;

/**
 * represents the applications seed / initialisation point, the whole program starts here,
 * In the javaFX start(Stage primaryStage){} method
 */
public class Main extends Application {

    /**
     * exists for when the start() method is experiencing problems,
     * the application can be manipulated from the command line through this method
     *
     * @param args arguments passed in from a terminal
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * the actual functional main method of the application,
     * everything starts here in a properly functioning JavaFx application
     *
     * @param primaryStage the base JavaFX container for the application
     * @throws Exception for anything that is not caught and handled within the application
     */
    @Override public void start(Stage primaryStage) throws Exception {
        new GUI(primaryStage);
    }
}

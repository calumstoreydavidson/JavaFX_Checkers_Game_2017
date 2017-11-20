import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.stream.IntStream;

public class JavaFXExample extends Application {

    private static final int N_BUTTONS = 5;

    @Override
    public void start(Stage stage) {
        VBox buttonLayout = new VBox(
                10,
                IntStream.range(0, N_BUTTONS)
                         .mapToObj(this::createButton)
                         .toArray(Button[]::new)
        );
        HBox.setHgrow(buttonLayout, Priority.ALWAYS);

        TextArea textArea = new TextArea("Test");
        textArea.setPrefWidth(100);
        textArea.setMaxWidth(TextArea.USE_PREF_SIZE);
        textArea.setMinWidth(TextArea.USE_PREF_SIZE);

        HBox layout = new HBox(10, textArea, buttonLayout);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout);

        stage.setScene(scene);
        stage.show();
    }

    private Button createButton(int i) {
        Button button = new Button("Button " + i);
//        button.setMaxWidth(Double.MAX_VALUE);
        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(button, Priority.ALWAYS);

        return button;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
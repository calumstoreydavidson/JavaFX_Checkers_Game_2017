import java.awt.*;
import java.net.URI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    private Stage primaryStage;
    public static TextArea output;
    VBox controls = buildControls();

    private CheckersGame game;

    public static void main(String[] args) {
        launch(args);
    }

    @Override public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        //game window title
        this.primaryStage.setTitle("Checkers Game");

        //prevent all window resizing
        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.UNIFIED);

        game = new CheckersGame();

        Scene GUI = new Scene(createGUI());
        this.primaryStage.setScene(GUI);
        this.primaryStage.show();

        game.startNewGame(true); //startNewGame(getUserInput()); TODO because effort
    }

    private Parent createGUI() {
        //game controls
//        VBox controls = buildControls();

        //game board
        Pane gameBoard = new Pane();
        int squareEdgeLength = CheckersGame.SCALE * CheckersGame.TILE_SIZE;
        gameBoard.setMinSize(squareEdgeLength, squareEdgeLength);
        gameBoard.getChildren().setAll(game.getComponents());

        HBox layout = new HBox(10, controls, gameBoard);
        layout.setPadding(new Insets(10));

        return layout;
    }

    private VBox buildControls() {
        //begin new game button
        Button newGameButton = new Button("Start New Game");
        newGameButton.setOnAction(value -> {
            game.startNewGame(true);
        });

        //crown stealing toggle
        Button crownStealingToggleButton = new Button("Disable King On King Kill");
        crownStealingToggleButton.setOnAction(value -> {
            game.CROWN_STEALING_ALLOWED = !game.CROWN_STEALING_ALLOWED;
            crownStealingToggleButton.setText(game.CROWN_STEALING_ALLOWED ? "Disable King On King Kill" : "Enable King On King Kill");
            output.setText(game.CROWN_STEALING_ALLOWED ? "King On King Kill Enabled" : "King On King Kill Disabled");
        });

        //game Instructions
        Button displayInstructionsButton = new Button("Open Game Rules In Browser");

        displayInstructionsButton.setOnAction(e -> {
            try {
                Desktop.getDesktop().browse(new URI("http://www.indepthinfo.com/checkers/play.shtml"));
                output.setText("Instructions Displayed - see browser");
            } catch (Exception e1) {
                output.setText("Apologies, your browser can't be accessed at this time");
            }
        });

        //play tile toggle
        Button togglePlayTileButton = new Button("Play on Black");
        togglePlayTileButton.setOnAction(value -> {
            game.PLAY_SQUARE = game.PLAY_SQUARE == 0 ? 1 : 0;
            togglePlayTileButton.setText(game.PLAY_SQUARE == 0 ? "Play on White" : "Play on Black"); //TODO what the fuck is up with this fucking button
            output.setText(String.valueOf(game.PLAY_SQUARE == 0 ? "You are now playing on the black squares" : "You are now playing on the white squares"));
            //TODO rework this so that the UI is not being reset as well
            game.startNewGame(true);
        });

        //god mode toggle
        Button developmentModeButton = new Button("Enable Development Mode");
        developmentModeButton.setOnAction(value -> {
            game.toggleDevelopmentMode();
            developmentModeButton.setText(game.DEVELOPMENT_MODE_ENABLED ? "Disable Development Mode" : "Enable Development Mode");
            output.setText(game.DEVELOPMENT_MODE_ENABLED ? "God Mode Enabled" : "God Mode Disabled");
        });

        //game output
        output = new TextArea("Welcome!!! to Calum Storey Davidson's University Of Sussex - Knowledge And Reasoning - checkers game coursework.\n\nInstructions:\n" +
                              "- Drag and drop units with your mouse to make your moves\n- Green squares are units that can move.\n- Blue squares are where they can go.\n- And red squares are mandatory attacks.");
        output.setPrefWidth(300);
        output.setMaxWidth(TextArea.USE_PREF_SIZE);
        output.setMinWidth(TextArea.USE_PREF_SIZE);
        output.setEditable(false);
        output.setPrefRowCount(10);
        output.setPrefColumnCount(20);
        output.setWrapText(true);

        return new VBox(10, newGameButton, crownStealingToggleButton, developmentModeButton, displayInstructionsButton, togglePlayTileButton, output);
    }

}

import java.awt.*;
import java.net.URI;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
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

    private Game game;

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

        game = new Game();

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
        int squareEdgeLength = Game.SCALE * Game.TILE_SIZE;
        gameBoard.setMinSize(squareEdgeLength, squareEdgeLength);
        gameBoard.getChildren().setAll(game.getComponents());

        HBox layout = new HBox(10, controls, gameBoard, output);
        layout.setPadding(new Insets(10));

        return layout;
    }

    private VBox buildControls() {
        //begin new game button
        Button newGameButton = getNewGameButton();

        //crown stealing toggle
        Button crownStealingToggleButton = getCrownStealingToggleButton();

        //play tile toggle
        Button togglePlayTileButton = getTogglePlayTileButton();

        //TODO play as other team toggle
//        Button togglePlayerTeamButton = getTogglePlayerTeamButton();

        //god mode toggle
        Button developmentModeButton = getDevelopmentModeButton();

        //game Instructions
        Button displayInstructionsButton = getDisplayInstructionsButton();

        //game output
        setUpGameOutput();

        //debug run AIMove
        Button runAIMoveButton = getRunAIMoveButton();

        //RandomAIPlayer move speed
        Label AITurnLengthLabel = new Label("RandomAIPlayer turn length control");
        Slider AITurnLengthSlider = getMoveSpeedSlider();

        //TODO toggle moveable piece highlighting

        //TODO toggle moveable piece target highlighting

        //TODO toggle player team

        //TODO toggle algorithm type

        //TODO toggle number of RandomAIPlayer players

        //TODO swap teams sides of the board

        //TODO show board axis
            //TODO make 0,0 in bottom left rather thant top left

        VBox controls = new VBox(10,
                newGameButton,
                crownStealingToggleButton,
                togglePlayTileButton,
                developmentModeButton,
                displayInstructionsButton,
                AITurnLengthLabel,
                AITurnLengthSlider
        );

        controls.setPrefWidth(300);

        return controls;
    }

    private Slider getMoveSpeedSlider() {
        Slider AIMoveSpeedSlider = new Slider(0, 1, 1);

        //slider value range and default
        AIMoveSpeedSlider.setMin(50);
        AIMoveSpeedSlider.setMax(1000);
        AIMoveSpeedSlider.setValue(100);

        //major numbered slider intervals
        AIMoveSpeedSlider.setMajorTickUnit(100);
        //minor intervals between major intervals
        AIMoveSpeedSlider.setMinorTickCount(1);

        //slider config
        AIMoveSpeedSlider.setShowTickLabels(true);
        AIMoveSpeedSlider.setShowTickMarks(true);
        AIMoveSpeedSlider.setSnapToTicks(true);

        AIMoveSpeedSlider.valueProperty().addListener((ov, old_val, new_val) -> game.AI_MOVE_LAG_TIME = (int) AIMoveSpeedSlider.getValue());
        return AIMoveSpeedSlider;
    }

    private Button getRunAIMoveButton() {
        Button runAIMoveButton = new Button("Run RandomAIPlayer Move");
        runAIMoveButton.setOnAction(value -> game.runAIMove());
        return runAIMoveButton;
    }

    private void setUpGameOutput() {
        output = new TextArea("Welcome!!! to Calum Storey Davidson's University Of Sussex - Knowledge And Reasoning " +
                              "- checkers game coursework.\n\nInstructions:\n" +
                              "- Drag and drop units with your mouse to make your moves\n" +
                              "- Green squares are units that can move.\n" +
                              "- Blue squares are where they can go.\n" +
                              "- And red squares are mandatory attacks.");
        output.setPrefWidth(300);
        output.setMaxWidth(TextArea.USE_PREF_SIZE);
        output.setMinWidth(TextArea.USE_PREF_SIZE);
        output.setEditable(false);
        output.setPrefRowCount(10);
        output.setPrefColumnCount(20);
        output.setWrapText(true);
    }

    private Button getDevelopmentModeButton() {
        Button developmentModeButton = new Button("Enable Development Mode");
        developmentModeButton.setOnAction(value -> {
            game.toggleDevelopmentMode();
            developmentModeButton.setText(game.DEVELOPMENT_MODE_ENABLED ? "Disable Development Mode" : "Enable Development Mode");
            output.setText(game.DEVELOPMENT_MODE_ENABLED ? "God Mode Enabled" : "God Mode Disabled");
        });
        return developmentModeButton;
    }

    private Button getTogglePlayTileButton() {
        Button togglePlayTileButton = new Button("Play on Black");
        togglePlayTileButton.setOnAction(value -> {
            game.PLAY_SQUARE = game.PLAY_SQUARE == 0 ? 1 : 0;
            togglePlayTileButton.setText(game.PLAY_SQUARE == 0 ? "Play on White" : "Play on Black"); //TODO what the fuck is up with this fucking button
            output.setText(String.valueOf(game.PLAY_SQUARE == 0 ? "You are now playing on the black squares" : "You are now playing on the white squares"));
            //TODO rework this so that the UI is not being reset as well
            game.startNewGame(true);
        });
        return togglePlayTileButton;
    }

    private Button getDisplayInstructionsButton() {
        Button displayInstructionsButton = new Button("Open Game Rules In Browser");
        displayInstructionsButton.setOnAction(e -> {
            try {
                Desktop.getDesktop().browse(new URI("http://www.indepthinfo.com/checkers/play.shtml"));
                output.setText("Instructions Displayed - see browser");
            } catch (Exception e1) {
                output.setText("Apologies, your browser can't be accessed at this time");
            }
        });
        return displayInstructionsButton;
    }

    private Button getCrownStealingToggleButton() {
        Button crownStealingToggleButton = new Button("Disable King On King Kill");
        crownStealingToggleButton.setOnAction(value -> {
            game.CROWN_STEALING_ALLOWED = !game.CROWN_STEALING_ALLOWED;
            crownStealingToggleButton.setText(game.CROWN_STEALING_ALLOWED ? "Disable King On King Kill" : "Enable King On King Kill");
            output.setText(game.CROWN_STEALING_ALLOWED ? "King On King Kill Enabled" : "King On King Kill Disabled");
        });
        return crownStealingToggleButton;
    }

    private Button getNewGameButton() {
        Button newGameButton = new Button("Start New Game");
        newGameButton.setOnAction(value -> game.startNewGame(true));
        return newGameButton;
    }

}

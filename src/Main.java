import java.awt.*;
import java.net.URI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    public static TextArea output;
    VBox controls = buildControls();

    private Game game;

    public static void main(String[] args) {
        launch(args);
    }

    @Override public void start(Stage primaryStage) throws Exception {
        //game window title
        primaryStage.setTitle("GUIExample Game");

        //prevent all window resizing
        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.UNIFIED);

        game = new Game();

        Scene GUI = new Scene(createGUI());
        primaryStage.setScene(GUI);
        primaryStage.show();

        game.startNewGame(); //startNewGame(getUserInput()); TODO because effort
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

        //TODO swap teams sides of the board

        //TODO show board axis
            //TODO make 0,0 in bottom left rather thant top left

        //TODO toggle number of AI players


        GridPane teamPlayerMenus = getTeamPlayerMenus();

        VBox controls = new VBox(10,
                newGameButton,
                crownStealingToggleButton,
                togglePlayTileButton,
                developmentModeButton,
                displayInstructionsButton,
                AITurnLengthLabel,
                AITurnLengthSlider,
                teamPlayerMenus
        );

        controls.setPrefWidth(300);

        return controls;
    }

    private GridPane getTeamPlayerMenus() {
        MenuItem redTeamPlayerMenuItem1 = new MenuItem("Human");
        redTeamPlayerMenuItem1.setOnAction(event -> {
            game.setRedPlayer(new HumanPlayer(Team.RED));
            game.startNewGame(); // TODO fix changing mid game?
        });

        MenuItem redTeamPlayerMenuItem2 = new MenuItem("Random");
        redTeamPlayerMenuItem2.setOnAction(event -> {
            game.setRedPlayer(new RandomAIPlayer(Team.RED));
            game.startNewGame();//TODO it is bad that it can immediatly start,  perhaps a submit button - or merge with start new game button
        });

//        MenuItem redTeamPlayerMenuItem3 = new MenuItem("Minimax");//TODO add minimax
//        redTeamPlayerMenuItem3.setOnAction(event -> game.setRedPlayer(new HumanPlayer(Team.RED)));

        MenuButton redTeamPlayerMenuButton = new MenuButton("Red Player", null, redTeamPlayerMenuItem1, redTeamPlayerMenuItem2);


        MenuItem whiteTeamPlayerMenuItem1 = new MenuItem("Human");
        whiteTeamPlayerMenuItem1.setOnAction(event -> {
            game.setWhitePlayer(new HumanPlayer(Team.WHITE));
            game.startNewGame();
        });

        MenuItem whiteTeamPlayerMenuItem2 = new MenuItem("Random");
        whiteTeamPlayerMenuItem2.setOnAction(event -> {
            game.setWhitePlayer(new RandomAIPlayer(Team.WHITE));
            game.startNewGame();
        });

//        MenuItem whiteTeamPlayerMenuItem3 = new MenuItem("Minimax");//TODO add minimax
//        whiteTeamPlayerMenuItem3.setOnAction(event -> game.setWhitePlayer(new HumanPlayer(Team.RED)));

        MenuButton whiteTeamPlayerMenuButton = new MenuButton("White Player", null, whiteTeamPlayerMenuItem1, whiteTeamPlayerMenuItem2);

        GridPane gridPane = new GridPane();
        gridPane.add(redTeamPlayerMenuButton,0,0);
        gridPane.add(whiteTeamPlayerMenuButton,1,0);
        return gridPane;
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
        runAIMoveButton.setOnAction(value -> game.nextPlayersTurn());
        return runAIMoveButton;
    }

    private void setUpGameOutput() {
        output = new TextArea("Welcome!!! to Calum Storey Davidson's University Of Sussex - Knowledge And Reasoning " +
                              "- checkers game coursework.\n\nInstructions:\n" +
                              "- Drag and drop units with your mouse to make your moves\n" +
                              "- Green squares are units that can move.\n" +
                              "- Blue squares are where they can go.\n" +
                              "- And red squares are mandatory attacks.");
        output.setPrefWidth(350);
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
            output.appendText(game.DEVELOPMENT_MODE_ENABLED ? "God Mode Enabled" : "God Mode Disabled");
        });
        return developmentModeButton;
    }

    private Button getTogglePlayTileButton() {
        Button togglePlayTileButton = new Button("Play on Black");
        togglePlayTileButton.setOnAction(value -> {
            Game.PLAY_SQUARE = Game.PLAY_SQUARE == 0 ? 1 : 0;
            togglePlayTileButton.setText(Game.PLAY_SQUARE == 0 ? "Play on White" : "Play on Black"); //TODO what the fuck is up with this fucking button
            output.appendText(String.valueOf(Game.PLAY_SQUARE == 0 ? "You are now playing on the black squares" : "You are now playing on the white squares"));
            //TODO rework this so that the UI is not being reset as well
            game.startNewGame();
        });
        return togglePlayTileButton;
    }

    private Button getDisplayInstructionsButton() {
        Button displayInstructionsButton = new Button("Open Game Rules In Browser");
        displayInstructionsButton.setOnAction(e -> {
            try {
                Desktop.getDesktop().browse(new URI("http://www.indepthinfo.com/checkers/play.shtml"));
                output.appendText("Instructions Displayed - see browser");
            } catch (Exception e1) {
                output.appendText("Apologies, your browser can't be accessed at this time");
            }
        });
        return displayInstructionsButton;
    }

    private Button getCrownStealingToggleButton() {
        Button crownStealingToggleButton = new Button("Disable King On King Kill");
        crownStealingToggleButton.setOnAction(value -> {
            Game.CROWN_STEALING_ALLOWED = !Game.CROWN_STEALING_ALLOWED;
            crownStealingToggleButton.setText(Game.CROWN_STEALING_ALLOWED ? "Disable King On King Kill" : "Enable King On King Kill");
            output.appendText(Game.CROWN_STEALING_ALLOWED ? "King On King Kill Enabled" : "King On King Kill Disabled");
        });
        return crownStealingToggleButton;
    }

    private Button getNewGameButton() {
        Button newGameButton = new Button("Start New Game");
        newGameButton.setOnAction(value -> game.startNewGame());
        return newGameButton;
    }

}

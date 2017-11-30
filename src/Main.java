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
    private Game game;
    VBox controls = buildControls();
    private Stage primaryStage;
    private int maxAIDifficulty = 14;//allow altering difficulty slider based on AI - pruning can be harder while not slowing the game down

    public static void main(String[] args) {
        launch(args);
    }

    @Override public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        //game window title
        this.primaryStage.setTitle("GUIExample Game");

        //prevent all window resizing
        this.primaryStage.setResizable(false);
        this.primaryStage.initStyle(StageStyle.UNIFIED);

        refreshGUI(new ABNegamaxAI(Team.RED), new ABNegamaxAI(Team.WHITE));

//        game.startNewGame(); //startNewGame(getUserInput()); TODO because effort
    }

    private void refreshGUI(Player redPlayer, Player whitePlayer) {
        game = new Game(redPlayer, whitePlayer);
        Scene GUI = new Scene(createGUI());
        primaryStage.setScene(GUI);
        primaryStage.show();
    }

    private Parent createGUI() {
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

        Button verbosOutputButton = getVerbosOutputButton();

        //game Instructions
        Button displayInstructionsButton = getDisplayInstructionsButton();

        //game output
        setUpGameOutput();

        //debug run AIMove
        Button runAIMoveButton = getRunAIMoveButton();

        //RandomAIPlayer move speed
        Label AITurnLengthLabel = new Label("AI Player turn length control");
        Slider AITurnLengthSlider = getMoveSpeedSlider();

        Label AIDifficultyLabel = new Label("AI Player difficulty control");
        Slider AIDifficultySlider = getAIDifficultySlider();

        //TODO toggle moveable piece highlighting

        //TODO toggle moveable piece target highlighting

        //TODO toggle player team

        //TODO toggle algorithm type

        //TODO swap teams sides of the board

        //TODO show board axis
        //TODO make 0,0 in bottom left rather thant top left

        //TODO toggle number of AI players

        //TODO AI difficulty slider

        GridPane teamPlayerMenus = getTeamPlayerMenus();

        VBox controls = new VBox(10, newGameButton, crownStealingToggleButton, togglePlayTileButton, developmentModeButton, verbosOutputButton, displayInstructionsButton, AITurnLengthLabel, AITurnLengthSlider, teamPlayerMenus, AIDifficultyLabel, AIDifficultySlider);

        controls.setPrefWidth(300);

        return controls;
    }

    private Button getVerbosOutputButton() {
        Button verbosOutputButton = new Button("Disable Verbose Output\n");
        verbosOutputButton.setOnAction(value -> {
            Game.VERBOSE_OUTPUT = !Game.VERBOSE_OUTPUT;
            verbosOutputButton.setText(Game.VERBOSE_OUTPUT ? "Disable Verbose Output\n" : "Enable Verbose Output\n");
            output.appendText(Game.VERBOSE_OUTPUT ? "Verbose Output Enabled\n" : "Verbose Output Disabled\n");
        });
        return verbosOutputButton;
    }

    private GridPane getTeamPlayerMenus() {
        MenuItem redTeamPlayerMenuItem1 = new MenuItem("Human");
        redTeamPlayerMenuItem1.setOnAction(event -> {
            game.setRedPlayer(new HumanPlayer(Team.RED));
            // TODO fix changing mid game?
            game.triggerReset();
//            game.scheduleNewGame();
//            refreshGUI(new HumanPlayer(Team.RED), game.getWhitePlayer());
        });

        MenuItem redTeamPlayerMenuItem2 = new MenuItem("Random");
        redTeamPlayerMenuItem2.setOnAction(event -> {
            game.setRedPlayer(new RandomAIPlayer(Team.RED));
            if (game.getRedPlayer().isPlayerHuman()) {
//                game.scheduleNewGame();
                game.startNewGame();
            } else {
                game.triggerReset();
            }
//TODO it is bad that it can immediatly start,  perhaps a submit button - or merge with start new game button
//            refreshGUI(new RandomAIPlayer(Team.RED), game.getWhitePlayer());
        });

        MenuItem redTeamPlayerMenuItem3 = new MenuItem("Minimax");
        redTeamPlayerMenuItem3.setOnAction(event -> {
            game.setRedPlayer(new MinimaxAI(Team.RED));
            if (game.getRedPlayer().isPlayerHuman()) {
                game.startNewGame();
            } else {
                game.triggerReset();
            }
        });

        MenuItem redTeamPlayerMenuItem4 = new MenuItem("Negamax");
        redTeamPlayerMenuItem4.setOnAction(event -> {
            maxAIDifficulty = 10;
            game.setWhitePlayer(new NegamaxAI(Team.RED));
            if (game.getRedPlayer().isPlayerHuman()) {
                game.scheduleNewGame();
            } else {
                game.triggerReset();
            }
        });

        MenuItem redTeamPlayerMenuItem5 = new MenuItem("ABNegamax");
        redTeamPlayerMenuItem5.setOnAction(event -> {
            maxAIDifficulty = 14;
            game.setWhitePlayer(new ABNegamaxAI(Team.RED));
            if (game.getRedPlayer().isPlayerHuman()) {
                game.scheduleNewGame();
            } else {
                game.triggerReset();
            }
        });

        MenuButton redTeamPlayerMenuButton = new MenuButton("Red Player", null, redTeamPlayerMenuItem1, redTeamPlayerMenuItem2, redTeamPlayerMenuItem3, redTeamPlayerMenuItem4, redTeamPlayerMenuItem5);


        MenuItem whiteTeamPlayerMenuItem1 = new MenuItem("Human");
        whiteTeamPlayerMenuItem1.setOnAction(event -> {
            game.setWhitePlayer(new HumanPlayer(Team.WHITE));
            game.triggerReset();
//            refreshGUI(game.getRedPlayer(), new HumanPlayer(Team.WHITE));
        });

        MenuItem whiteTeamPlayerMenuItem2 = new MenuItem("Random");
        whiteTeamPlayerMenuItem2.setOnAction(event -> {
            game.setWhitePlayer(new RandomAIPlayer(Team.WHITE));
            if (game.getWhitePlayer().isPlayerHuman()) {
                game.scheduleNewGame();
            } else {
                game.triggerReset();
            }
//            refreshGUI(game.getRedPlayer(), new RandomAIPlayer(Team.WHITE));
        });

        MenuItem whiteTeamPlayerMenuItem3 = new MenuItem("Minimax");
        whiteTeamPlayerMenuItem3.setOnAction(event -> {
            game.setWhitePlayer(new MinimaxAI(Team.WHITE));
            if (game.getWhitePlayer().isPlayerHuman()) {
                game.scheduleNewGame();
            } else {
                game.triggerReset();
            }
        });

        MenuItem whiteTeamPlayerMenuItem4 = new MenuItem("Negamax");
        whiteTeamPlayerMenuItem4.setOnAction(event -> {
            game.setWhitePlayer(new NegamaxAI(Team.WHITE));
            maxAIDifficulty = 10;
            if (game.getWhitePlayer().isPlayerHuman()) {
                game.scheduleNewGame();
            } else {
                game.triggerReset();
            }
        });

        MenuItem whiteTeamPlayerMenuItem5 = new MenuItem("ABNegamax");
        whiteTeamPlayerMenuItem5.setOnAction(event -> {
            game.setWhitePlayer(new ABNegamaxAI(Team.WHITE));
            maxAIDifficulty = 14;
            if (game.getWhitePlayer().isPlayerHuman()) {
                game.scheduleNewGame();
            } else {
                game.triggerReset();
            }
        });

        MenuButton whiteTeamPlayerMenuButton = new MenuButton("White Player", null, whiteTeamPlayerMenuItem1, whiteTeamPlayerMenuItem2, whiteTeamPlayerMenuItem3, whiteTeamPlayerMenuItem4, whiteTeamPlayerMenuItem5);

        GridPane gridPane = new GridPane();
        gridPane.add(redTeamPlayerMenuButton, 0, 0);
        gridPane.add(whiteTeamPlayerMenuButton, 1, 0);
        return gridPane;
    }

    private Slider getMoveSpeedSlider() {
        Slider AIMoveSpeedSlider = new Slider(0, 1, 1);

        //slider value range and default
        AIMoveSpeedSlider.setMin(100);
        AIMoveSpeedSlider.setMax(1000);
        AIMoveSpeedSlider.setValue(Game.AI_MOVE_LAG_TIME);

        //major numbered slider intervals
        AIMoveSpeedSlider.setMajorTickUnit(100);
        //minor intervals between major intervals
        AIMoveSpeedSlider.setMinorTickCount(1);

        //slider config
        AIMoveSpeedSlider.setShowTickLabels(true);
        AIMoveSpeedSlider.setShowTickMarks(true);
        AIMoveSpeedSlider.setSnapToTicks(true);

        AIMoveSpeedSlider.valueProperty().addListener((ov, old_val, new_val) -> {
            Game.AI_MOVE_LAG_TIME = (int) AIMoveSpeedSlider.getValue();
        });
        return AIMoveSpeedSlider;
    }

    private Slider getAIDifficultySlider() {
        Slider AIDifficultySlider = new Slider(0, 1, 1);

        //slider value range and default
        AIDifficultySlider.setMin(1);
        AIDifficultySlider.setMax(maxAIDifficulty);
        AIDifficultySlider.setValue(Game.AI_MAX_SEARCH_DEPTH);

        //major numbered slider intervals
        AIDifficultySlider.setMajorTickUnit(1);
        //minor intervals between major intervals
        AIDifficultySlider.setMinorTickCount(0);

        //slider config
        AIDifficultySlider.setShowTickLabels(true);
        AIDifficultySlider.setShowTickMarks(true);
        AIDifficultySlider.setSnapToTicks(true);

        AIDifficultySlider.valueProperty().addListener((ov, old_val, new_val) -> {
            Game.AI_MAX_SEARCH_DEPTH = (int) AIDifficultySlider.getValue();
        });
        return AIDifficultySlider;
    }

    private Button getRunAIMoveButton() {
        Button runAIMoveButton = new Button("Run RandomAIPlayer Move");
        runAIMoveButton.setOnAction(value -> game.nextPlayersTurn());
        return runAIMoveButton;
    }

    private void setUpGameOutput() {
        output = new TextArea("Welcome!!! to Calum Storey Davidson's University Of Sussex - Knowledge And Reasoning " + "- checkers game coursework.\n\nInstructions:\n" + "- Drag and drop units with your mouse to make your moves\n" + "- Green squares are units that can move.\n" + "- Blue squares are where they can go.\n" + "- And red squares are mandatory attacks.");
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
            output.appendText(game.DEVELOPMENT_MODE_ENABLED ? "God Mode Enabled\n" : "God Mode Disabled\n");
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
            game.triggerReset();
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
        newGameButton.setOnAction(value -> {
//            refreshGUI(game.getRedPlayer(), game.getWhitePlayer());
            game.triggerReset();
        });
        return newGameButton;
    }

}

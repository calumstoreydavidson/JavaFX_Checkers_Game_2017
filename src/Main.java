import java.awt.*;
import java.net.URI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
    private Stage primaryStage;
    public static int maxWhiteAIDifficulty;//allow altering difficulty slider based on AI - pruning can be harder while not slowing the game down
    public static int maxRedAIDifficulty;//allow altering difficulty slider based on AI - pruning can be harder while not slowing the game down
    VBox controls = buildControls();

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

        Player initialRedPlayer = new RandomAIPlayer(Team.RED);
        Player initialWhitePlayer = new RandomAIPlayer(Team.WHITE);
        refreshGUI(initialRedPlayer, initialWhitePlayer);

//        game.startNewGame(); //startNewGame(getUserInput()); TODO because effort
    }

    private void refreshGUI(Player redPlayer, Player whitePlayer) {
        refreshGame(redPlayer, whitePlayer);
        Scene GUI = new Scene(createGUI());
        primaryStage.setScene(GUI);
        primaryStage.show();
    }

    private void refreshGame(Player redPlayer, Player whitePlayer) {
        game = new Game(redPlayer, whitePlayer);
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

        VBox controls = new VBox(10, newGameButton, crownStealingToggleButton, togglePlayTileButton, developmentModeButton, verbosOutputButton, displayInstructionsButton, teamPlayerMenus, AITurnLengthLabel, AITurnLengthSlider, AIDifficultyLabel, AIDifficultySlider);

        controls.setPrefWidth(300);

        return controls;
    }

    private GridPane getTeamPlayerMenus() {
        ComboBox redPlayer = new ComboBox();
        redPlayer.getItems().setAll("Human", "Random AI", "Negamax AI", "AB Negamax AI");
        redPlayer.getSelectionModel().select("Random AI");
        redPlayer.setOnAction((event -> {
            switch (redPlayer.getSelectionModel().getSelectedIndex()) {
                case 0:
                    game.setRedPlayer(new HumanPlayer(Team.RED));
                    game.triggerReset();
                    break;
                case 1:
                    game.setRedPlayer(new RandomAIPlayer(Team.RED));
                    if (game.isHumanPlaying()) {
                        game.startNewGame();
                    } else {
                        game.triggerReset();
                    }
                    break;
                case 2:
                    game.setRedPlayer(new NegamaxAI(Team.RED));
                    if (game.isHumanPlaying()) {
                        game.scheduleNewGame();
                    } else {
                        game.triggerReset();
                    }
                    break;
                case 3:
                    game.setRedPlayer(new ABNegamaxAI(Team.RED));
                    if (game.isHumanPlaying()) {
                        game.scheduleNewGame();
                    } else {
                        game.triggerReset();
                    }
                    break;
            }
        }));

        ComboBox whitePlayer = new ComboBox();
        whitePlayer.getItems().setAll("Human", "Random AI", "Negamax AI", "AB Negamax AI");
        whitePlayer.getSelectionModel().select("Random AI");
        whitePlayer.setOnAction((event -> {
            switch (whitePlayer.getSelectionModel().getSelectedIndex()) {
                case 0:
                    game.setWhitePlayer(new HumanPlayer(Team.WHITE));
                    game.triggerReset();
                    break;
                case 1:
                    game.setWhitePlayer(new RandomAIPlayer(Team.WHITE));
                    if (game.isHumanPlaying()) {
                        game.startNewGame();
                    } else {
                        game.triggerReset();
                    }
                    break;
                case 2:
                    game.setWhitePlayer(new NegamaxAI(Team.WHITE));
                    if (game.isHumanPlaying()) {
                        game.scheduleNewGame();
                    } else {
                        game.triggerReset();
                    }
                    break;
                case 3:
                    game.setWhitePlayer(new ABNegamaxAI(Team.WHITE));
                    if (game.isHumanPlaying()) {
                        game.scheduleNewGame();
                    } else {
                        game.triggerReset();
                    }
                    break;
            }
        }));

        GridPane playerMenus = new GridPane();
        playerMenus.add(new Label("Red Player:"), 0, 0);
        playerMenus.add(redPlayer, 1, 0);

        playerMenus.add(new Label("White Player:"), 0, 1);
        playerMenus.add(whitePlayer, 1, 1);

        playerMenus.setHgap(10);
        return playerMenus;
    }

    private Button getVerbosOutputButton() {
        Button verboseOutputButton = new Button("Disable Verbose Output\n");
        verboseOutputButton.setOnAction(value -> {
            Game.VERBOSE_OUTPUT = !Game.VERBOSE_OUTPUT;
            verboseOutputButton.setText(Game.VERBOSE_OUTPUT ? "Disable Verbose Output\n" : "Enable Verbose Output\n");
            output.appendText(Game.VERBOSE_OUTPUT ? "Verbose Output Enabled\n" : "Verbose Output Disabled\n");
        });
        return verboseOutputButton;
    }

//    private GridPane getTeamPlayerMenus() {
//        MenuItem redTeamHumanPlayer = new MenuItem("Human");
//        redTeamHumanPlayer.setOnAction(event -> {
//            game.setRedPlayer(new HumanPlayer(Team.RED));
//            game.triggerReset();
//        });
//
//        MenuItem redTeamRandomPlayer = new MenuItem("Random");
//        redTeamRandomPlayer.setOnAction(event -> {
//            game.setRedPlayer(new RandomAIPlayer(Team.RED));
//            if (game.isHumanPlaying().isPlayerHuman()) {
//                game.startNewGame();
//            } else {
//                game.triggerReset();
//            }
////TODO it is bad that it can immediatly start,  perhaps a submit button - or merge with start new game button
////            refreshGUI(new RandomAIPlayer(Team.RED), game.getWhitePlayer());
//        });
//
//        MenuItem redTeamMinimaxPlayer = new MenuItem("Minimax");
//        redTeamMinimaxPlayer.setOnAction(event -> {
//            game.setRedPlayer(new MinimaxAI(Team.RED));
//            if (game.isHumanPlaying().isPlayerHuman()) {
//                game.startNewGame();
//            } else {
//                game.triggerReset();
//            }
//        });
//
//        MenuItem redTeamNegamaxPlayer = new MenuItem("Negamax");
//        redTeamNegamaxPlayer.setOnAction(event -> {
//            maxAIDifficulty = 10;
//            game.setWhitePlayer(new NegamaxAI(Team.RED));
//            if (game.isHumanPlaying().isPlayerHuman()) {
//                game.scheduleNewGame();
//            } else {
//                game.triggerReset();
//            }
//        });
//
//        MenuItem redTeamABNegamaxPlayer = new MenuItem("ABNegamax");
//        redTeamABNegamaxPlayer.setOnAction(event -> {
//            maxAIDifficulty = 14;
//            game.setWhitePlayer(new ABNegamaxAI(Team.RED));
//            if (game.isHumanPlaying().isPlayerHuman()) {
//                game.scheduleNewGame();
//            } else {
//                game.triggerReset();
//            }
//        });
//
//        MenuButton redTeamPlayerMenuButton = new MenuButton("Red Player", null, redTeamHumanPlayer, redTeamRandomPlayer, redTeamMinimaxPlayer, redTeamNegamaxPlayer, redTeamABNegamaxPlayer);
//
//
//        MenuItem whiteTeamHumanPlayer = new MenuItem("Human");
//        whiteTeamHumanPlayer.setOnAction(event -> {
//            game.setWhitePlayer(new HumanPlayer(Team.WHITE));
//            game.triggerReset();
////            refreshGUI(game.isHumanPlaying(), new HumanPlayer(Team.WHITE));
//        });
//
//        MenuItem whiteTeamRandomPlayer = new MenuItem("Random");
//        whiteTeamRandomPlayer.setOnAction(event -> {
//            game.setWhitePlayer(new RandomAIPlayer(Team.WHITE));
//            if (game.getWhitePlayer().isPlayerHuman()) {
//                game.scheduleNewGame();
//            } else {
//                game.triggerReset();
//            }
////            refreshGUI(game.isHumanPlaying(), new RandomAIPlayer(Team.WHITE));
//        });
//
//        MenuItem whiteTeamMinimaxPlayer = new MenuItem("Minimax");
//        whiteTeamMinimaxPlayer.setOnAction(event -> {
//            game.setWhitePlayer(new MinimaxAI(Team.WHITE));
//            if (game.getWhitePlayer().isPlayerHuman()) {
//                game.scheduleNewGame();
//            } else {
//                game.triggerReset();
//            }
//        });
//
//        MenuItem whiteTeamNegamaxPlayer = new MenuItem("Negamax");
//        whiteTeamNegamaxPlayer.setOnAction(event -> {
//            game.setWhitePlayer(new NegamaxAI(Team.WHITE));
//            maxAIDifficulty = 10;
//            if (game.getWhitePlayer().isPlayerHuman()) {
//                game.scheduleNewGame();
//            } else {
//                game.triggerReset();
//            }
//        });
//
//        MenuItem whiteTeamABNegamaxPlayer = new MenuItem("ABNegamax");
//        whiteTeamABNegamaxPlayer.setOnAction(event -> {
//            game.setWhitePlayer(new ABNegamaxAI(Team.WHITE));
//            maxAIDifficulty = 14;
//            if (game.getWhitePlayer().isPlayerHuman()) {
//                game.scheduleNewGame();
//            } else {
//                game.triggerReset();
//            }
//        });
//
//        MenuButton whiteTeamPlayerMenuButton = new MenuButton("White Player", null, whiteTeamHumanPlayer, whiteTeamRandomPlayer, whiteTeamMinimaxPlayer, whiteTeamNegamaxPlayer, whiteTeamABNegamaxPlayer);
//
//        GridPane gridPane = new GridPane();
//        gridPane.add(redTeamPlayerMenuButton, 0, 0);
//        gridPane.add(whiteTeamPlayerMenuButton, 1, 0);
//        return gridPane;
//    }

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

        AIMoveSpeedSlider.valueProperty().addListener((ov, old_val, new_val) -> Game.AI_MOVE_LAG_TIME = (int) AIMoveSpeedSlider.getValue());
        return AIMoveSpeedSlider;
    }

    private Slider getAIDifficultySlider() {//TODO one of these for each AI
        Slider AIDifficultySlider = new Slider(0, 1, 1);

        //slider value range and default
        AIDifficultySlider.setMin(1);
        AIDifficultySlider.setMax(7);
        AIDifficultySlider.setValue(Game.AI_MAX_SEARCH_DEPTH);

        //major numbered slider intervals
        AIDifficultySlider.setMajorTickUnit(1);
        //minor intervals between major intervals
        AIDifficultySlider.setMinorTickCount(0);

        //slider config
        AIDifficultySlider.setShowTickLabels(true);
        AIDifficultySlider.setShowTickMarks(true);
        AIDifficultySlider.setSnapToTicks(true);

        AIDifficultySlider.valueProperty().addListener((ov, old_val, new_val) -> Game.AI_MAX_SEARCH_DEPTH = (int) AIDifficultySlider.getValue());
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
            game.triggerReset();
        });
        return newGameButton;
    }

}

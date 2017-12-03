import java.awt.Desktop;
import java.net.URI;

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

/**
 * represents the graphical user interface of system, initialises the backend
 * and is the top of the applications class hierarchy besides Main
 */
public class GUI {

    // this should be the first text printed to the games output feed upon a new game
    public static final String GAME_PREAMBLE_AND_INSTRUCTIONS
            = "Welcome!!! to Calum Storey Davidson's University Of Sussex - Knowledge And Reasoning "
              + "- checkers game coursework.\n\nInstructions:\n"
              + "- Drag and drop units with your mouse to make your moves\n"
              + "- Green squares are units that can move.\n"
              + "- Blue squares are where they can go.\n"
              + "- And red squares are mandatory attacks.\n"
              + "---------------------------------------------------\n";

    // the games output / announcement feed
    public static TextArea output;

    //the underlying game backend
    private Game game;

    //the base JavaFX container for the application
    private Stage primaryStage;

    /**
     * create and display a new game application instance
     *
     * @param primaryStage the highest level javaFX container provided implicitly to the new application instance
     */
    public GUI(Stage primaryStage) {
        configureApplicationWindow(primaryStage);

        initialiseApplicationBackend();

        initialiseApplicationFrontend();

        displayApplication(primaryStage);
    }

    /**
     * store and configure the base application parent container / window
     *
     * @param primaryStage the highest level javaFX container provided implicitly to the new application instance
     */
    public void configureApplicationWindow(Stage primaryStage) {
        this.primaryStage = primaryStage;

        //game window title
        this.primaryStage.setTitle("GUIExample Game");

        //prevent all window resizing
        this.primaryStage.setResizable(false);
        this.primaryStage.initStyle(StageStyle.UNIFIED);
    }

    /**
     * create a new game instance with 2 players
     */
    public void initialiseApplicationBackend() {
        Player initialRedPlayer = new RandomAIPlayer(Team.RED);
        Player initialWhitePlayer = new RandomAIPlayer(Team.WHITE);
        game = new Game(initialRedPlayer, initialWhitePlayer);
    }

    /**
     * initialise the GUI / frontend
     */
    private void initialiseApplicationFrontend() {
        Scene GUI = new Scene(createGUI());
        primaryStage.setScene(GUI);
    }

    /**
     * make the base / parent application window / container visible to the user
     *
     * @param primaryStage
     */
    public void displayApplication(Stage primaryStage) {
        primaryStage.show(); // make the base window and all its children visible
    }

    /**
     * assemble the frontend GUI e.g. controls, game board and output stream
     *
     * @return the assembled GUI
     */
    private Parent createGUI() {
        VBox controls = buildControls();
        Pane gameBoard = getGameBoard();
        setUpGameOutputFeed();

        HBox layout = new HBox(10, controls, gameBoard, output);
        layout.setPadding(new Insets(10));

        return layout;
    }

    /**
     * create a GUI panel for the game board and plug in the game backend component
     *
     * @return the assembled game board GUI block
     */
    private Pane getGameBoard() {
        Pane gameBoard = new Pane();
        int squareEdgeLength = Game.SCALE * Game.TILE_SIZE;
        gameBoard.setMinSize(squareEdgeLength, squareEdgeLength);
        gameBoard.getChildren().setAll(game.getComponents());
        return gameBoard;
    }

    /**
     * create the key game controls e.g. buttons, sliders, menus, etc
     *
     * @return the assembled controls panel of the GUI
     */
    private VBox buildControls() {
        //begin new game button
        Button newGameButton = getNewGameButton();

        //crown stealing toggle
        Button crownStealingToggleButton = getCrownStealingToggleButton();

        //play tile toggle
        Button togglePlayTileButton = getTogglePlayTileButton();

        //god mode toggle
        Button godModeButton = getGodModeButton();

        Button verboseOutputButton = getVerboseOutputButton();

        //game Instructions
        Button displayInstructionsButton = getDisplayInstructionsButton();

        //RandomAIPlayer move speed
        Label AITurnLengthLabel = new Label("AI Player turn length control");
        Slider AITurnLengthSlider = getTurnLengthSlider();

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

        VBox controls = new VBox(10, newGameButton, crownStealingToggleButton, togglePlayTileButton, godModeButton, verboseOutputButton, displayInstructionsButton, teamPlayerMenus, AITurnLengthLabel, AITurnLengthSlider, AIDifficultyLabel, AIDifficultySlider);

        controls.setPrefWidth(300);
        controls.setMinWidth(300);

        return controls;
    }

    /**
     * create the player type menus for selecting what is playing for each team
     *
     * @return the labelled player type menus in a self contained grid panel
     */
    private GridPane getTeamPlayerMenus() {
        ComboBox redPlayer = getPlayerMenu(Team.RED);
        ComboBox whitePlayer = getPlayerMenu(Team.WHITE);

        GridPane playerMenus = new GridPane();
        playerMenus.add(new Label("Red Player:"), 0, 0);
        playerMenus.add(redPlayer, 1, 0);

        playerMenus.add(new Label("White Player:"), 0, 1);
        playerMenus.add(whitePlayer, 1, 1);

        playerMenus.setHgap(10);
        return playerMenus;
    }

    /**
     * create the player type menu for selecting what is playing for the given team
     *
     * @param team the team to get a player options menu for
     * @return the player options menu for the given team
     */
    private ComboBox getPlayerMenu(Team team) {
        ComboBox playerMenu = new ComboBox();
        playerMenu.getItems().setAll("Human", "Random AI", "Negamax AI", "AB Negamax AI");
        playerMenu.getSelectionModel().select("Random AI");
        playerMenu.setOnAction((event -> {
            switch (playerMenu.getSelectionModel().getSelectedIndex()) {
                case 0:
                    game.restartGame(new HumanPlayer(team));
                    break;
                case 1:
                    game.restartGame(new RandomAIPlayer(team));
                    break;
                case 2:
                    game.restartGame(new NegamaxAI(team));
                    break;
                case 3:
                    game.restartGame(new ABNegamaxAI(team));
                    break;
            }
        }));
        return playerMenu;
    }

    /**
     * create the button that allows the user to toggle verbose output in the game feed
     *
     * @return the verbose output toggle button
     */
    private Button getVerboseOutputButton() {
        Button verboseOutputButton = new Button("Disable Verbose Output\n");
        verboseOutputButton.setOnAction(value -> {
            Game.VERBOSE_OUTPUT = !Game.VERBOSE_OUTPUT;
            verboseOutputButton.setText(Game.VERBOSE_OUTPUT ? "Disable Verbose Output\n" : "Enable Verbose Output\n");
            output.appendText(Game.VERBOSE_OUTPUT ? "Verbose Output Enabled\n" : "Verbose Output Disabled\n");
        });
        verboseOutputButton.setMaxWidth(Double.MAX_VALUE);
        return verboseOutputButton;
    }

    /**
     * create the slider that allows the user to increase and decrease the speed of AI moves
     *
     * @return the slider that allows the user to alter the speed of AI moves
     */
    private Slider getTurnLengthSlider() {
        Slider AITurnLengthSlider = new Slider(100, 1000, Game.AI_MOVE_LAG_TIME);
        configureSlider(AITurnLengthSlider, 0, 100);

        AITurnLengthSlider.valueProperty().addListener((ov, old_val, new_val) -> Game.AI_MOVE_LAG_TIME = (int) AITurnLengthSlider.getValue());
        return AITurnLengthSlider;
    }

    /**
     * create the slider that allows the user to increase and decrease the difficulty of AI players,
     * by manipulating the depth of the future state space they are allowed to evaluate for the ideal move
     *
     * @return the slider that allows the user to alter the difficulty of AI players
     */
    private Slider getAIDifficultySlider() {//TODO one of these for each AI?
        Slider AIDifficultySlider = new Slider(1, 7, Game.AI_MAX_SEARCH_DEPTH);
        configureSlider(AIDifficultySlider, 0, 1);

        AIDifficultySlider.valueProperty().addListener((ov, old_val, new_val) -> Game.AI_MAX_SEARCH_DEPTH = (int) AIDifficultySlider.getValue());
        return AIDifficultySlider;
    }

    /**
     * apply basic standard configuration to pre-assembled GUI sliders
     *
     * @param slider        the slider to configure
     * @param minorTickUnit the number of lines between the major value lines
     * @param majorTickUnit the number of major value lines on the entire slider
     */
    public void configureSlider(Slider slider, int minorTickUnit, int majorTickUnit) {
        //major numbered slider intervals
        slider.setMajorTickUnit(majorTickUnit);
        //minor intervals between major intervals
        slider.setMinorTickCount(minorTickUnit);

        //slider config
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setSnapToTicks(true);
    }

    /**
     * create and configure the games output feed
     */
    private void setUpGameOutputFeed() {
        output = new TextArea();
        output.setPrefWidth(350);
        output.setMaxWidth(TextArea.USE_PREF_SIZE);
        output.setMinWidth(TextArea.USE_PREF_SIZE);
        output.setEditable(false);
        output.setPrefRowCount(10);
        output.setPrefColumnCount(20);
        output.setWrapText(true);
    }

    /**
     * create the button that allows the user to toggle god mode in order to manipulate the game state mid game
     *
     * @return the button that allows the user to toggle god mode in order to manipulate the game state mid game
     */
    private Button getGodModeButton() {
        Button godModeButton = new Button("Enable God Mode");
        godModeButton.setOnAction(value -> {
            game.toggleGodMode();
            godModeButton.setText(game.GOD_MODE_ENABLED ? "Disable God Mode" : "Enable God Mode");
            output.appendText(game.GOD_MODE_ENABLED ? "God Mode Enabled\n" : "God Mode Disabled\n");
        });
        godModeButton.setMaxWidth(Double.MAX_VALUE);
        return godModeButton;
    }

    /**
     * create the button that allows the user to toggle which color tiles the game is played on
     *
     * @return the button that allows the user to toggle which color tiles the game is played on
     */
    private Button getTogglePlayTileButton() {
        Button togglePlayTileButton = new Button("Play on Black");
        togglePlayTileButton.setOnAction(value -> {
            Game.PLAY_SQUARE = Game.PLAY_SQUARE == 0 ? 1 : 0;
            togglePlayTileButton.setText(Game.PLAY_SQUARE == 0 ? "Play on White" : "Play on Black"); //TODO what the fuck is up with this fucking button
            output.appendText(String.valueOf(Game.PLAY_SQUARE == 0 ? "You are now playing on the black squares" : "You are now playing on the white squares"));
            game.triggerReset();
        });
        togglePlayTileButton.setMaxWidth(Double.MAX_VALUE);
        return togglePlayTileButton;
    }

    /**
     * create the button that allows the user to have the original instructions for game opened in their browser
     *
     * @return the button that allows the user to have the original instructions for game opened in their browser
     */
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
        displayInstructionsButton.setMaxWidth(Double.MAX_VALUE);
        return displayInstructionsButton;
    }

    /**
     * create the button that allows the user to toggle whether the game is played with the rule that gives pawns the
     * ability to become a king if they kill a king
     *
     * @return the button for toggling whether pawns have the ability to become a king if they kill a king
     */
    private Button getCrownStealingToggleButton() {
        Button crownStealingToggleButton = new Button("Disable King On King Kill");
        crownStealingToggleButton.setOnAction(value -> {
            Game.CROWN_STEALING_ALLOWED = !Game.CROWN_STEALING_ALLOWED;
            crownStealingToggleButton.setText(Game.CROWN_STEALING_ALLOWED ? "Disable King On King Kill" : "Enable King On King Kill");
            output.appendText(Game.CROWN_STEALING_ALLOWED ? "King On King Kill Enabled" : "King On King Kill Disabled");
        });
        crownStealingToggleButton.setMaxWidth(Double.MAX_VALUE);
        return crownStealingToggleButton;
    }

    /**
     * create the button that allows the user to have the game restart as soon as possible
     *
     * @return the button that allows the user to have the game restart as soon as possible
     */
    private Button getNewGameButton() {
        Button newGameButton = new Button("Start New Game");
        newGameButton.setOnAction(value -> game.restartGame(null));
        newGameButton.setMaxWidth(Double.MAX_VALUE);
        return newGameButton;
    }
}

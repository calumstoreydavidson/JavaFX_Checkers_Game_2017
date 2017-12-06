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
    public static final String GAME_PREAMBLE_AND_INSTRUCTIONS = "Welcome!!! to Calum Storey Davidson's University Of Sussex - Knowledge And Reasoning " +
                                                                "- checkers game coursework." +
                                                                "\n\nApplication Instructions:\n" +
                                                                "- Select the types of player to be included in the game\n" +
                                                                "- Control AI player move speed with the AI move speed slider\n" +
                                                                "- Control AI player difficulty with the AI difficulty slider\n" +
                                                                "- Drag and drop units with your mouse to make your moves\n" +
                                                                "- Game rules can be opened in the browser\n" +
                                                                "\nToggleable Features:\n"+
                                                                "- User move highlighting\n"+
                                                                "- AI move highlighting\n"+
                                                                "- User's AI advisor move highlighting\n"+
                                                                "- Allow pawns to become kings when they kill other kings\n"+
                                                                "- Verbose output in the game feed - limit to win alerts\n"+
                                                                "- God mode, move any unit anywhere and toggle units as kings\n"+
                                                                "- Select the colour tiles the game is played on\n"+
                                                                "- Green squares are units that can move.\n" +
                                                                "- Blue squares are where they can go.\n" +
                                                                "- And red squares are mandatory attacks.\n" +
                                                                "---------------------------------------------------\n";

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
    private void configureApplicationWindow(Stage primaryStage) {
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
    private void initialiseApplicationBackend() {
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
     * @param primaryStage the highest level javaFX container provided implicitly to the new application instance
     */
    private void displayApplication(Stage primaryStage) {
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
        int squareEdgeLength = Game.BOARD_SIZE * Game.TILE_SIZE;
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
        Button togglePlayTileToggleButton = getPlayTileToggleButton();

        //god mode toggle
        Button godModeToggleButton = getGodModeToggleButton();

        Button advisorAIToggleButton = getAdvisorAIToggleButton();

        Button verboseOutputToggleButton = getVerboseOutputToggleButton();

        Button userMoveHighlightingToggleButton = getUserMoveHighlightingToggleButton();

        Button AIMoveHighlightingToggleButton = getAIMoveHighlightingToggleButton();

        //game Instructions
        Button displayInstructionsButton = getDisplayInstructionsButton();

        VBox playerControls = getPlayerControls();

        //TODO swap teams sides of the board

        //TODO show board axis

        //TODO make 0,0 in bottom left rather than the top left

        //TODO give each AI its own difficulty slider and make it change the max size of those sliders with the teams AI

        VBox userControls = new VBox(10, newGameButton, crownStealingToggleButton, togglePlayTileToggleButton, godModeToggleButton, verboseOutputToggleButton, userMoveHighlightingToggleButton, AIMoveHighlightingToggleButton, advisorAIToggleButton, displayInstructionsButton, playerControls);

        userControls.setPrefWidth(300);
        userControls.setMinWidth(300);

        return userControls;
    }

    /**
     * get the controls for editing the players of the game
     *
     * @return the controls for editing the players of the game
     */
    private VBox getPlayerControls() {
        //player type selectors
        GridPane teamPlayerMenus = getTeamPlayerMenus();

        //RandomAIPlayer move speed
        VBox AITurnLengthSlider = getAITurnLengthSlider();

        VBox AIDifficultySlider = getAIDifficultySlider();

        return new VBox(10, teamPlayerMenus, AITurnLengthSlider, AIDifficultySlider);
    }

    /**
     * create the player type menus for selecting what is playing for each team
     *
     * @return the labelled player type menus in a self contained grid panel
     */
    private GridPane getTeamPlayerMenus() {
        ComboBox<String> redPlayer = getPlayerMenu(Team.RED);
        ComboBox<String> whitePlayer = getPlayerMenu(Team.WHITE);

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
    private ComboBox<String> getPlayerMenu(Team team) {
        ComboBox<String> playerMenu = new ComboBox<>();
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
                    game.restartGame(new ABNegamaxAI(team, PlayerType.AI));
                    break;
            }
        }));
        return playerMenu;
    }

    /**
     * create the button that allows the user to toggle their move highlighting
     *
     * @return the user move highlighting toggle button
     */
    private Button getUserMoveHighlightingToggleButton() {
        String mechanism = "User Move Highlighting";
        Button userMoveHighlightingToggleButton = new Button("Disable " + mechanism + "\n");

        userMoveHighlightingToggleButton.setOnAction(value -> {
            game.toggleUserMoveHighlighting();
            userMoveHighlightingToggleButton.setText(Game.USER_MOVE_HIGHLIGHTING ? "Disable " + mechanism + "\n" : "Enable " + mechanism + "\n");
            output.appendText(Game.USER_MOVE_HIGHLIGHTING ? mechanism + " Enabled\n" : mechanism + " Disabled\n");

            if(Game.USER_MOVE_HIGHLIGHTING) {
                output.appendText("\n\n- Green squares are units that can move." + "\n- Blue squares are where they can go." + "\n- And red squares are mandatory attacks.\n\n");
            }
        });

        userMoveHighlightingToggleButton.setMaxWidth(Double.MAX_VALUE);
        return userMoveHighlightingToggleButton;
    }

    /**
     * create the button that allows the user to toggle the AI's move highlighting
     *
     * @return the AI move highlighting toggle button
     */
    private Button getAIMoveHighlightingToggleButton() {
        String mechanism = "AI Move Highlighting";
        Button AIMoveHighlightingToggleButton = new Button("Disable " + mechanism + "\n");

        AIMoveHighlightingToggleButton.setOnAction(value -> {
            Game.AI_MOVE_HIGHLIGHTING = !Game.AI_MOVE_HIGHLIGHTING;
            AIMoveHighlightingToggleButton.setText(Game.AI_MOVE_HIGHLIGHTING ? "Disable " + mechanism + "\n" : "Enable " + mechanism + "\n");
            output.appendText(Game.AI_MOVE_HIGHLIGHTING ? mechanism + " Enabled\n" : mechanism + " Disabled\n");
        });

        AIMoveHighlightingToggleButton.setMaxWidth(Double.MAX_VALUE);
        return AIMoveHighlightingToggleButton;
    }

    /**
     * create the button that allows the user to toggle verbose output in the game feed
     *
     * @return the verbose output toggle button
     */
    private Button getVerboseOutputToggleButton() {
        String mechanism = "Verbose Output";
        Button verboseOutputButton = new Button("Disable " + mechanism + "\n");

        verboseOutputButton.setOnAction(value -> {
            Game.VERBOSE_OUTPUT = !Game.VERBOSE_OUTPUT;
            verboseOutputButton.setText(Game.VERBOSE_OUTPUT ? "Disable " + mechanism + "\n" : "Enable " + mechanism + "\n");
            output.appendText(Game.VERBOSE_OUTPUT ? mechanism + " Enabled\n" : mechanism + " Disabled\n");
        });

        verboseOutputButton.setMaxWidth(Double.MAX_VALUE);
        return verboseOutputButton;
    }

    /**
     * create the slider that allows the user to increase and decrease the speed of AI moves
     *
     * @return the slider that allows the user to alter the speed of AI moves
     */
    private VBox getAITurnLengthSlider() {
        Label AITurnLengthLabel = new Label("AI Player Turn Length Control");

        Slider AITurnLengthSlider = new Slider(100, 1000, Game.AI_MOVE_LAG_TIME);
        configureSlider(AITurnLengthSlider, 0, 100);

        AITurnLengthSlider.valueProperty().addListener((ov, old_val, new_val) -> Game.AI_MOVE_LAG_TIME = (int) AITurnLengthSlider.getValue());
        return new VBox(10, AITurnLengthLabel, AITurnLengthSlider);
    }

    /**
     * create the slider that allows the user to increase and decrease the difficulty of AI players,
     * by manipulating the depth of the future state space they are allowed to evaluate for the ideal move
     *
     * @return the slider that allows the user to alter the difficulty of AI players
     */
    private VBox getAIDifficultySlider() {
        Label AIDifficultyLabel = new Label("AI Player Difficulty Control");

        Slider AIDifficultySlider = new Slider(1, 7, Game.AI_MAX_SEARCH_DEPTH);
        configureSlider(AIDifficultySlider, 0, 1);

        AIDifficultySlider.valueProperty().addListener((ov, old_val, new_val) -> Game.AI_MAX_SEARCH_DEPTH = (int) AIDifficultySlider.getValue());
        return new VBox(10, AIDifficultyLabel, AIDifficultySlider);
    }

    //TODO - it would be interesting to implement a board scale slider, to allow the player to experiment with board sizes, requires further decoupling of code to be feasible

    //TODO - allow both teams max number of units to be controlled by the user, e.g. to handicap the AI

    //TODO separate team controls, so that different teams can have different values

    /**
     * apply basic standard configuration to pre-assembled GUI sliders
     *
     * @param slider        the slider to configure
     * @param minorTickUnit the number of dashes between the major value lines
     * @param majorTickUnit the number of major value dashes on the entire slider
     */
    private void configureSlider(Slider slider, int minorTickUnit, int majorTickUnit) {
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
        output.setPrefWidth(400);
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
    private Button getAdvisorAIToggleButton() {
        String mechanism = "Advisor AI";
        Button advisorAIToggleButton = new Button("Enable " + mechanism + "\n");

        advisorAIToggleButton.setOnAction(value -> {
            game.toggleUserAdvisorAISuggestedMoveHighlighting();
            advisorAIToggleButton.setText(Game.USERS_AI_ADVISOR ? "Disable " + mechanism + "\n" : "Enable " + mechanism + "\n");
            output.appendText(Game.USERS_AI_ADVISOR ? mechanism + " Enabled\n" : mechanism + " Disabled\n");
            output.appendText("WARNING!!! This is an experimental feature, and may decrease performance\n");
        });

        advisorAIToggleButton.setMaxWidth(Double.MAX_VALUE);
        return advisorAIToggleButton;
    }

    /**
     * create the button that allows the user to toggle god mode in order to manipulate the game state mid game
     *
     * @return the button that allows the user to toggle god mode in order to manipulate the game state mid game
     */
    private Button getGodModeToggleButton() {
        String mechanism = "God Mode";
        Button godModeToggleButton = new Button("Enable " + mechanism + "\n");

        godModeToggleButton.setOnAction(value -> {
            game.toggleGodMode();
            godModeToggleButton.setText(Game.GOD_MODE_ENABLED ? "Disable " + mechanism + "\n" : "Enable " + mechanism + "\n");
            output.appendText(Game.GOD_MODE_ENABLED ? mechanism + " Enabled\n" : mechanism + " Disabled\n");
        });

        godModeToggleButton.setMaxWidth(Double.MAX_VALUE);
        return godModeToggleButton;
    }

    /**
     * create the button that allows the user to toggle which color tiles the game is played on
     *
     * @return the button that allows the user to toggle which color tiles the game is played on
     */
    private Button getPlayTileToggleButton() {
        Button togglePlayTileButton = new Button("Play on Black");

        togglePlayTileButton.setOnAction(value -> {
            Game.PLAY_SQUARE = Game.PLAY_SQUARE == 0 ? 1 : 0;
            togglePlayTileButton.setText(Game.PLAY_SQUARE == 0 ? "Play on White" : "Play on Black");
            output.appendText(String.valueOf(Game.PLAY_SQUARE == 0 ? "You are now playing on the black squares\n" : "You are now playing on the white squares\n"));
            game.restartGame(null);
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
            } catch (Exception exception) {
                output.appendText("Apologies, your browser can't be accessed at this time\n");
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
        String mechanism = "Crown Stealing";
        Button crownStealingToggleButton = new Button("Disable " + mechanism + "\n");

        crownStealingToggleButton.setOnAction(value -> {
            Game.CROWN_STEALING_ALLOWED = !Game.CROWN_STEALING_ALLOWED;
            crownStealingToggleButton.setText(Game.CROWN_STEALING_ALLOWED ? "Disable " + mechanism + "\n" : "Enable " + mechanism + "\n");
            output.appendText(Game.CROWN_STEALING_ALLOWED ? mechanism + " Enabled\n" : mechanism + " Disabled\n");
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

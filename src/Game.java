import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Group;
import javafx.scene.Node;

public class Game {

    //TODO make these final and initialised upon game creation
    //the configurable number of tiles across the game board should be
    public static int SCALE = 8;

    //the configurable number of pixels across each board tile should be
    public static int TILE_SIZE = 100;

    //the configurable maximum number of units red is allowed to start with
    public static int MAX_RED_POPULATION = Integer.MAX_VALUE;

    //the configurable maximum number of units white is allowed to start with
    public static int MAX_WHITE_POPULATION = Integer.MAX_VALUE;

    //toggle which colour squares the game is played on - 1 for white, 0 for black
    public static int PLAY_SQUARE = 1;

    //the configurable option to allow pawns to become kings when they kill enemy kings
    public static boolean CROWN_STEALING_ALLOWED = true;

    //the configurable option to use god mode and manipulate the board state before continuing
    public static boolean GOD_MODE_ENABLED = false;

    //the configurable amount of time the AI should lag its movement on the GUI to help the user see whats happening
    public static int AI_MOVE_LAG_TIME = 500; //milliseconds

    // whether or not the game should be reset at the next opportunity -
    // protects against bugs tht develop from resetting the game while turn threads are still running
    public static boolean RESET_GAME;

    // the configurable maximum depth to which the AI is allowed to search the future state space for optimal moves
    public static int AI_MAX_SEARCH_DEPTH = 7; //initial difficulty, max for negamax, around half for ABNegamax

    //the configurable option to prevent superfluous output to the games announcement feed
    public static boolean VERBOSE_OUTPUT = true;

    //the configurable option to allow the user to toggle their move highlighting
    public static boolean USER_MOVE_HIGHLIGHTING = true;

    //the configurable option to allow the user to toggle the AI's move highlighting
    public static boolean AI_MOVE_HIGHLIGHTING = true;

    //the games Red player
    private Player redPlayer;

    // the games White Player
    private Player whitePlayer;

    //the Board that contains the state of the actual current game being played
    private DisplayBoard displayBoard;

    //the game components that will be passed upt ot the GUI and displayed
    private Group components;

    /**
     * creates a new game with the specified players
     *
     * @param redPlayer   the provided red player
     * @param whitePlayer the provided white player
     */
    public Game(Player redPlayer, Player whitePlayer) {
        components = new Group();
        this.redPlayer = redPlayer;
        this.whitePlayer = whitePlayer;
        resetGame();
        scheduleNewGame();
    }

    /**
     * schedule the game to be restarted once the JavaFX core thread has finished whatever it is doing
     */
    public void scheduleNewGame() {
        Platform.runLater(() -> startNewGame());
    }

    /**
     * reinitialise a new game, reset the necessary variables and run the new game
     */
    public void startNewGame() {
        GUI.output.setText(GUI.GAME_PREAMBLE_AND_INSTRUCTIONS);
        resetGame();
        if (VERBOSE_OUTPUT) {
            GUI.output.appendText("           A NEW GAME BEGINS --FIGHT!--\n");
            printNewTurnDialogue();
        }

        RESET_GAME = false;
        nextPlayersTurn();
    }

    /**
     * create a new board with new mouse programmed units, set all units untouchable, refresh the GUI components list
     * and reset the players
     */
    private void resetGame() {
        displayBoard = new DisplayBoard();
        setAllUnitsLocked(true);
        addMouseControlToAllUnits();

        components.getChildren().setAll(displayBoard.getGUIComponents().getChildren());

        redPlayer.resetPlayer();
        whitePlayer.resetPlayer();
    }

    /**
     * when it is a human players turn, it is fine to just abruptly restart the game but when it is not,
     * there may be turn threads still active and so we must trigger a reset instead which allows the threads to run
     * their course and be cleaned up before the game restarts
     *
     * @param player the new player playing in the new game
     */
    public void restartGame(Player player) {
        if (getCurrentPlayer().isPlayerHuman()) {
            setPlayer(player);//this has to be inside the if, as the if is never true otherwise
            startNewGame();
        } else {
            setPlayer(player);
            triggerReset();
        }
    }

    /**
     * if god mode is active, then disable it and refresh the turn, if it is not active, then enable it and remove
     * all highlighting and set all units to react to mouse events
     */
    public void toggleGodMode() {
        if (GOD_MODE_ENABLED) {
            refreshTurn(); // deactivation
        } else {
            displayBoard.resetTileColors();//activation
            setAllUnitsLocked(false);
        }
        GOD_MODE_ENABLED = !GOD_MODE_ENABLED;
    }

    /**
     * if user move highlighting is active then disable it and erase all board highlighting, else activate and apply it
     */
    public void toggleUserMoveHighlighting() {
        if (USER_MOVE_HIGHLIGHTING) {
            USER_MOVE_HIGHLIGHTING = false;
            displayBoard.resetTileColors();// deactivation
        } else {
            USER_MOVE_HIGHLIGHTING = true;
            refreshBoard(); //activation
        }
    }

    /**
     * set whether all units should react to mouse events / locks units against user manipulation
     *
     * @param unitsLocked whether units should react
     */
    public void setAllUnitsLocked(boolean unitsLocked) {
        displayBoard.getRedUnits().setMouseTransparent(unitsLocked);
        displayBoard.getWhiteUnits().setMouseTransparent(unitsLocked);
    }

    /**
     * refresh the moves available to the current turns player and refresh the boards
     */
    private void refreshTurn() {
        refreshTeamsAvailableMoves();
        refreshBoard();
    }

    /**
     * refresh the moves available to the new turns player and begin their first move
     */
    public void nextPlayersTurn() {
        refreshTeamsAvailableMoves();
        runNextMove();
    }

    /**
     * refresh the board, if game over then schedule new game, else run current players current move
     */
    public void runNextMove() {
        refreshBoard();
        if (isGameOver()) {
            scheduleNewGame();
        } else {
            runPlayerMove(getCurrentPlayer());
        }
    }

    /**
     * refresh the boards highlighting and which units react to mouse events / are locked against user controls
     */
    private void refreshBoard() {
        displayBoard.resetTileColors();
        if (getCurrentPlayer().isPlayerHuman()) {
            refreshUserSupportHighlighting();
            displayBoard.makeCurrentTeamAccessible(redPlayer, whitePlayer);
        }else {
            setAllUnitsLocked(true);
        }
    }

    /**
     * get the current turns player
     *
     * @return the player who's turn it  currently is
     */
    public Player getCurrentPlayer() {
        return redPlayer.isPlayersTurn() ? redPlayer : whitePlayer;
    }

    /**
     * apply the move highlighting on the board for the current player their available moves
     */
    private void refreshUserSupportHighlighting() {
        if (USER_MOVE_HIGHLIGHTING) {
            displayBoard.highlightUsersAvailableMoves();
        }
    }

    /**
     * check if the game is over or has been ordered to reset
     *
     * @return return whether the game is over or has been ordered to reset
     */
    private boolean isGameOver() {
        if (displayBoard.getPossibleMoves().isEmpty()) {
            if (redPlayer.isPlayersTurn()) {
                GUI.output.appendText("---------------------------------------------------\n");
                GUI.output.appendText("!!!!!!!!!!!!!!!!!!!  WHITE WINS  !!!!!!!!!!!!!!!!!!\n");
                GUI.output.appendText("---------------------------------------------------\n");
                return true;
            } else {
                GUI.output.appendText("---------------------------------------------------\n");
                GUI.output.appendText("!!!!!!!!!!!!!!!!!!!   RED WINS   !!!!!!!!!!!!!!!!!!\n");
                GUI.output.appendText("---------------------------------------------------\n");
                return true;
            }
        }
        return RESET_GAME; //returns true if game set to reset
    }

    /**
     * this does not stop the GUI - because on each turn, the current thread creates and starts a successor thread
     * containing the next turn before being disposed of
     *
     * @param player the player whose move should be retrieved and executed
     */
    public void runPlayerMove(Player player) {
        //create the task to run the next turn
        Task<Void> task = new Task<Void>() {
            @Override public Void call() throws Exception {
                Thread.sleep(AI_MOVE_LAG_TIME); //ensure it is clear to the player what is happening

                player.getPlayerMove(displayBoard).ifPresent(move -> {
                    if(AI_MOVE_HIGHLIGHTING) {
                        displayBoard.getTile(move.getTarget()).highlightAIMove(); // show the user what the AI is moving and where
                        displayBoard.getTile(move.getOrigin()).highlightAIMove();
                    }

                    try {
                        Thread.sleep(AI_MOVE_LAG_TIME); //ensure it is clear to the player what is happening
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Platform.runLater(() -> executePlayerMove(move));
                });

                return null;
            }
        };
        // create and trigger the new thread to run the new turn
        new Thread(task).start();
        //current thread finishes, climbs the call stack, and is disposed off
    }

    /**
     * run the provided move for the current player,
     * if it has begun a mulitiJump move
     * then get the current players next move(the next part of the multijump)
     * else finish the turn and make it the other players turn
     *
     * @param move the move to apply to the board for the current player
     */
    private void executePlayerMove(Move move) {
        boolean turnFinished = displayBoard.executeMove(move);
        if (turnFinished) { //actual next players turn
            endTurn();
        } else { //for multikill logic, next move, not next turn
            runNextMove();
        }
    }

    /**
     * wrap up the current turn, make it the other players turn and trigger that new turn
     */
    private void endTurn() {
        displayBoard.setUnitInMotion(null); //empty / reset the identifier for multijump moves
        switchPlayerTurn();
        printNewTurnDialogue();
        nextPlayersTurn();
    }

    /**
     * update players and board on who's turn it is
     */
    private void switchPlayerTurn() {
        redPlayer.switchTurn();
        whitePlayer.switchTurn();
        displayBoard.setNextPlayer();
    }

    /**
     * announce a new turn in the game output feed
     */
    private void printNewTurnDialogue() {
        if (VERBOSE_OUTPUT) {
            String player = redPlayer.isPlayersTurn() ? "Red" : "White";
            GUI.output.appendText("---------------------------------------------------\n");
            GUI.output.appendText("                                  " + player + "'s Turn\n");
        }
    }

    /**
     * trigger an update of the possible moves available to the current turns player
     */
    private void refreshTeamsAvailableMoves() {
        displayBoard.refreshTeamsAvailableMoves(getCurrentPlayer().getPlayerTeam());
    }

    /**
     * get the game components to be passed up to and displayed by the GUI
     *
     * @return the game components to be passed up to and displayed by the GUI
     */
    public Group getComponents() {
        return components;
    }

    /**
     * slot the given player into its teams player slot if it exists
     *
     * @param player the player to put in its teams variable
     */
    public void setPlayer(Player player) {
        if (player != null) {
            if (player.getPlayerTeam() == Team.RED) {
                redPlayer = player;
            } else {
                whitePlayer = player;
            }
        }
    }

    /**
     * for all the units in play add mouseReleased action instructions
     */
    private void addMouseControlToAllUnits() {
        for (Node node : displayBoard.getRedUnits().getChildren()) {
            Unit unit = (Unit) node;
            addMouseControlToUnit(unit);
        }
        for (Node node : displayBoard.getWhiteUnits().getChildren()) {
            Unit unit = (Unit) node;
            addMouseControlToUnit(unit);
        }
    }

    /**
     * for the given unit add mouseReleased action instructions
     *
     * @param unit the unit to add mouseReleased action instructions to
     */
    private void addMouseControlToUnit(Unit unit) {
        unit.setOnMouseReleased(e -> {
            int targetX = Coordinates.toBoard(unit.getLayoutX());
            int targetY = Coordinates.toBoard(unit.getLayoutY());

            Coordinates origin = unit.getPos();
            Coordinates mouseDragTarget = new Coordinates(origin, targetX, targetY);

            if (Game.GOD_MODE_ENABLED) {
                programUnitGodMode(unit, origin, mouseDragTarget);
            } else {
                programUnitNormalMode(unit, origin, mouseDragTarget);
            }
        });
    }

    /**
     * run the units regular non-god-mode action upon mouse release
     *
     * @param unit   the unit to program
     * @param origin the units starting position
     * @param mouseDragTarget the position to place the unit
     */
    private void programUnitNormalMode(Unit unit, Coordinates origin, Coordinates mouseDragTarget) {
        Move actualMove = null;
        for (Move move : displayBoard.getPossibleMoves()) {
            if (move.getOrigin().equals(origin) && move.getTarget().equals(mouseDragTarget)) {
                actualMove = move;
                break;
            }
        }
        if (actualMove == null) {
            actualMove = new Move(unit.getPos(), mouseDragTarget, MoveType.NONE);
            actualMove.setInvalidMoveExplanation(getInvalidMoveError(actualMove));
        }
        executePlayerMove(actualMove);
    }

    /**
     * figure out what was wrong with the unacceptable move, and return an InvalidMoveError
     *
     * @param move the move containing the point the user dragged a unit too that must have been unacceptable
     * @return invalid move error
     */
    private InvalidMoveError getInvalidMoveError(Move move){
        Coordinates mouseDragTarget = move.getTarget();
        Coordinates origin = move.getOrigin();

        InvalidMoveError invalidMoveError;
        if (mouseDragTarget.isOutsideBoard()){
            invalidMoveError = InvalidMoveError.OUTSIDE_BOARD_ERROR;
        }else if (origin.equals(mouseDragTarget) ){
            invalidMoveError = InvalidMoveError.SAME_POSITION_ERROR;
        }else if (displayBoard.isOccupiedTile(mouseDragTarget)){
            invalidMoveError = InvalidMoveError.TILE_ALREADY_OCCUPIED_ERROR;
        }else if (!mouseDragTarget.isPlaySquare()){
            invalidMoveError = InvalidMoveError.NOT_PLAY_SQUARE_ERROR;
        }else {
            invalidMoveError = InvalidMoveError.DISTANT_MOVE_AND_CATCHALL_ERROR;
        }
        return invalidMoveError;
    }

    /**
     * run the units god-mode action upon mouse release
     *
     * @param unit   the unit to program
     * @param origin the units starting position
     * @param mouseDragTarget the position to place the unit
     */
    private void programUnitGodMode(Unit unit, Coordinates origin, Coordinates mouseDragTarget) {
        if (!mouseDragTarget.isOutsideBoard()) {
            if (origin.equals(mouseDragTarget)) {
                unit.toggleKing();
                displayBoard.moveUnit(origin, mouseDragTarget, unit, false);
            } else {
                displayBoard.moveUnit(origin, mouseDragTarget, unit, false);
            }
        } else {
            unit.abortMove();
        }
    }

    /**
     * set the game to reset itself at the earliest opportunity
     */
    public void triggerReset() {
        RESET_GAME = true;
    }

}
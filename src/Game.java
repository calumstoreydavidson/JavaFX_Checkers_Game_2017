import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Group;
import javafx.scene.Node;

public class Game { //extends Application {

    public static final int SCALE = 8;
    public static final int TILE_SIZE = 100;
    public static final int MAX_RED_POPULATION = Integer.MAX_VALUE;
    public static final int MAX_WHITE_POPULATION = Integer.MAX_VALUE;
    //toggle which colour squares the game is played on - 1 for white, 0 for black
    public static int PLAY_SQUARE = 1;
    public static boolean CROWN_STEALING_ALLOWED = true;
    public static boolean GOD_MODE_ENABLED = false;
    public static int AI_MOVE_LAG_TIME = 500; //milliseconds
    public static boolean RESET_GAME;
    public static int AI_MAX_SEARCH_DEPTH = 7; //Negamax AI Difficulty
    public static boolean VERBOSE_OUTPUT = true;//TODO add a button for this

    private Player redPlayer;
    private Player whitePlayer;
    private DisplayBoard displayBoard;
    private Group components;

    public Game(Player redPlayer, Player whitePlayer) {
        components = new Group();
        this.redPlayer = redPlayer;
        this.whitePlayer = whitePlayer;
        resetGame();
        scheduleNewGame();
    }

    public void scheduleNewGame() {
        Platform.runLater(() -> startNewGame());
    }

    public void startNewGame() {
        resetGame();
        if (VERBOSE_OUTPUT) {
            Main.output.appendText("\n---------------------------------------------------\n\n");
            Main.output.appendText("A NEW GAME BEGINS --FIGHT!--\n");
            printNewTurnDialogue();
        }

        RESET_GAME = false;
        nextPlayersTurn();
    }

    private void resetGame() {
        displayBoard = new DisplayBoard();
        addMouseControlToAllUnits();

        components.getChildren().setAll(displayBoard.getGUIComponents().getChildren());

        redPlayer.resetPlayer();
        whitePlayer.resetPlayer();
    }

    public void toggleGodMode() {
        if (GOD_MODE_ENABLED) {
            refreshTurn();
        } else {
            displayBoard.resetTileColors();
            displayBoard.getRedUnits().setMouseTransparent(false);
            displayBoard.getWhiteUnits().setMouseTransparent(false);
        }
        GOD_MODE_ENABLED = !GOD_MODE_ENABLED;
    }

    private void refreshTurn() {
        refreshTeamsAvailableMoves();
        refreshBoard();
    }

    public void nextPlayersTurn() {
        refreshTeamsAvailableMoves();
        runNextMove();
    }

    public void runNextMove() {
        refreshBoard();
        if (isGameOver()) {
            scheduleNewGame();
        } else {
            runPlayerMove(getCurrentPlayer());
        }
    }

    private void refreshBoard() {
        displayBoard.resetTileColors();
        if (getCurrentPlayer().isPlayerHuman()) {
            refreshUserSupportHighlighting();
            displayBoard.makeCurrentTeamAccessible(redPlayer, whitePlayer);
        }
    }

    public Player getCurrentPlayer() {
        return redPlayer.isPlayersTurn() ? redPlayer : whitePlayer;
    }

    private void refreshUserSupportHighlighting() {
        displayBoard.highlightAvailableMoves();
    }

    private boolean isGameOver() {
        if (displayBoard.getPossibleMoves().isEmpty()) {
            if (redPlayer.isPlayersTurn()) {
                Main.output.appendText("---------------------------------------------------\n");
                Main.output.appendText("!!!!!!!!!!!!!!!!!!!  WHITE WINS  !!!!!!!!!!!!!!!!!!\n");
                Main.output.appendText("---------------------------------------------------\n");
                return true;
            } else {
                Main.output.appendText("---------------------------------------------------\n");
                Main.output.appendText("!!!!!!!!!!!!!!!!!!!   RED WINS   !!!!!!!!!!!!!!!!!!\n");
                Main.output.appendText("---------------------------------------------------\n");
                return true;
            }
        }
        return RESET_GAME; //returns true if game set to reset
    }

    //this does work and does not gate the GUI - because on each turn, the current thread creates a successor thread
    // containing the next turn, the new thread is started and the current thread is disposed of.
    public void runPlayerMove(Player player) {
        //create the task to run the next turn
        Task<Void> task = new Task<Void>() {
            @Override public Void call() throws Exception {
                Thread.sleep(AI_MOVE_LAG_TIME);

                player.getPlayerMove(displayBoard).ifPresent(move -> {
                    displayBoard.getTile(move.getTarget()).highlightAIMove();
                    displayBoard.getTile(move.getOrigin()).highlightAIMove();

                    try {
                        Thread.sleep(AI_MOVE_LAG_TIME);
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

    private void executePlayerMove(Move move) {
        boolean turnFinished = displayBoard.executeMove(move);
        if (turnFinished) {
            //actual next players turn
            displayBoard.setMovingUnit(null);
            switchPlayerTurn();
            printNewTurnDialogue();
            nextPlayersTurn();
        } else {
            //for multikill logic, next move, not next turn
            runNextMove();
        }
    }

    private void switchPlayerTurn() {
        redPlayer.switchTurn();
        whitePlayer.switchTurn();
        displayBoard.setNextPlayer();
    }

    private void printNewTurnDialogue() {
        if (VERBOSE_OUTPUT) {
            Main.output.appendText("\n---------------------------------------------------\n\n");
            String player = redPlayer.isPlayersTurn() ? "Red" : "White";
            Main.output.appendText(player + "'s Turn\n");
        }
    }

    private void refreshTeamsAvailableMoves() {
        displayBoard.getTeamMoves(getCurrentPlayer().getPlayerTeam());
    }

    public Group getComponents() {
        return components;
    }

    public boolean isHumanPlaying() {
        return redPlayer.isPlayerHuman() || whitePlayer.isPlayerHuman();
    }

    public void setPlayer(Player player) {
        if (player.getPlayerTeam() == Team.RED){
            this.redPlayer = player;
        }else {
            this.whitePlayer = player;
        }
    }

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

    private void addMouseControlToUnit(Unit unit) {
        unit.setOnMouseReleased(e -> {
            int targetX = Coordinates.toBoard(unit.getLayoutX());
            int targetY = Coordinates.toBoard(unit.getLayoutY());

            Coordinates origin = unit.getPos();
            Coordinates target = new Coordinates(origin, targetX, targetY);

            if (Game.GOD_MODE_ENABLED) {
                programUnitGodMode(unit, origin, target);
            } else {
                programUnitNormalMode(unit, origin, target);
            }
        });
    }

    private void programUnitNormalMode(Unit unit, Coordinates origin, Coordinates target) {
        Move actualMove = null;
        for (Move move : displayBoard.getPossibleMoves()) {
            if (move.getOrigin().equals(origin) && move.getTarget().equals(target)) {
                actualMove = move;
                break;
            }
        }
        if (actualMove == null) {
            actualMove = new Move(unit.getPos(), target, MoveType.NONE);
        }

        executePlayerMove(actualMove);
    }

    private void programUnitGodMode(Unit unit, Coordinates origin, Coordinates target) {
        if (!target.isOutsideBoard()) {
            if (origin.equals(target)) {
                unit.toggleKing();
                displayBoard.moveUnit(origin, target, unit, false);
            } else {
                displayBoard.moveUnit(origin, target, unit, false);
            }
        }else {
            unit.abortMove();
        }
    }

    public void triggerReset() {
        RESET_GAME = true;
    }

}
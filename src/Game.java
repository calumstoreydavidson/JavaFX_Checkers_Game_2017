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
    public static boolean DEVELOPMENT_MODE_ENABLED = false;
    public static int AI_MOVE_LAG_TIME = 500; //milliseconds
    public static boolean RESET_GAME;

    private Player redPlayer;
    private Player whitePlayer;
    private Board board;
    private Group components;

    public Game(Player redPlayer, Player whitePlayer) {
        components = new Group();
        this.redPlayer = redPlayer;
        this.whitePlayer = whitePlayer;
        scheduleNewGame();
    }

    public void scheduleNewGame() {
        Platform.runLater(() -> startNewGame());
    }

    public void startNewGame() {
        resetGame();
        Main.output.appendText("\n---------------------------------------------------\n\n");
        Main.output.appendText("A NEW GAME BEGINS --FIGHT!--\n");
        printNewTurnDialogue();

        RESET_GAME = false;
        nextPlayersTurn();
    }

    private void resetGame() {
        board = new Board();
        addMouseControlToAllUnits();

        components.getChildren().setAll(board.getComponents().getChildren());

        redPlayer.resetPlayer();
        whitePlayer.resetPlayer();
    }

    public void toggleDevelopmentMode() {
        if (DEVELOPMENT_MODE_ENABLED) {
            refreshTurn();
        } else {
            board.resetTileColors();
            board.getRedUnits().setMouseTransparent(false);
            board.getWhiteUnits().setMouseTransparent(false);
        }
        DEVELOPMENT_MODE_ENABLED = !DEVELOPMENT_MODE_ENABLED;
    }

    public void nextPlayersTurn() {
        refreshTurn();
        if (isGameOver()) {
            scheduleNewGame();
        } else {
            runPlayerMove(getCurrentPlayer());
        }
    }

    private void refreshTurn() {
        refreshTeamsAvailableMoves();
        refreshBoardHighlighting();
        board.makeCurrentTeamAccessible(redPlayer);
    }

    public Player getCurrentPlayer() {
        if (redPlayer.isPlayersTurn()) {
            return redPlayer;
        } else {
            return whitePlayer;
        }
    }

    private void refreshBoardHighlighting() {
        board.resetTileColors();
        board.highlightAvailableMoves();
    }

    private boolean isGameOver() {
        if (board.getPossibleMoves().isEmpty()) {
            if (redPlayer.isPlayersTurn()) {
                Main.output.appendText("!!!WHITE WINS!!!");
                return true;
            } else {
                Main.output.appendText("!!!RED WINS!!!");
                return true;
            }
        } else if (RESET_GAME) {
            return true;
        } else {
            return false;
        }
    }

    public void runPlayerMove(Player player) {
        Task<Void> task = new Task<Void>() {
            @Override public Void call() throws Exception {
                Thread.sleep(AI_MOVE_LAG_TIME);

                player.getPlayerMove(board).ifPresent(move -> {
                    board.getTile(move.getTarget()).highlightAIMove();
                    board.getTile(move.getUnit().getCurrentCoords()).highlightAIMove();

                    try {
                        Thread.sleep(AI_MOVE_LAG_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Platform.runLater(() -> executePlayerMove(move));
                });

                //TODO recolour the unit that moves?
                return null;
            }
        };
        new Thread(task).start();
    }

    private void executePlayerMove(Move move) {
        if (board.executeMove(move)) {
            //actual next players turn
            switchPlayerTurn();
            printNewTurnDialogue();
            nextPlayersTurn();
        } else {
            //for multikill logic, next move, not next turn
            nextPlayersTurn();
        }
    }

    private void switchPlayerTurn() {
        redPlayer.switchTurn();
        whitePlayer.switchTurn();
    }

    private void printNewTurnDialogue() {
        Main.output.appendText("\n---------------------------------------------------\n\n");
        String player = redPlayer.isPlayersTurn() ? "Red" : "White";
        Main.output.appendText(player + "'s Turn\n");
    }

    private void refreshTeamsAvailableMoves() {
        board.getTeamMoves(getCurrentPlayer().getPlayerTeam());
    }

    public Group getComponents() {
        return components;
    }

    public Player getRedPlayer() {
        return redPlayer;
    }

    public void setRedPlayer(Player redPlayer) {
        this.redPlayer = redPlayer;
    }

    public Player getWhitePlayer() {
        return whitePlayer;
    }

    public void setWhitePlayer(Player whitePlayer) {
        this.whitePlayer = whitePlayer;
    }

    private void addMouseControlToAllUnits() {
        for (Node node : board.getRedUnits().getChildren()) {
            Unit unit = (Unit) node;
            addMouseControlToUnit(unit);
        }
        for (Node node : board.getWhiteUnits().getChildren()) {
            Unit unit = (Unit) node;
            addMouseControlToUnit(unit);
        }
    }

    private void addMouseControlToUnit(Unit unit) {
        unit.setOnMouseReleased(e -> {
            int targetX = Coordinates.toBoard(unit.getLayoutX());
            int targetY = Coordinates.toBoard(unit.getLayoutY());

            Coordinates origin = unit.getCurrentCoords();
            Coordinates target = new Coordinates(origin, targetX, targetY);

            if (Game.DEVELOPMENT_MODE_ENABLED) {
                if (origin.equals(target)) {
                    unit.toggleKing();
                    board.moveUnit(origin, target, unit, false);
                } else {
                    board.moveUnit(origin, target, unit, false);
                }
            } else {
                Move actualMove = null;
                for (Move move : board.getPossibleMoves()) {
                    if (move.getUnit().getCurrentCoords().equals(origin) && move.getTarget().equals(target)) {
                        actualMove = move;
                        break;
                    }
                }
                if (actualMove == null) {
                    MoveResult result = new MoveResult(MoveType.NONE);
                    actualMove = new Move(unit, target, result);
                }

                executePlayerMove(actualMove);
            }
        });
    }

    public void triggerReset() {
        RESET_GAME = true;
    }
}
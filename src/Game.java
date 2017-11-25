import static java.lang.System.err;
import static java.lang.System.out;

import java.util.ArrayList;
import java.util.stream.Collectors;

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
    public int AI_MOVE_LAG_TIME = 500; //milliseconds

    private Player redPlayer = new HumanPlayer(Team.RED);
    private Player whitePlayer = new RandomAIPlayer(Team.WHITE);
    private Group components = new Group();
    private Board board = new Board();


    private ArrayList<Move> possibleMoves = new ArrayList<>();

    public void startNewGame() {
        resetGame();
        Main.output.appendText("\n---------------------------------------------------\n\n");
        Main.output.appendText("A NEW GAME BEGINS --FIGHT!--\n");
        printNewTurnDialogue();

        populateBoard();

        nextPlayersTurn();
    }

    private void resetGame() {
        board = new Board();
        redUnits = new Group();
        whiteUnits = new Group();
        components.getChildren().setAll(board.getComponents(), redUnits, whiteUnits);
        if (getCurrentPlayer() == whitePlayer) {
            switchPlayerTurn();
        }
    }

    public void toggleDevelopmentMode() {
        if (DEVELOPMENT_MODE_ENABLED) {
            refreshTurn();
        } else {
            board.resetTileColors();
            redUnits.setMouseTransparent(false);
            whiteUnits.setMouseTransparent(false);
        }
        DEVELOPMENT_MODE_ENABLED = !DEVELOPMENT_MODE_ENABLED;
    }

    private void refreshTurn() {
        refreshTeamsAvailableMoves();
        if (isGameOver()) {
            startNewGame();
        }
        refreshBoardHighlighting();
        makeCurrentTeamAccessible();
    }

    public void nextPlayersTurn() {
        refreshTurn();
        runPlayerMove(getCurrentPlayer());
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
        highlightAvailableMoves();
    }

    private boolean isGameOver() {
        if (possibleMoves.isEmpty()) {
            if (redPlayer.isPlayersTurn()) {
                Main.output.appendText("!!!WHITE WINS!!!");
                return true;
            } else {
                Main.output.appendText("!!!RED WINS!!!");
                return true;
            }
        } else {
            return false;
        }
    }

    private void highlightAvailableMoves() {
        for (Move move : possibleMoves) {
            if (move.getResult().getType() == MoveType.KILL) {
                //TODO make these optional
//                board.getTile(move.getTarget()).highlightAttackDestination();
            } else {
//                board.getTile(move.getTarget()).highlightMoveDestination();
            }
            board.getTile(move.getUnit().getCurrentCoords()).highlightUnit();
        }
    }

    private void makeCurrentTeamAccessible() {
        redUnits.setMouseTransparent(!redPlayer.isPlayersTurn());
        whiteUnits.setMouseTransparent(redPlayer.isPlayersTurn());
    }

    public void runPlayerMove(Player player) {
        Task<Void> task = new Task<Void>() {
            @Override public Void call() throws Exception {
                Thread.sleep(AI_MOVE_LAG_TIME);

                Move AIMove = player.getPlayerMove(board);
                board.getTile(AIMove.getTarget()).highlightAIMove();
                board.getTile(AIMove.getUnit().getCurrentCoords()).highlightAIMove();

                Thread.sleep(AI_MOVE_LAG_TIME);

                Platform.runLater(() -> executeMove(AIMove));

                //TODO recolour the unit that moves?
                return null;
            }
        };
        new Thread(task).start();
    }

    public Board getBoard() {
        return board;
    }

    public void executeMove(Move move) {
        Coordinates origin = move.getTarget().origin;
        Coordinates target = move.getTarget();
        Unit unit = move.getUnit();
        MoveResult result = move.getResult();
        boolean kingIsCreated = result.isKingCreated();

        boolean turnFinished = false;
        switch (result.getType()) {
            case NONE:
                unit.abortMove();
                Main.output.appendText("That Is An Invalid Move\n");
                break;
            case NORMAL:
                moveUnit(origin, target, unit, kingIsCreated);
                turnFinished = true;
                Main.output.appendText(unit.getTeam() + " Move Successful\n");
                break;
            case KILL:
                Unit attackedUnit = result.getAttackedUnit();

                moveUnit(origin, target, unit, kingIsCreated);
                board.killUnit(attackedUnit);

                if (canMove(unit) && canAttack(unit) && !result.isKingCreated()) {
                    possibleMoves = board.getUnitMoves(unit);
                    nextPlayersTurn();
                } else {
                    turnFinished = true;
                }
                Main.output.appendText(unit.getTeam() + " Attack Successful\n");
                break;
        }

        if (turnFinished) {
            switchPlayerTurn();
            printNewTurnDialogue();

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

    private boolean canAttack(Unit unit) {
        return board.getUnitMoves(unit).get(0).getResult().getType() == MoveType.KILL;
    }

    public boolean canMove(Unit unit) {
        return !board.getUnitMoves(unit).isEmpty();
    }

    public void moveUnit(Coordinates origin, Coordinates target, Unit unit, boolean kingIsCreated) {
        unit.move(target);
        board.moveUnit(origin, target, unit);
        Main.output.appendText(unit.getTeam() + " " + target.origin.x + ", " + target.origin.y + " -> " + target.x + ", " + target.y + "\n");

        if (kingIsCreated) {
            unit.toggleKing();
            Main.output.appendText(unit.getCurrentX() + ", " + unit.getCurrentY() + " IS NOW A KING\n");
        }
    }

    private void refreshTeamsAvailableMoves() {
        Team team = redPlayer.isPlayersTurn() ? Team.RED : Team.WHITE;
        possibleMoves = board.getTeamMoves(team);
    }

    public Group getComponents() {
        return components;
    }

    public void setRedPlayer(Player redPlayer) {
        this.redPlayer = redPlayer;
    }

    public void setWhitePlayer(Player whitePlayer) {
        this.whitePlayer = whitePlayer;
    }

    public ArrayList<Move> getPossibleMoves() {
        return possibleMoves;
    }

}
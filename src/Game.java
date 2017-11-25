import static java.lang.System.out;

import java.util.ArrayList;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Group;
import javafx.scene.Node;

public class Game { //extends Application {

    public static final int SCALE = 8;
    public static final int TILE_SIZE = 100;
    private static final int MAX_RED_POPULATION = Integer.MAX_VALUE;
    private static final int MAX_WHITE_POPULATION = Integer.MAX_VALUE;
    //toggle which colour squares the game is played on - 1 for white, 0 for black
    public static int PLAY_SQUARE = 1;
    public static boolean CROWN_STEALING_ALLOWED = true;
    public boolean DEVELOPMENT_MODE_ENABLED = false;
    public int AI_MOVE_LAG_TIME = 500; //milliseconds

    private Player redPlayer = new HumanPlayer(Team.RED);
    private Player whitePlayer = new RandomAIPlayer(Team.WHITE);
    private Group components = new Group();
    private Board board = new Board();
    private Group redUnits = new Group();
    private Group whiteUnits = new Group();

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
        if (getCurrentPlayer() == whitePlayer){
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

    public boolean isGameOver() {
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

                Move AIMove = player.getPlayerMove(possibleMoves);
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

    private void populateBoard() {
        populateRed();
        populateWhite();
    }

    private void populateRed() {
        //Red units keep generating down the board till they hit their border
        int factionBorder = Math.round(SCALE / 3f);
        int placedUnits = 0;
        for (int y = 0; y < SCALE; y++) {
            for (int x = 0; x < SCALE; x++) {
                Coordinates c = new Coordinates(x, y);
                if (y < factionBorder && board.isPlaySquare(c) && placedUnits <= MAX_RED_POPULATION) {
                    Unit unit = new Unit(UnitType.PAWN, Team.RED, c, this);
                    board.getTile(c).setUnit(unit);
                    redUnits.getChildren().add(unit);
                    placedUnits++;
                }
            }
        }
    }

    private void populateWhite() {
        //White units keep generating up the board till they hit their border
        int factionBorder = Math.round((SCALE / 3f) * 2);
        int placedUnits = 0;
        for (int y = SCALE - 1; y >= 0; y--) {
            for (int x = SCALE - 1; x >= 0; x--) {
                Coordinates c = new Coordinates(x, y);
                if (y >= factionBorder && board.isPlaySquare(c) && placedUnits <= MAX_WHITE_POPULATION) {
                    Unit unit = new Unit(UnitType.PAWN, Team.WHITE, c, this);
                    board.getTile(c).setUnit(unit);
                    whiteUnits.getChildren().add(unit);
                    placedUnits++;
                }
            }
        }
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
                killUnit(attackedUnit);

                if (unit.canMove() && canAttack(unit) && !result.isKingCreated()) {
                    possibleMoves = getUnitMoves(unit);
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
        return getUnitMoves(unit).get(0).getResult().getType() == MoveType.KILL;
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

    private void killUnit(Unit unit) {
        board.getTile(unit.getCurrentCoords()).setUnit(null);
        if (unit.isRed()) {
            redUnits.getChildren().remove(unit);
        } else {
            whiteUnits.getChildren().remove(unit);
        }
    }

    private void refreshTeamsAvailableMoves() {
        possibleMoves = redPlayer.isPlayersTurn() ? getTeamMoves(Team.RED) : getTeamMoves(Team.WHITE);
    }

    public ArrayList<Move> getTeamMoves(Team team) {
        ArrayList<Move> possibleTeamMoves = new ArrayList<>();
        Group teamUnits = team == Team.RED ? redUnits: whiteUnits;

        for (Node node : teamUnits.getChildren()) {
            Unit unit = (Unit) node;
            possibleTeamMoves.addAll(unit.getPossibleMoves());
        }

        return prioritiseAttackMoves(possibleTeamMoves);
    }

    private ArrayList<Move> getUnitMoves(Unit unit) {
        return prioritiseAttackMoves(unit.getPossibleMoves());
    }

    public ArrayList<Move> prioritiseAttackMoves(ArrayList<Move> possibleUnitMoves) {
        ArrayList<Move> attackMoves = new ArrayList<>();
        for (Move move : possibleUnitMoves) {
            if (move.getResult().getType() == MoveType.KILL) {
                attackMoves.add(move);
            }
        }

        if (!attackMoves.isEmpty()) {
            return attackMoves;
        } else {
            return possibleUnitMoves;
        }
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
import static java.lang.System.out;

import java.util.ArrayList;
import java.util.Random;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Group;
import javafx.scene.Node;

public class Game { //extends Application {

    //toggle which colour squares the game is played on - 1 for white, 0 for black
    public static int PLAY_SQUARE = 1;
    public static final int SCALE = 8;
    public static final int TILE_SIZE = 100;
    private static final int MAX_RED_POPULATION = Integer.MAX_VALUE;
    private static final int MAX_WHITE_POPULATION = Integer.MAX_VALUE;
    public static boolean CROWN_STEALING_ALLOWED = true;
    public boolean DEVELOPMENT_MODE_ENABLED = false;
    public int AI_MOVE_LAG_TIME = 500; //milliseconds

    private Player redPlayer = new RandomAIPlayer();
    private Player whitePlayer = new RandomAIPlayer();
    private Group components = new Group();
    private boolean isRedsTurn;
    private Board board = new Board();
    private Group redUnits = new Group();
    private Group whiteUnits = new Group();

    private ArrayList<Move> possibleMoves = new ArrayList<>();
    private Random rand = new Random();
    private boolean nextTurnAllowed;

    public Game(){
//        startNewGame(true);
    }

    public void startNewGame(Player Player1, Player player2) {
        resetGame();
        out.println("");
        out.println("A NEW GAME BEGINS --FIGHT!--");

        populateBoard();

        isRedsTurn = isFirstPlayer;

        nextTurnAllowed = true;
        nextPlayersTurn();
    }

    private void resetGame() {
        board = new Board();
        redUnits = new Group();
        whiteUnits = new Group();
        components.getChildren().setAll(board.getComponents(), redUnits, whiteUnits);
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
        refreshBoardHighlighting();
        makeCurrentTeamAccessible();
    }

    private void nextPlayersTurn(){
        refreshTurn();
        if (isRedsTurn){
            redsTurn();
        } else {
            whitesTurn();
        }
    }

    private void redsTurn(){
        runAIMove();
    }

    private void whitesTurn(){
        runAIMove();
    }

    private void refreshBoardHighlighting() {
        board.resetTileColors();
        highlightAvailableMoves();
        playByPlay();
    }

    public void playByPlay() {
        out.println("");
        String player = isRedsTurn ? "red" : "white";
        out.println(player + "'s turn");

//        for (Move move : possibleMoves) {
//            if (move.getUnit().isKing()) {
//                out.print(move.getUnit().getTeam() + " " + move.getUnit().getType() + ": ");
//            } else {
//                out.print(move.getUnit().getTeam() + " " + move.getUnit().getType() + ": ");
//            }
//            out.print(move.getUnit().getCurrentX() + ", " + move.getUnit().getCurrentY() + " -> ");
//            out.print(move.getTarget().x + ", " + move.getTarget().y + ": ");
//            out.println(move.getResult().getType());
//        }
    }

    private boolean isGameOver() {
        if (redUnits.getChildren().size() == 0) {
            Main.output.setText("!!!WHITE WINS!!!");
            return true;
        }
        if (whiteUnits.getChildren().size() == 0) {
            Main.output.setText("!!!RED WINS!!!");
            return true;
        }
        return false;
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
        redUnits.setMouseTransparent(!isRedsTurn);
        whiteUnits.setMouseTransparent(isRedsTurn);
    }

    public void runAIMove() {
        Task<Void> task = new Task<Void>() {
            @Override public Void call() throws Exception {
                try {
                    Thread.sleep(AI_MOVE_LAG_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Move AIMove = redPlayer.getPlayerMove(possibleMoves);
                board.getTile(AIMove.getTarget()).highlightAIMove();
                board.getTile(AIMove.getUnit().getCurrentCoords()).highlightAIMove();
                try {
                    Thread.sleep(AI_MOVE_LAG_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                isRedsTurn = !isRedsTurn;
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
                    Unit unit = generateUnit(UnitType.PAWN, Team.RED, c);
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
                    Unit unit = generateUnit(UnitType.PAWN, Team.WHITE, c);
                    board.getTile(c).setUnit(unit);
                    whiteUnits.getChildren().add(unit);
                    placedUnits++;
                }
            }
        }
    }

    private Unit generateUnit(UnitType type, Team team, Coordinates c) {
        Unit unit = new Unit(type, team, c, this);

        unit.setOnMouseReleased(e -> {
            int targetX = Coordinates.toBoard(unit.getLayoutX());
            int targetY = Coordinates.toBoard(unit.getLayoutY());

            Coordinates origin = unit.getCurrentCoords();
            Coordinates target = new Coordinates(origin, targetX, targetY);

            if (DEVELOPMENT_MODE_ENABLED) {
                if (origin.equals(target)) {
                    unit.toggleKing();
                    moveUnit(origin, target, unit, false);
                } else {
                    moveUnit(origin, target, unit, false);
                }
            } else {
                Move actualMove = null;
                for (Move move : possibleMoves) {
                    if (move.getUnit().getCurrentCoords().equals(origin) && move.getTarget().equals(target)) {
                        actualMove = move;
                        break;
                    }
                }
                if (actualMove == null) {
                    MoveResult result = new MoveResult(MoveType.NONE);
                    actualMove = new Move(unit, target, result);
                }

                executeMove(actualMove);
            }
        });

        return unit;
    }

    private void executeMove(Move move) {
        Coordinates origin = move.getTarget().origin;
        Coordinates target = move.getTarget();
        Unit unit = move.getUnit();
        MoveResult result = move.getResult();
        boolean kingIsCreated = result.isKingCreated();

        boolean turnFinished = false;
        switch (result.getType()) {
            case NONE:
                unit.abortMove();
                Main.output.setText("That Is An Invalid Move");
                break;
            case NORMAL:
                moveUnit(origin, target, unit, kingIsCreated);
                turnFinished = true;
//                Main.output.setText(unit.getTeam() + " to " + );
                break;
            case KILL:
                Unit attackedUnit = result.getAttackedUnit();

                moveUnit(origin, target, unit, kingIsCreated);
                killUnit(attackedUnit);

                if (unit.canMove() && canAttack(unit) && !result.isKingCreated()) {
                    possibleMoves = getUnitMoves(unit);
                    refreshBoardHighlighting();
                    nextPlayersTurn();
                } else {
                    turnFinished = true;
                }

//                Main.output.setText(unit.getTeam() + " Attack Successful");
                break;
        }

        if (turnFinished) {
            if (isGameOver()) {
                startNewGame(true);
            } else {
                isRedsTurn = !isRedsTurn;
//                refreshTurn();
                nextPlayersTurn();
            }
        }
    }

    private boolean canAttack(Unit unit) {
        return getUnitMoves(unit).get(0).getResult().getType() == MoveType.KILL;
    }

    private void moveUnit(Coordinates origin, Coordinates target, Unit unit, boolean kingIsCreated) {
        unit.move(target);
        board.moveUnit(origin, target, unit);

        if (kingIsCreated) {
            unit.toggleKing();
            out.print(unit.getTeam() + " at ");
            out.println(unit.getCurrentX() + ", " + unit.getCurrentY() + " IS NOW A KING");
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
        if (isRedsTurn) {
            possibleMoves = getTeamMoves(redUnits);
        } else {
            possibleMoves = getTeamMoves(whiteUnits);
        }
    }

    private ArrayList<Move> getTeamMoves(Group team) {
        ArrayList<Move> possibleTeamMoves = new ArrayList<>();

        for (Node node : team.getChildren()) {
            Unit unit = (Unit) node;
            possibleTeamMoves.addAll(unit.getPossibleMoves());
        }

        return prioritiseAttackMoves(possibleTeamMoves);
    }

    private ArrayList<Move> getUnitMoves(Unit unit) {
        return prioritiseAttackMoves(unit.getPossibleMoves());
    }

    private ArrayList<Move> prioritiseAttackMoves(ArrayList<Move> possibleUnitMoves) {
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

}
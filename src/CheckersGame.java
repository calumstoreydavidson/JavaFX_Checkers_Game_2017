import static java.lang.System.out;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class CheckersGame extends Application {

    //toggle which colour squares the game is played on - 1 for white, 0 for black
    static final int PLAY_SQUARE = 1;
    static final int SCALE = 6;
    static final int TILE_SIZE = 100;

    private static final int MAX_RED_POPULATION = Integer.MAX_VALUE;
    private static final int MAX_WHITE_POPULATION = Integer.MAX_VALUE;
    private static final boolean CROWN_STEALING_ALLOWED = true;

    private Stage primaryStage;
    private boolean isRedsTurn;
    private Board board;
    private Group redUnits = new Group();
    private Group whiteUnits = new Group();
    private ArrayList<Move> possibleMoves = new ArrayList<>();
    private Random rand = new Random();

    public static void main(String[] args) {
        launch(args);
    }

    @Override public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Checkers Game");
        resetGame();
    }

    private void resetGame() {
        board = new Board();
        redUnits = new Group();
        whiteUnits = new Group();

        Scene scene = new Scene(createContent());
        this.primaryStage.setScene(scene);
        this.primaryStage.show();
        startNewGame(true); //startNewGame(getUserInput()); TODO because effort
    }

    private Parent createContent() {
        Pane root = new Pane();
        int squareEdgeLength = SCALE * TILE_SIZE;
        root.setMinSize(squareEdgeLength, squareEdgeLength);
        root.getChildren().addAll(board.getComponents(), redUnits, whiteUnits);

        return root;
    }

    private boolean getUserInput() {
        Scanner scanner = new Scanner(System.in);
        out.println("do you wish to go first?, y/n");
        return scanner.next() == "y";
    }

    private void startNewGame(boolean isFirstPlayer) {
        out.println("");
        out.println("A NEW GAME BEGINS --FIGHT!--");

        populateBoard();

        isRedsTurn = isFirstPlayer;
        nextTurn();
    }

    private void redsTurn() {
        if (possibleMoves.isEmpty()) {
            nextTurn();
        }
        nextTurn();
    }

    private void WhitesTurn() {
        if (possibleMoves.isEmpty()) {
            nextTurn();
        }
        nextTurn();
    }

    private void nextTurn() {
        refreshTeamsAvailableMoves();
        refreshBoardHighlighting();
        makeCurrentTeamAccessible();
    }

    private void refreshBoardHighlighting() {
        board.resetTileColors();
        highlightAvailableMoves();
        playByPlay();
    }

    public void playByPlay() {
        out.println("");
        String player = isRedsTurn ? "red" : "white";
        out.println("now " + player + "'s turn");

        for (Node node : redUnits.getChildren()) {
            Unit unit = (Unit) node;
            if (unit.isKing()) {
                out.print(unit.getTeam() + " " + unit.getType() + " at ");
                out.println(unit.getCurrentX() + ", " + unit.getCurrentY() + " IS A KING");
            }
        }
        for (Node node : whiteUnits.getChildren()) {
            Unit unit = (Unit) node;
            if (unit.isKing()) {
                out.print(unit.getTeam() + " " + unit.getType() + " at ");
                out.println(unit.getCurrentX() + ", " + unit.getCurrentY() + " IS A KING");
            }
        }

        for (Move move : possibleMoves) {
            if (move.getUnit().isKing()) {
                out.print(move.getUnit().getTeam() + " " + move.getUnit().getType() + " " + "KING" + ": ");
            } else {
                out.print(move.getUnit().getTeam() + " " + move.getUnit().getType() + ": ");
            }
            out.print(move.getUnit().getCurrentX() + ", " + move.getUnit().getCurrentY() + " -> ");
            out.print(move.getTarget().x + ", " + move.getTarget().y + ": ");
            out.println(move.getResult().getType());
        }
    }

    private boolean isGameOver() {
        if (redUnits.getChildren().size() == 0) {
            out.println("!!!WHITE WINS!!!");
            return true;
        }
        if (whiteUnits.getChildren().size() == 0) {
            out.println("!!!RED WINS!!!");
            return true;
        }
        return false;
    }

    private void highlightAvailableMoves() {
        for (Move move : possibleMoves) {
            if (move.getResult().getType() == MoveType.KILL) {
                board.getTile(move.getTarget()).highlightAttackDestination();
            } else {
                board.getTile(move.getTarget()).highlightMoveDestination();
            }
            board.getTile(move.getUnit().getCurrentCoords()).highlightUnit();
        }
    }

    private void makeCurrentTeamAccessible() {
        redUnits.setMouseTransparent(!isRedsTurn);
        whiteUnits.setMouseTransparent(isRedsTurn);
    }

//    public void getAIMove() {
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        int r = rand.nextInt(possibleMoves.size());
//        Move AIMove = possibleMoves.get(r);
//        System.out.println("");
//        System.out.println("AI moving: " + AIMove.getTarget().origin.x + ", " + AIMove
//                .getTarget().origin.y + " -> " + AIMove.getTarget().x + ", " + AIMove.getTarget().y);
//        doMove(AIMove);
//        isRedsTurn = !isRedsTurn;
//        getAIMove();
//    }

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

            Move actualMove = null;
            for (Move move : possibleMoves) {
                if (coordsAreEqual(move.getUnit().getCurrentCoords(), origin) && coordsAreEqual(move.getTarget(), target)) {
                    actualMove = move;
                    break;
                }
            }
            if (actualMove == null) {
                MoveResult result = new MoveResult(MoveType.NONE);
                actualMove = new Move(unit, target, result);
            }

            doMove(actualMove);
        });

        return unit;
    }

    private boolean coordsAreEqual(Coordinates pos1, Coordinates pos2) {
        return pos1.x == pos2.x && pos1.y == pos2.y;
    }

    private void doMove(Move move) {
        Coordinates origin = move.getTarget().origin;
        Coordinates target = move.getTarget();
        Unit unit = move.getUnit();
        MoveResult result = move.getResult();
        boolean kingCreated;

        boolean turnFinished = false;
        switch (result.getType()) {
            case NONE:
                unit.abortMove();
                break;
            case NORMAL:
                moveUnit(origin, target, unit);
                turnFinished = true;
                break;
            case KILL:
                Unit attackedUnit = result.getAttackedUnit();

                kingCreated = moveUnit(origin, target, unit);

                if (attackedUnit.isKing() && !unit.isKing() && CROWN_STEALING_ALLOWED) {
                    unit.crownKing();
                    kingCreated = true;
                    out.print(unit.getTeam() + " at ");
                    out.println(unit.getCurrentX() + ", " + unit.getCurrentY() + " IS NOW A KING");
                }

                killUnit(attackedUnit);

                if (unit.canMove() && canAttack(unit) && !kingCreated) {
                    possibleMoves = getUnitMoves(unit);
                    refreshBoardHighlighting();
                } else {
                    turnFinished = true;
                }

                break;
        }

        if (turnFinished) {
            if (isGameOver()) {
                resetGame();
            } else {
                isRedsTurn = !isRedsTurn;
                nextTurn();
            }
        }
    }

    private boolean canAttack(Unit unit) {
        return getUnitMoves(unit).get(0).getResult().getType() == MoveType.KILL;
    }

    private boolean moveUnit(Coordinates origin, Coordinates target, Unit unit) {
        unit.move(target);
        board.moveUnit(origin, target, unit);

        if (target.isEnemyKingRow(unit.getTeam()) && !unit.isKing()) {
            unit.crownKing();
            return true;
        } else {
            return false;
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

}
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
    static final int SCALE = 8;
    static final int TILE_SIZE = 100;
    private static final int MAX_RED_POPULATION = Integer.MAX_VALUE;
    private static final int MAX_WHITE_POPULATION = Integer.MAX_VALUE;
    private Stage primaryStage;
    private boolean isRedsTurn;
//    private Tile[][] board = new Tile[SCALE][SCALE];
    private Board board;
//    private Group tiles = new Group();
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

    public void resetGame() {
        board = new Board();
//        tiles = new Group();
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
        System.out.println("do you wish to go first?, y/n");
        return scanner.next() == "y";
    }

    private void startNewGame(boolean isFirstPlayer) {
        System.out.println("");
        System.out.println("A NEW GAME BEGINS --FIGHT!--");

//        generateBoard();
        populateBoard();

        isRedsTurn = isFirstPlayer;
        nextTurn();
    }

    private void redsTurn() {
        if (possibleMoves.size() == 0) {
            nextTurn();
        }
        nextTurn();
    }

    private void WhitesTurn() {
        if (possibleMoves.size() == 0) {
            nextTurn();
        }
        nextTurn();
    }

    private void nextTurn() {
        refreshTeamsAvailableMoves();
        refreshBoardHighlighting();
        makeCurrentTeamAccessible();
//        if (!isRedsTurn){
//            getAIMove();
//        }
    }

    private void refreshBoardHighlighting() {
        board.resetTileColors();
        highlightAvailableMoves();
        playByPlay();
    }

    public void playByPlay() {
        System.out.println("");
        String player = isRedsTurn ? "red" : "white";
        System.out.println("now " + player + "'s turn");

        for (Node node : redUnits.getChildren()) {
            Unit unit = (Unit) node;
            if (unit.isKing()) {
                System.out.print(unit.getTeam() + " " + unit.getType() + " at ");
                System.out.println(unit.getCurrentX() + ", " + unit.getCurrentY() + " IS A KING");
            }
        }
        for (Node node : whiteUnits.getChildren()) {
            Unit unit = (Unit) node;
            if (unit.isKing()) {
                System.out.print(unit.getTeam() + " " + unit.getType() + " at ");
                System.out.println(unit.getCurrentX() + ", " + unit.getCurrentY() + " IS A KING");
            }
        }

        for (Move move : possibleMoves) {
            if (move.getUnit().isKing()) {
                System.out.print(move.getUnit().getTeam() + " " + move.getUnit().getType() + " " + "KING" + ": ");
            } else {
                System.out.print(move.getUnit().getTeam() + " " + move.getUnit().getType() + ": ");
            }
            System.out.print(move.getUnit().getCurrentX() + ", " + move.getUnit().getCurrentY() + " -> ");
            System.out.print(move.getTarget().x + ", " + move.getTarget().y + ": ");
            System.out.println(move.getResult().getType());
        }
    }

    private boolean isGameOver() {
        if (redUnits.getChildren().size() == 0) {
            System.out.println("!!!WHITE WINS!!!");
            return true;
        }
        if (whiteUnits.getChildren().size() == 0) {
            System.out.println("!!!RED WINS!!!");
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

//    private void resetTileColors() {
//        for (Node node : tiles.getChildren()) {
//            Tile tile = (Tile) node;
//            tile.resetTileColor();
//        }
//    }

//    public Tile getTile(Coordinates position) {
//        return board[position.x][position.y];
//    }

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

//    private void generateBoard() {
//        for (int y = 0; y < SCALE; y++) {
//            for (int x = 0; x < SCALE; x++) {
//                generateTile(y, x);
//            }
//        }
//    }

    public Board getBoard() {
        return board;
    }

//    private void generateTile(int y, int x) {
//        Tile tile = new Tile((x + y) % 2 == 0, x, y);
//        board[x][y] = tile;
//        tiles.getChildren().add(tile);
//    }

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
                if (coordsAreEqual(move.getUnit().getCurrentCoords(), origin) && coordsAreEqual(move
                        .getTarget(), target)) {
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

    public boolean coordsAreEqual(Coordinates pos1, Coordinates pos2) {
        return pos1.x == pos2.x && pos1.y == pos2.y;
    }

    private void doMove(Move move) {
        Coordinates origin = move.getTarget().origin;
        Coordinates target = move.getTarget();
        Unit unit = move.getUnit();
        MoveResult result = move.getResult();

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

                moveUnit(origin, target, unit);

                if (target.isEnemyKingRow(unit.getTeam()) || attackedUnit.isKing()) {
                    unit.crownKing();
                    result.kingIsCreated();
                    System.out.print(unit.getTeam() + " at ");
                    System.out.println(unit.getCurrentX() + ", " + unit.getCurrentY() + " IS NOW A KING");
                }

                killUnit(attackedUnit);

                if (unit.canMove() && canAttack(unit) && !result.isKingCreated()) {
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

    private void moveUnit(Coordinates origin, Coordinates target, Unit unit) {
        unit.move(target);
        board.moveUnit(origin, target, unit);

//        board[origin.x][origin.y].setUnit(null);
//        board[target.x][target.y].setUnit(unit);
    }

    private void killUnit(Unit unit) {
        board.getTile(unit.getCurrentCoords()).setUnit(null);
        if (unit.isRed()) {
            redUnits.getChildren().remove(unit);
        } else {
            whiteUnits.getChildren().remove(unit);
        }
    }

//    public boolean isPlaySquare(Coordinates c) {
//        return (c.x + c.y) % 2 != CheckersGame.PLAY_SQUARE;
//    }

    private void refreshTeamsAvailableMoves() {
        if (isRedsTurn) {
            possibleMoves = getTeamMoves(redUnits);
        } else {
            possibleMoves = getTeamMoves(whiteUnits);
        }
    }

    public ArrayList<Move> getTeamMoves(Group team) {
        ArrayList<Move> possibleTeamMoves = new ArrayList<>();

        for (Node node : team.getChildren()) {
            Unit unit = (Unit) node;
            possibleTeamMoves.addAll(unit.getPossibleMoves());
        }

        return prioritiseAttackMoves(possibleTeamMoves);
    }

    public ArrayList<Move> getUnitMoves(Unit unit) {
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
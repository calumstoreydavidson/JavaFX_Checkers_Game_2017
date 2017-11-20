import static java.lang.System.out;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CheckersGame extends Application {

    public static boolean CROWN_STEALING_ALLOWED = true;
    //toggle which colour squares the game is played on - 1 for white, 0 for black
    static final int PLAY_SQUARE = 1;
    static final int SCALE = 8;
    static final int TILE_SIZE = 100;
    private static final int MAX_RED_POPULATION = Integer.MAX_VALUE;
    private static final int MAX_WHITE_POPULATION = Integer.MAX_VALUE;
    public static boolean DEVELOPMENT_MODE_ENABLED = false;

    private Stage primaryStage;
    private boolean isRedsTurn;
    private Board board;
    private Group redUnits;
    private Group whiteUnits;

    private ArrayList<Move> possibleMoves = new ArrayList<>();
    private Random rand = new Random();

    public static void main(String[] args) {
        launch(args);
    }

    @Override public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        //game window title
        this.primaryStage.setTitle("Checkers Game");

        //prevent all window resizing
        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.UTILITY);

        resetGame();
    }

    private void resetGame() {
        board = new Board();
        redUnits = new Group();
        whiteUnits = new Group();

        Scene gameBoard = new Scene(createGUI());
        this.primaryStage.setScene(gameBoard);
        this.primaryStage.show();
        startNewGame(true); //startNewGame(getUserInput()); TODO because effort
    }

    private Parent createGUI() {
        //game controls
        VBox controls = buildControls();

        //game board
        Pane gameBoard = new Pane();
        int squareEdgeLength = SCALE * TILE_SIZE;
        gameBoard.setMinSize(squareEdgeLength, squareEdgeLength);
        gameBoard.getChildren().addAll(board.getComponents(), redUnits, whiteUnits);

        HBox layout = new HBox(10, controls, gameBoard);
        layout.setPadding(new Insets(10));

        return layout;
    }

    private VBox buildControls() {
        Button newGameButton = new Button("Start New Game");
        newGameButton.setOnAction(value -> {
            resetGame();
        });

        //god mode toggle
        Button developmentModeButton = new Button("Enable Development Mode ");
        developmentModeButton.setOnAction(value -> {
            toggleDevelopmentMode();
            if (DEVELOPMENT_MODE_ENABLED){
                developmentModeButton.setText("Disable Development Mode");
            }else {
                developmentModeButton.setText("Enable Development Mode ");
            }
        });

        return new VBox(10, newGameButton, developmentModeButton);
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
        refreshTurn();
    }

    private void toggleDevelopmentMode() {
        if (DEVELOPMENT_MODE_ENABLED) {
            refreshTurn();
        } else {
            board.resetTileColors();
            redUnits.setMouseTransparent(false);
            whiteUnits.setMouseTransparent(false);
        }
        DEVELOPMENT_MODE_ENABLED = !DEVELOPMENT_MODE_ENABLED;
    }

    private void redsTurn() {
        if (possibleMoves.isEmpty()) {
            refreshTurn();
        }
        refreshTurn();
    }

    private void WhitesTurn() {
        if (possibleMoves.isEmpty()) {
            refreshTurn();
        }
        refreshTurn();
    }

    private void refreshTurn() {
        refreshTeamsAvailableMoves();
        refreshBoardHighlighting();
        makeCurrentTeamAccessible();
    }

    private void refreshBoardHighlighting() {
        board.resetTileColors();
        highlightAvailableMoves();
//        playByPlay();
    }

    public void playByPlay() {
        out.println("");
        String player = isRedsTurn ? "red" : "white";
        out.println("now " + player + "'s turn");

        for (Move move : possibleMoves) {
            if (move.getUnit().isKing()) {
                out.print(move.getUnit().getTeam() + " " + move.getUnit().getType() + ": ");
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
//        executeMove(AIMove);
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

            if (DEVELOPMENT_MODE_ENABLED) {
                if (coordsAreEqual(origin,target)) {
                    unit.toggleKing();
                    moveUnit(origin, target, unit, false);
                }else {
                    moveUnit(origin, target, unit, false);
                }
            } else {
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

                executeMove(actualMove);
            }
        });

        return unit;
    }

    private boolean coordsAreEqual(Coordinates pos1, Coordinates pos2) {
        return pos1.x == pos2.x && pos1.y == pos2.y;
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
                break;
            case NORMAL:
                moveUnit(origin, target, unit, kingIsCreated);
                turnFinished = true;
                break;
            case KILL:
                Unit attackedUnit = result.getAttackedUnit();

                moveUnit(origin, target, unit, kingIsCreated);
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
                refreshTurn();
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

}
import java.util.ArrayList;
import java.util.stream.Collectors;

import javafx.scene.Group;
import javafx.scene.Node;

public class Board {

    private Group components = new Group();
    private Group tiles = new Group();

    private Tile[][] board;
    private Group redUnits = new Group();
    private Group whiteUnits = new Group();

    private ArrayList<Move> possibleMoves = new ArrayList<>();

    public Board() {
        board = new Tile[Game.SCALE][Game.SCALE];

        generateBoard();
        populateBoard();

        components.getChildren().setAll(tiles, redUnits, whiteUnits);
    }

    public static boolean isBoardEdge(Coordinates pos) {
        return pos.x == 0 || pos.y == 0 || pos.x == Game.SCALE - 1 || pos.y == Game.SCALE - 1;
    }

    private void generateBoard() {
        for (int y = 0; y < Game.SCALE; y++) {
            for (int x = 0; x < Game.SCALE; x++) {
                generateTile(y, x);
            }
        }
    }

    private void generateTile(int y, int x) {
        Tile tile = new Tile((x + y) % 2 == 0, x, y);
        board[x][y] = tile;
        tiles.getChildren().add(tile);
    }

    private void populateBoard() {
        populateRed();
        populateWhite();
    }

    private void populateRed() {
        //Red units keep generating down the board till they hit their border
        int factionBorder = Math.round(Game.SCALE / 3f);
        int placedUnits = 0;
        for (int y = 0; y < Game.SCALE; y++) {
            for (int x = 0; x < Game.SCALE; x++) {
                Coordinates position = new Coordinates(x, y);
                if (y < factionBorder && isPlaySquare(position) && placedUnits <= Game.MAX_RED_POPULATION) {
                    Unit unit = new Unit(UnitType.PAWN, Team.RED, position);
                    getTile(position).setUnit(unit);
                    redUnits.getChildren().add(unit);
                    placedUnits++;
                }
            }
        }
    }

    private void populateWhite() {
        //White units keep generating up the board till they hit their border
        int factionBorder = Math.round((Game.SCALE / 3f) * 2);
        int placedUnits = 0;
        for (int y = Game.SCALE - 1; y >= 0; y--) {
            for (int x = Game.SCALE - 1; x >= 0; x--) {
                Coordinates position = new Coordinates(x, y);
                if (y >= factionBorder && isPlaySquare(position) && placedUnits <= Game.MAX_WHITE_POPULATION) {
                    Unit unit = new Unit(UnitType.PAWN, Team.WHITE, position);
                    getTile(position).setUnit(unit);
                    whiteUnits.getChildren().add(unit);
                    placedUnits++;
                }
            }
        }
    }

    public Group getComponents() {
        return components;
    }

    public void resetTileColors() {
        for (Node node : tiles.getChildren()) {
            Tile tile = (Tile) node;
            tile.resetTileColor();
        }
    }

    public void highlightAvailableMoves() {
        for (Move move : possibleMoves) {
            if (move.getResult().getType() == MoveType.KILL) {
                //TODO make these optional
//                board.getTile(move.getTarget()).highlightAttackDestination();
            } else {
//                board.getTile(move.getTarget()).highlightMoveDestination();
            }
            getTile(move.getUnit().getCurrentCoords()).highlightUnit();
        }
    }

    public Tile getTile(Coordinates position) {
        return board[position.x][position.y];
    }

    public boolean isPlaySquare(Coordinates c) {
        return (c.x + c.y) % 2 != Game.PLAY_SQUARE;
    }

    public void getTeamMoves(Team team) {
        ArrayList<Move> possibleTeamMoves = new ArrayList<>();
        Group teamUnits = team == Team.RED ? redUnits : whiteUnits;

        for (Node node : teamUnits.getChildren()) {
            Unit unit = (Unit) node;
            possibleTeamMoves.addAll(getPotentialUnitMoves(unit));
        }

        possibleMoves = prioritiseAttackMoves(possibleTeamMoves);
    }

    public ArrayList<Move> getUnitMoves(Unit unit) {
        return prioritiseAttackMoves(getPotentialUnitMoves(unit));
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

    //returns moves to empty adjacent spaces and spaces beyond adjacent enemies
    public ArrayList<Move> getPotentialUnitMoves(Unit unit) {
        ArrayList<Move> moves = new ArrayList<>();

        for (Coordinates possiblePosition : unit.getPossiblePositions()) {
            if (!isOccupiedTile(possiblePosition)) {
                MoveResult result = new MoveResult(MoveType.NORMAL);
                if (possiblePosition.isEnemyKingRow(unit.getTeam()) && !unit.isKing()) {
                    result.createKing();
                }
                moves.add(new Move(unit, possiblePosition, result));
            } else if (isEnemyUnit(unit, possiblePosition) && isAttackPossible(possiblePosition)) {
                Unit attackedUnit = getTile(possiblePosition).getUnit();
                MoveResult result = new MoveResult(MoveType.KILL, attackedUnit);
                if (possiblePosition.getNextOnPath().isEnemyKingRow(unit.getTeam()) && !unit.isKing() || attackedUnit.isKing() && !unit.isKing() && Game.CROWN_STEALING_ALLOWED) {
                    result.createKing();
                }
                moves.add(new Move(unit, possiblePosition.getNextOnPath(), result));
            }
        }
        return moves;
    }

    private boolean isAttackPossible(Coordinates adjacentTile) {
        return !isEnemyOnEdge(adjacentTile) && !isOccupiedTile(adjacentTile.getNextOnPath());
    }

    public boolean isOccupiedTile(Coordinates c) {
        return getTile(c).hasUnit();
    }

    public boolean isEnemyUnit(Unit unit, Coordinates c) {
        Unit enemyUnit = getTile(c).getUnit();
        return enemyUnit.getTeam() != unit.getTeam();
    }

    public boolean isEnemyOnEdge(Coordinates enemyPos) {
        return Board.isBoardEdge(enemyPos);
    }

    public void killUnit(Unit unit) {
        getTile(unit.getCurrentCoords()).setUnit(null);
        if (unit.isRed()) {
            redUnits.getChildren().remove(unit);
        } else {
            whiteUnits.getChildren().remove(unit);
        }
    }

    public ArrayList<Unit> getPlayerUnits(Team team) {
        Group playerUnits = team == Team.RED ? redUnits : whiteUnits;
        return playerUnits.getChildren().stream().map(node -> (Unit) node).collect(Collectors.toCollection(ArrayList::new));
    }

    public boolean executeMove(Move move) {
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

                if (canMove(unit) && canAttack(unit) && !result.isKingCreated()) {
                    possibleMoves = getUnitMoves(unit);
                } else {
                    turnFinished = true;
                }
                Main.output.appendText(unit.getTeam() + " Attack Successful\n");
                break;
        }
        return turnFinished;
    }

    private boolean canAttack(Unit unit) {
        return getUnitMoves(unit).get(0).getResult().getType() == MoveType.KILL;
    }

    public boolean canMove(Unit unit) {
        return !getUnitMoves(unit).isEmpty();
    }

    public void moveUnit(Coordinates origin, Coordinates target, Unit unit, boolean kingIsCreated) {
        unit.move(target);
        getTile(origin).setUnit(null);
        getTile(target).setUnit(unit);
        Main.output.appendText(unit.getTeam() + " " + target.origin.x + ", " + target.origin.y + " -> " + target.x + ", " + target.y + "\n");

        if (kingIsCreated) {
            unit.toggleKing();
            Main.output.appendText(unit.getCurrentX() + ", " + unit.getCurrentY() + " IS NOW A KING\n");
        }
    }

    public void makeCurrentTeamAccessible(Player player) {
        redUnits.setMouseTransparent(!player.isPlayersTurn());
        whiteUnits.setMouseTransparent(player.isPlayersTurn());
    }

    public Group getRedUnits() {
        return redUnits;
    }

    public Group getWhiteUnits() {
        return whiteUnits;
    }

    public ArrayList<Move> getPossibleMoves() {
        return possibleMoves;
    }
}

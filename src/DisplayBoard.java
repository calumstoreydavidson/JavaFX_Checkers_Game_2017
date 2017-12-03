import java.util.ArrayList;

import javafx.scene.Group;
import javafx.scene.Node;

public class DisplayBoard extends Board {

    private Group GUIComponents = new Group();
    private Group tiles = new Group();

    private Tile[][] board;
    private Group redUnits = new Group();
    private Group whiteUnits = new Group();

    private ArrayList<Move> possibleMoves = new ArrayList<>();

    public DisplayBoard() {
        board = new Tile[Game.SCALE][Game.SCALE];
        setCurrentTeam(Team.RED);

        generateBoard();
        populateBoard();

        GUIComponents.getChildren().setAll(tiles, redUnits, whiteUnits);
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
                if (y < factionBorder && position.isPlaySquare() && placedUnits <= Game.MAX_RED_POPULATION) {
                    spawnUnit(position, redUnits, Team.RED);
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
                if (y >= factionBorder && position.isPlaySquare() && placedUnits <= Game.MAX_WHITE_POPULATION) {
                    spawnUnit(position, whiteUnits, Team.WHITE);
                    placedUnits++;
                }
            }
        }
    }

    private void spawnUnit(Coordinates position, Group teamUnits, Team team) {
        Unit unit = new Unit(UnitType.PAWN, team, position);
        getTile(position).setUnit(unit);
        teamUnits.getChildren().add(unit);
    }

    public Group getGUIComponents() {
        return GUIComponents;
    }

    public void resetTileColors() {
        for (Node node : tiles.getChildren()) {
            Tile tile = (Tile) node;
            tile.resetTileColor();
        }
    }

    public void highlightAvailableMoves() {
        for (Move move : possibleMoves) {
            if (move.getType() == MoveType.KILL) {
                //TODO make these optional
                getTile(move.getTarget()).highlightAttackDestination();
            } else {
                getTile(move.getTarget()).highlightMoveDestination();
            }
            getTile(move.getOrigin()).highlightUnit();
        }
    }

    public Tile getTile(Coordinates position) {
        return board[position.x][position.y];
    }

    public void getTeamMoves(Team team) {
        ArrayList<Move> possibleTeamMoves = new ArrayList<>();
        Group teamUnits = team == Team.RED ? redUnits : whiteUnits;

        for (Node node : teamUnits.getChildren()) {
            Unit unit = (Unit) node;
            possibleTeamMoves.addAll(getUnitsPossibleMoves(unit));
        }

        possibleMoves = prioritiseAttackMoves(possibleTeamMoves);
    }

    public ArrayList<Move> getUnitMoves(Unit unit) {
        return prioritiseAttackMoves(getUnitsPossibleMoves(unit));
    }

    //returns moves to empty adjacent spaces and spaces beyond adjacent enemies
    public ArrayList<Move> getUnitsPossibleMoves(Unit unit) {
        ArrayList<Move> moves = new ArrayList<>();

        for (Coordinates possiblePosition : unit.getPossiblePositions()) {
            if (!isOccupiedTile(possiblePosition)) {
                Move normalMove = new Move(unit.getPos(), possiblePosition, MoveType.NORMAL);
                if (possiblePosition.isEnemyKingRow(unit.getTeam()) && !unit.isKing()) {
                    normalMove.createKing();
                }
                moves.add(normalMove);
            } else if (isEnemyUnit(possiblePosition) && isAttackPossible(possiblePosition)) {
                Unit attackedUnit = getTile(possiblePosition).getUnit();
                Move attackMove = new Move(unit.getPos(), possiblePosition.getNextOnPath(), MoveType.KILL);
                if (possiblePosition.getNextOnPath().isEnemyKingRow(unit.getTeam()) && !unit.isKing() || attackedUnit.isKing() && !unit.isKing() && Game.CROWN_STEALING_ALLOWED) {
                    attackMove.createKing();
                }
                moves.add(attackMove);
            }
        }
        return moves;
    }

    private boolean isAttackPossible(Coordinates adjacentTile) {
        return !isEnemyOnEdge(adjacentTile) && !isOccupiedTile(adjacentTile.getNextOnPath());
    }

    public boolean isOccupiedTile(Coordinates position) {
        return getTile(position).hasUnit();
    }

    public boolean isEnemyUnit(Coordinates position) {
        Unit unit = getTile(position).getUnit();
        return getCurrentTeam() == Team.RED ? unit.isWhite() : unit.isRed();
    }

    public void killUnit(Unit unit) {
        getTile(unit.getPos()).setUnit(null);
        if (unit.isRed()) {
            redUnits.getChildren().remove(unit);
        } else {
            whiteUnits.getChildren().remove(unit);
        }
    }

    public boolean executeMove(Move move) {
        Coordinates origin = move.getTarget().origin;
        Coordinates target = move.getTarget();
        Unit unit = getUnitAtPos(move.getOrigin());
        boolean kingIsCreated = move.isKingCreated();

        boolean turnFinished = false;
        switch (move.getType()) {
            case NONE:
                unit.abortMove();
                Main.output.appendText("That Is An Invalid Move\n");
                break;
            case NORMAL:
                moveUnit(origin, target, unit, kingIsCreated);
                turnFinished = true;
                if (Game.VERBOSE_OUTPUT) {
                    Main.output.appendText(unit.getTeam() + " Move Successful\n");
                }
                break;
            case KILL:
                Unit attackedUnit = getUnitAtPos(Coordinates.getKillCoords(move));

                moveUnit(origin, target, unit, kingIsCreated);
                killUnit(attackedUnit);

                if (canMove(unit) && canAttack(unit) && !move.isKingCreated()) {
                    possibleMoves = getUnitMoves(unit);
                    setMovingUnit(unit);
                } else {
                    turnFinished = true;
                }
                if (Game.VERBOSE_OUTPUT) {
                    Main.output.appendText(unit.getTeam() + " Attack Successful\n");
                }
                break;
        }
        return turnFinished;
    }

    private boolean canAttack(Unit unit) {
        return getUnitMoves(unit).get(0).getType() == MoveType.KILL;
    }

    public boolean canMove(Unit unit) {
        return !getUnitMoves(unit).isEmpty();
    }

    public void moveUnit(Coordinates origin, Coordinates target, Unit unit, boolean kingIsCreated) {
        unit.move(target);
        getTile(origin).setUnit(null);
        getTile(target).setUnit(unit);
        if (Game.VERBOSE_OUTPUT) {
            Main.output.appendText(unit.getTeam() + " " + (target.origin.x + 1) + ", " + (target.origin.y + 1) + " -> " + (target.x + 1) + ", " + (target.y + 1) + "\n");
        }
        if (kingIsCreated) {
            unit.toggleKing();
            if (Game.VERBOSE_OUTPUT) {
                Main.output.appendText(unit.getCurrentX() + ", " + unit.getCurrentY() + " IS NOW A KING\n");
            }
        }
    }

    public void makeCurrentTeamAccessible(Player redPlayer, Player whitePlayer) {
        redUnits.setMouseTransparent(!redPlayer.isPlayersTurn());
        whiteUnits.setMouseTransparent(!whitePlayer.isPlayersTurn());
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

    public Unit getUnitAtPos(Coordinates position){
        return getTile(position).getUnit();
    }
}

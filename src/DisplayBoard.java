import java.util.ArrayList;

import javafx.scene.Group;
import javafx.scene.Node;

/**
 * represents the state of board as should be displayed in the GUI
 */
public class DisplayBoard extends Board {

    // the components to be passed up and displayed by the GUI
    private Group GUIComponents = new Group();

    // the squares of the board too be passed up to and displayed by the GUI
    private Group tiles = new Group();

    // the 2D array of the squares of the game board
    private Tile[][] board;

    // the red units still in play to be passed up to and displayed by the GUI
    private Group redUnits = new Group();

    // the white units still in play to be passed up to and displayed by the GUI
    private Group whiteUnits = new Group();

    // the possible moves that can be made this turn
    private ArrayList<Move> possibleMoves = new ArrayList<>();

    /**
     * creates a new Board populated with the initial state of a new checkers game
     */
    public DisplayBoard() {
        board = new Tile[Game.SCALE][Game.SCALE];
        setCurrentTeam(Team.RED);

        generateBoard();
        populateBoard();

        GUIComponents.getChildren().setAll(tiles, redUnits, whiteUnits);
    }

    /**
     * fill the board array with new tiles
     */
    private void generateBoard() {
        for (int y = 0; y < Game.SCALE; y++) {
            for (int x = 0; x < Game.SCALE; x++) {

                generateTile(new Coordinates(x, y));
            }
        }
    }

    /**
     * create a new tile at a given location
     *
     * @param pos the board position at which to create the new tile
     */
    private void generateTile(Coordinates pos) {
        Tile tile = new Tile((pos.x + pos.y) % 2 == 0, pos.x, pos.y);
        board[pos.x][pos.y] = tile;
        tiles.getChildren().add(tile);
    }

    /**
     * place red and white units on the board
     */
    private void populateBoard() {
        populateRed();
        populateWhite();

    }

    /**
     * working down from the top of the board, while the max number of red units is not met,
     * add red units to play squares above the red border
     */
    private void populateRed() {
        //Red units keep generating down the board till they hit their border
        int factionBorder = Math.round(Game.SCALE / 3f); // determine the row that is the red border
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

    /**
     * working up from the bottom of the board, while the max number of white units is not met,
     * add white units to play squares below the white border
     */
    private void populateWhite() {
        //White units keep generating up the board till they hit their border
        int factionBorder = Math.round((Game.SCALE / 3f) * 2); // determine the row that is the white border
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

    /**
     * create a new unit on the board and add it to its teams unit list and tile on which it is placed
     *
     * @param position  the position at which the new unit should be created
     * @param teamUnits the units list of the created units team
     * @param team      the created units team
     */
    private void spawnUnit(Coordinates position, Group teamUnits, Team team) {
        Unit unit = new Unit(UnitType.PAWN, team, position);
        getTile(position).setUnit(unit);
        teamUnits.getChildren().add(unit);
    }

    /**
     * get the components to be passed up to and displayed by the GUI
     *
     * @return the components to be passed up to and displayed by the GUI
     */
    public Group getGUIComponents() {
        return GUIComponents;
    }

    /**
     * remove all highlight coloring and set all tiles back to there default board color
     */
    public void resetTileColors() {
        for (Node node : tiles.getChildren()) {
            Tile tile = (Tile) node;
            tile.resetTileColor();
        }
    }

    /**
     * for each of the current players possible moves, highlight tiles at origin coordinates green,
     * tiles at target coordinates orange, and tiles at attack move target coordinates orange
     */
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

    /**
     * get the tile at the specified coordinates
     *
     * @param position the coordinates from which to get the board tile
     * @return the tile at the specified coordinates
     */
    public Tile getTile(Coordinates position) {
        return board[position.x][position.y];
    }

    /**
     * refresh the possible moves available for all units in play for the current player
     *
     * @param team the team of the current player
     */
    public void refreshTeamsAvailableMoves(Team team) {
        ArrayList<Move> possibleTeamMoves = new ArrayList<>();
        Group teamUnits = team == Team.RED ? redUnits : whiteUnits;

        for (Node node : teamUnits.getChildren()) {
            Unit unit = (Unit) node;
            possibleTeamMoves.addAll(getUnitsPossibleMoves(unit));
        }

        possibleMoves = prioritiseAttackMoves(possibleTeamMoves);
    }

    /**
     * get the attack move prioritised moves of a given unit
     *
     * @param unit the unit for which to get all attack move prioritised available moves
     * @return the units attack prioritised available moves
     */
    public ArrayList<Move> getUnitMoves(Unit unit) {
        return prioritiseAttackMoves(getUnitsPossibleMoves(unit));
    }

    /**
     * returns all valid moves to empty adjacent spaces and empty spaces behind adjacent enemies
     * (a space is only adjacent if the unit can move in that direction)
     *
     * @param unit the unit the get all the possible moves for
     * @return all the possible moves of the unit
     */
    public ArrayList<Move> getUnitsPossibleMoves(Unit unit) {
        ArrayList<Move> moves = new ArrayList<>();

        for (Coordinates adjacentPositions : unit.getAdjacentPositions()) {
            if (!isOccupiedTile(adjacentPositions)) {
                Move normalMove = new Move(unit.getPos(), adjacentPositions, MoveType.NORMAL);
                if (adjacentPositions.isEnemyKingRow(unit.getTeam()) && !unit.isKing()) {
                    normalMove.createKing();
                }
                moves.add(normalMove);
            } else if (isAttackPossible(adjacentPositions)) {
                Unit attackedUnit = getTile(adjacentPositions).getUnit();
                Move attackMove = new Move(unit.getPos(), adjacentPositions.getNextOnPath(), MoveType.KILL);
                if (adjacentPositions.getNextOnPath().isEnemyKingRow(unit.getTeam()) && !unit.isKing() || attackedUnit.isKing() && !unit.isKing() && Game.CROWN_STEALING_ALLOWED) {
                    attackMove.createKing();
                }
                moves.add(attackMove);
            }
        }
        return moves;
    }

    /**
     * checks if the given Coordinate can be the subject of a jump/attack,
     * e.g. is there an enemy and is there somewhere to jump to
     *
     * @param adjacentTile the Coordinate to evaluate for attack viability
     * @return whether the board position can be attacked or not
     */
    private boolean isAttackPossible(Coordinates adjacentTile) {
        return isEnemyUnit(adjacentTile) && !isEnemyOnEdge(adjacentTile) && !isOccupiedTile(adjacentTile.getNextOnPath());
    }

    /**
     * evaluate if there is a unit at the specified position
     *
     * @param position the position to check for a unit
     * @return whether there is a unit at the specified position
     */
    public boolean isOccupiedTile(Coordinates position) {
        return getTile(position).hasUnit();
    }

    /**
     * evaluate whether the unit at the specified position is an enemy of the current player
     *
     * @param position the position to check for an enemy unit
     * @return whether the unit at the specified position is an enemy of the current player
     */
    public boolean isEnemyUnit(Coordinates position) {
        Unit unit = getTile(position).getUnit();
        return getCurrentTeam() == Team.RED ? unit.isWhite() : unit.isRed();
    }

    /**
     * remove the specified unit from play by removing it from its tile, and the list of its teams units
     *
     * @param unit the unit to be removed from play
     */
    public void killUnit(Unit unit) {
        getTile(unit.getPos()).setUnit(null);
        if (unit.isRed()) {
            redUnits.getChildren().remove(unit);
        } else {
            whiteUnits.getChildren().remove(unit);
        }
    }

    /**
     * run / execute the specified move on the game if it is a valid move and finish the turn if this move did not start
     * a multijump
     *
     * @param move the move to run / execute
     * @return whether or not this turn is finished
     */
    public boolean executeMove(Move move) {
        Coordinates origin = move.getTarget().origin;
        Coordinates target = move.getTarget();
        Unit unit = getUnitAtPos(move.getOrigin());
        boolean kingIsCreated = move.isKingCreated();

        boolean turnFinished = false;
        switch (move.getType()) {
            case NONE:
                unit.abortMove();
                GUI.output.appendText("That Is An Invalid Move\n");
                break;
            case NORMAL:
                moveUnit(origin, target, unit, kingIsCreated);
                turnFinished = true;
                if (Game.VERBOSE_OUTPUT) {
                    GUI.output.appendText(unit.getTeam() + " Move Successful\n");
                }
                break;
            case KILL:
                Unit attackedUnit = getUnitAtPos(Coordinates.getKillCoords(move));

                moveUnit(origin, target, unit, kingIsCreated);
                killUnit(attackedUnit);

                if (canMove(unit) && canAttack(unit) && !move.isKingCreated()) {
                    possibleMoves = getUnitMoves(unit);
                    setUnitInMotion(unit);
                } else {
                    turnFinished = true;
                }
                if (Game.VERBOSE_OUTPUT) {
                    GUI.output.appendText(unit.getTeam() + " Attack Successful\n");
                }
                break;
        }
        return turnFinished;
    }

    /**
     * TODO see if I can refactor this away / more efficient - fixed by implementing Move refactor
     * check if the given unit has any attack moves available
     *
     * @param unit the unit to check for possible attack moves
     * @return whether the unit can make any attack moves
     */
    private boolean canAttack(Unit unit) {
        return getUnitMoves(unit).get(0).getType() == MoveType.KILL; //moves are either all attacks or none are attacks
    }

    /**
     * checks to see if the given unit can make any mve at all
     *
     * @param unit the unit to check for possible moves
     * @return whether the unit has any available moves
     */
    public boolean canMove(Unit unit) {
        return !getUnitMoves(unit).isEmpty();
    }

    /**
     * move the specified unit from the specified origin position to the specified target position and trigger king
     * creation as necessary
     *
     * @param origin        the location at which the unit begins
     * @param target        the location the unit must move to
     * @param unit          the unit to move
     * @param kingIsCreated whether this move action involves king creation
     */
    public void moveUnit(Coordinates origin, Coordinates target, Unit unit, boolean kingIsCreated) {
        unit.move(target);
        getTile(origin).setUnit(null);
        getTile(target).setUnit(unit);
        if (Game.VERBOSE_OUTPUT) {
            GUI.output.appendText((target.origin.x + 1) + ", " + (target.origin.y + 1) + " -> " + (target.x + 1) + ", " + (target.y + 1) + "\n");
        }
        if (kingIsCreated) {
            unit.toggleKing();
            if (Game.VERBOSE_OUTPUT) {
                GUI.output.appendText(unit.getCurrentX() + ", " + unit.getCurrentY() + " IS NOW A KING\n");
            }
        }
    }

    /**
     * ensure that only the units of the current player react to mouse actions
     *
     * @param redPlayer   the current red player
     * @param whitePlayer the current white player
     */
    public void makeCurrentTeamAccessible(Player redPlayer, Player whitePlayer) {
        redUnits.setMouseTransparent(!redPlayer.isPlayersTurn());
        whiteUnits.setMouseTransparent(!whitePlayer.isPlayersTurn());
    }

    /**
     * get all the units in the red teams list of units
     *
     * @return all the units in the red teams list of units
     */
    public Group getRedUnits() {
        return redUnits;
    }

    /**
     * get all the units in the white teams list of units
     *
     * @return all the units in the white teams list of units
     */
    public Group getWhiteUnits() {
        return whiteUnits;
    }

    /**
     * get the list of possible moves the current player can make this turn
     *
     * @return the list of possible moves the current player can make this turn
     */
    public ArrayList<Move> getPossibleMoves() {
        return possibleMoves;
    }

    /**
     * get the unit at the specified position
     *
     * @param position the position the retrieve the unit from
     * @return the unit at the specified position
     */
    public Unit getUnitAtPos(Coordinates position) {
        return getTile(position).getUnit();
    }
}

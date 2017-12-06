import java.util.ArrayList;
import java.util.Random;

/**
 * represents a simulation of a stripped down version of the game board,
 * for use in the tree structure constructed by some algorithms,
 * in this case negamax and negamax with alpha beta pruning
 */
public class SimulationBoard extends Board {

    //a randomisation tool
    private final Random rand = new Random();

    //the grid of Types that represent the simulated board
    private Type[][] board = new Type[Game.BOARD_SIZE][Game.BOARD_SIZE];//TODO looking into bit board systems to increase efficiency could be interesting

    //all the units on the red team
    private ArrayList<Coordinates> redUnits = new ArrayList<>();

    //all the units on the white team
    private ArrayList<Coordinates> whiteUnits = new ArrayList<>();

    /**
     * create a new board state simulation from a real game state held in DisplayBoard
     *
     * @param oldDisplayBoard the real game state the simulation will start from
     * @param team            the team who's turn it is in the real game state
     */
    public SimulationBoard(DisplayBoard oldDisplayBoard, Team team) {
        generateSimBoardFromRealBoard(oldDisplayBoard);
        setCurrentTeam(team);
        setUnitInMotion(oldDisplayBoard.getUnitInMotion());
    }

    /**
     * create a new board state simulation from another simulated board state
     *
     * @param oldSimBoard the previous simulated game state
     */
    private SimulationBoard(SimulationBoard oldSimBoard) {
        for (int i = 0; i < Game.BOARD_SIZE; i++) {
            for (int j = 0; j < Game.BOARD_SIZE; j++) {
                board[i][j] = oldSimBoard.getBoard()[i][j];
            }
        }
        redUnits = new ArrayList<>(oldSimBoard.getRedUnits());
        whiteUnits = new ArrayList<>(oldSimBoard.getWhiteUnits());
        setCurrentTeam(oldSimBoard.getCurrentTeam());
    }

    /**
     * populate the board grid by duplicating the board state from the grid of the real games DisplayBoard
     *
     * @param oldDisplayBoard the real games state representation to be duplicated
     */
    private void generateSimBoardFromRealBoard(DisplayBoard oldDisplayBoard) {
        board = new Type[Game.BOARD_SIZE][Game.BOARD_SIZE];
        for (int x = 0; x < Game.BOARD_SIZE; x++) {
            for (int y = 0; y < Game.BOARD_SIZE; y++) {
                Tile tile = oldDisplayBoard.getTile(new Coordinates(x, y));
                generateSimTile(tile, x, y);
            }
        }
    }

    /**
     * duplicate and translate a given position on the real games board onto the simulated board
     *
     * @param tile a tile from the real games state representation to be duplicated
     * @param x    the x component of the position of the given tile to be duplicated
     * @param y    the y component of the position of the given tile to be duplicated
     */
    private void generateSimTile(Tile tile, int x, int y) {
        if (tile.getUnit() == null) {
            board[x][y] = Type.EMPTY;
        } else {
            Unit unit = tile.getUnit();
            if (unit.isRed()) {
                redUnits.add(new Coordinates(x, y));
                if (unit.isKing()) {
                    board[x][y] = Type.RED_KING;
                } else {
                    board[x][y] = Type.RED;
                }
            } else {
                whiteUnits.add(new Coordinates(x, y));
                if (unit.isKing()) {
                    board[x][y] = Type.WHITE_KING;
                } else {
                    board[x][y] = Type.WHITE;
                }
            }
        }
    }

    /**
     * get the possible moves available for all units in play for the current player
     *
     * @return the possible moves available for all units in play for the current player
     */
    public ArrayList<Move> getTeamsPossibleMoves() {
        ArrayList<Move> possibleTeamMoves = new ArrayList<>();
        ArrayList<Coordinates> teamUnits = getCurrentTeam() == Team.RED ? redUnits : whiteUnits;

        if (getUnitInMotion() != null) {
            possibleTeamMoves.addAll(getUnitsPossibleMoves(getUnitInMotion().getPos()));
        } else {
            for (Coordinates position : teamUnits) {
                possibleTeamMoves.addAll(getUnitsPossibleMoves(position));
            }
        }
        return prioritiseAttackMoves(possibleTeamMoves);
    }

    /**
     * returns the possible moves available for the given unit for the current player
     *
     * @param position the position of the unit to get the possible moves of
     * @return all the possible moves of the given unit
     */
    private ArrayList<Move> getUnitsPossibleMoves(Coordinates position) { //TODO get this and all subordinate code generic enough to go in the Board class
        ArrayList<Move> moves = new ArrayList<>();

        for (Coordinates possiblePosition : getUnitsPossiblePositions(position)) {
            if (isUnoccupiedTile(possiblePosition)) {
                Move normalMove = new Move(position, possiblePosition, MoveType.NORMAL);
                if (possiblePosition.isEnemyKingRow(getCurrentTeam()) && !isKing(position)) {
                    normalMove.createKing();
                }
                moves.add(normalMove);
            } else if (isEnemyUnit(possiblePosition) && isAttackPossible(possiblePosition)) {
                Move attackMove = new Move(position, possiblePosition.getNextOnPath(), MoveType.KILL);
                if (possiblePosition.getNextOnPath().isEnemyKingRow(getCurrentTeam()) && !isKing(position) || isKing(possiblePosition) && !isKing(position) && Game.CROWN_STEALING_ALLOWED) {
                    attackMove.createKing();
                }
                moves.add(attackMove);
            }
        }
        return prioritiseAttackMoves(moves);
    }

    /**
     * get the adjacent positions to this units position,
     * where an adjacent position is a connected square in a valid direction of travel
     *
     * @param origin the position from which to get adjacent positions
     * @return the list of adjacent positions to this unit
     */
    private ArrayList<Coordinates> getUnitsPossiblePositions(Coordinates origin) {
        ArrayList<Coordinates> potentiallyAdjacentTiles = new ArrayList<>();

        if (isKing(origin) || getCurrentTeam() == Team.RED) {
            potentiallyAdjacentTiles.add(origin.SW());
            potentiallyAdjacentTiles.add(origin.SE());
        }
        if (isKing(origin) || getCurrentTeam() == Team.WHITE) {
            potentiallyAdjacentTiles.add(origin.NW());
            potentiallyAdjacentTiles.add(origin.NE());
        }

        ArrayList<Coordinates> validAdjacentTiles = new ArrayList<>();
        for (Coordinates position : potentiallyAdjacentTiles) {
            if (!position.isOutsideBoard()) {
                validAdjacentTiles.add(position);
            }
        }

        return validAdjacentTiles;
    }

    /**
     * get the Type at the specified position
     *
     * @param position the position for which to get the type
     * @return the Type at the given position
     */
    private Type getTile(Coordinates position) {
        return board[position.x][position.y];
    }

    /**
     * set the specified position to the given Type
     *
     * @param position the position at which to set the type
     * @param type     the type to assign to the given position
     */
    private void setTile(Coordinates position, Type type) {
        board[position.x][position.y] = type;
    }

    /**
     * evaluate whether the unit at the specified position is an enemy of the current player
     *
     * @param position the position to check for an enemy unit
     * @return whether the unit at the specified position is an enemy of the current
     */
    private boolean isEnemyUnit(Coordinates position) {
        if (getCurrentTeam() == Team.RED) {
            return getTile(position) == Type.WHITE || getTile(position) == Type.WHITE_KING;
        } else {
            return getTile(position) == Type.RED || getTile(position) == Type.RED_KING;
        }
    }

    /**
     * checks if the given Coordinate can be the subject of a jump/attack,
     * e.g. is there an enemy and is there somewhere to jump to
     *
     * @param adjacentTile the Coordinate to evaluate for attack viability
     * @return whether the board position can be attacked or not
     */
    private boolean isAttackPossible(Coordinates adjacentTile) {
        return !isEnemyOnEdge(adjacentTile) && isUnoccupiedTile(adjacentTile.getNextOnPath());
    }

    /**
     * evaluate if there is a unit at the specified position
     *
     * @param position the position to check for a unit
     * @return whether there is a unit at the specified position
     */
    private boolean isUnoccupiedTile(Coordinates position) {
        return getTile(position) == Type.EMPTY;
    }

    /**
     * check if the unit at the given position is a king
     *
     * @param position the position to check for a king Type
     * @return whether the given position holds a king
     */
    private boolean isKing(Coordinates position) {
        Type type = getTile(position);
        return type == Type.WHITE_KING || type == Type.RED_KING;
    }

    /**
     * check if the given unit has any attack moves available
     *
     * @param position the position of the unit to get the possible moves from
     * @return whether the unit can make any attack moves
     */
    private boolean canAttack(Coordinates position) {
        ArrayList<Move> possibleMoves = getUnitsPossibleMoves(position);
        return !possibleMoves.isEmpty() && possibleMoves.get(0).getType() == MoveType.KILL;
    }

    /**
     * run / execute the specified move on the game
     * and if this move did start a multijump then recursively select random moves and execute them
     *
     * @param move the move to execute on the simulated game state
     */
    private void executeMove(Move move) {
        moveUnit(move);

        if (move.getType() == MoveType.KILL) {
            killUnit(Coordinates.getKillCoords(move));

            if (canAttack(move.getTarget()) && !move.isKingCreated()) {
                executeMove(getRandomMove(getUnitsPossibleMoves(move.getTarget())));
            }
        }
    }

    /**
     * move the specified unit from the specified origin position to the specified target position and trigger king
     * creation as necessary
     *
     * @param move the move to execute on the simulated game state
     */
    private void moveUnit(Move move) {
        if (getTile(move.getOrigin()) == Type.RED || getTile(move.getOrigin()) == Type.RED_KING) {
            redUnits.remove(move.getOrigin());
            redUnits.add(move.getTarget());
        } else {
            whiteUnits.remove(move.getOrigin());
            whiteUnits.add(move.getTarget());
        }

        setTile(move.getTarget(), getTile(move.getOrigin()));
        setTile(move.getOrigin(), Type.EMPTY);

        if (move.isKingCreated()) {
            crownKing(move.getTarget());
        }
    }

    /**
     * remove the Type at the specified position setting that slot to empty,
     * and by removing it from its teams unit list
     *
     * @param position the position at which a unit must be killed / removed from the game
     */
    private void killUnit(Coordinates position) {
        if (getTile(position) == Type.RED || getTile(position) == Type.RED_KING) {
            redUnits.remove(position);
        } else {
            whiteUnits.remove(position);
        }
        setTile(position, Type.EMPTY);
    }

    /**
     * convert a pawn to a king
     *
     * @param position the position at which to convert the rpe existing pawn to king
     */
    private void crownKing(Coordinates position) {
        Type type = getTile(position) == Type.RED ? Type.RED_KING : Type.WHITE_KING;
        setTile(position, type);
    }

    /**
     * randomly select a move to execute from the list of possible moves
     *
     * @param moves the list of possible moves to select from
     * @return the move that has been randomly selected
     */
    private Move getRandomMove(ArrayList<Move> moves) {
        int r = rand.nextInt(moves.size());
        return moves.get(r);
    }

    /**
     * get the simulations board state
     *
     * @return the simulations board state
     */
    private Type[][] getBoard() {
        return board;
    }

    /**
     * get all the red teams units
     *
     * @return all the red teams units
     */
    private ArrayList<Coordinates> getRedUnits() {
        return redUnits;
    }

    /**
     * get all the white teams units
     *
     * @return all the white teams units
     */
    private ArrayList<Coordinates> getWhiteUnits() {
        return whiteUnits;
    }

    /**
     * evaluate the score value of this board configuration / state - for use in such algorithms as Minimax
     *
     * @return the score value of the current board state
     */
    public double evaluateState() { //TODO extend this based on further research, tried giving it an affinity for edges but seemed detrimental, tried a random uncertainty multiplier on returned scores but seemed to lag the game
//        int uncertainty = rand.nextInt(potentialDivergence) + 1;
        //heuristic - absolute piece count - kings worth double
        double reds = 0;
        double whites = 0;

        for (Coordinates pos : redUnits) {
            Type type = getTile(pos);

            if (type == Type.RED) {
                reds++;
            } else if (type == Type.RED_KING) {//kings are better than pawns, get and protect them
                reds++;
                reds++;
            }
        }

        for (Coordinates pos : whiteUnits) {
            Type type = getTile(pos);
            if (type == Type.WHITE) {
                whites++;
            } else if (type == Type.WHITE_KING) {
                whites++;
                whites++;
            }
        }

        if (getCurrentTeam() == Team.WHITE) {
            return whites - reds;
        } else {
            return reds - whites;
        }
    }

    /**
     * generate a new board state simulation from this one by applying a given move action
     *
     * @param move the move action to apply to the newly duplicated board state simulation
     * @return the newly duplicated board state simulation with the move action applied
     */
    public SimulationBoard getChild(Move move) {
        SimulationBoard child = new SimulationBoard(this);
        child.executeMove(move);
        child.setNextPlayer();
        return child;
    }

    /**
     * the fundamental types of the spaces/units on the simulated board, each square is either empty, or a unit type
     */
    private enum Type {
        EMPTY, WHITE, RED, WHITE_KING, RED_KING
    }
}

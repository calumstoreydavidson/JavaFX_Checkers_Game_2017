import java.util.ArrayList;

/**
 * represents basic state and logic that can be shared between DisplayBoard and SimulationBoard
 */
public abstract class Board {

    // stores the team whose turn it currently is
    private Team currentTeam;

    // stores Units that have just made an attack and must attack again - multijumps - primarily needed to ensure proper
    // transition of state when converting the real game state to the simulated game state.
    private Unit unitInMotion;

    /**
     * if a Unit can make an attack move, it must do so, get the attack moves if they exist otherwise get the normal
     * moves
     *
     * @param possibleMoves all the possible moves that can be made by the player
     * @return only attack moves if any exist in possibleMoves, otherwise return all possibleMoves
     */
    public ArrayList<Move> prioritiseAttackMoves(ArrayList<Move> possibleMoves) {
        ArrayList<Move> attackMoves = new ArrayList<>();
        for (Move move : possibleMoves) {
            if (move.getType() == MoveType.KILL) {
                attackMoves.add(move);
            }
        }

        if (!attackMoves.isEmpty()) {
            return attackMoves;
        } else {
            return possibleMoves;
        }
    }

    /**
     * check if a unit is on the edge of the board and therefore immune to attack
     *
     * @param unitPosition the board position of the unit to be checked
     * @return whether or not the unit in question is on the edge of the game board and subsequently immune to attack
     */
    public boolean isEnemyOnEdge(Coordinates unitPosition) {
        return Coordinates.isBoardEdge(unitPosition);
    }

    /**
     * set the current player to be the next team / the opposite team to be the current team
     */
    public void setNextPlayer() {
        currentTeam = currentTeam == Team.RED ? Team.WHITE : Team.RED;
    }

    /**
     * get the team whose turn it currently is
     *
     * @return the team whose turn it currently is
     */
    public Team getCurrentTeam() {
        return currentTeam;
    }

    /**
     * set the team whose turn it currently is
     *
     * @param team the team whose turn it currently is
     */
    public void setCurrentTeam(Team team) {
        this.currentTeam = team;
    }

    /**
     * get the unit that is currently in the middle of a multijump
     *
     * @return the unit that is the in the middle of the multijump
     */
    public Unit getUnitInMotion() {
        return unitInMotion;
    }

    /**
     * set the unit that is currently in the middle of a multijump
     *
     * @param unitInMotion the unit that is currently mid multi jump
     */
    public void setUnitInMotion(Unit unitInMotion) {
        this.unitInMotion = unitInMotion;
    }
}

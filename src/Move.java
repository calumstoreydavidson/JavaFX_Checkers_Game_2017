/**
 * represents the action of moving a unit across the board and its result
 */
public class Move {

    // the coordinates from which the move starts
    private final Coordinates origin;

    // the coordinates where the move ends
    private final Coordinates target;

    // the type of the move, e.g. normal, attack, invalid
    private MoveType type;

    //whether or not this move action will create a king with the moving unit
    private boolean kingCreated;

    //an explanation of why the move failed - if it was invalid
    private String invalidMoveExplanation = null;

    /**
     * create a move action with an origin, a destination and a type
     *
     * @param origin the coordinates from which the move starts
     * @param target the coordinates where the move ends
     * @param type   the type of move, e.g. normal, attack, invalid
     */
    public Move(Coordinates origin, Coordinates target, MoveType type) {
        this.origin = origin;
        this.target = target;
        this.type = type;
    }

    /**
     * get the moves type, e.g. normal, attack, invalid
     *
     * @return the moves type, e.g. normal, attack, invalid
     */
    public MoveType getType() {
        return type;
    }

    /**
     * check whether or not this move action will create a king with the moving unit
     *
     * @return whether or not this move action will create a king with the moving unit
     */
    public boolean isKingCreated() {
        return kingCreated;
    }

    /**
     * set that this move action will cause the moving unit to become a king
     */
    public void createKing() {
        kingCreated = true;
    }

    /**
     * get the starting point of the move
     *
     * @return the starting point of the move
     */
    public Coordinates getOrigin() {
        return origin;
    }

    /**
     * get the destination of the move
     *
     * @return the destination of the move
     */
    public Coordinates getTarget() {
        return target;
    }

    /**
     * get the invalid move explanation
     *
     * @return the invalid move explanation
     */
    public String getInvalidMoveExplanation() {
        return invalidMoveExplanation;
    }

    /**
     * set the invalid move explanation
     *
     * @param invalidMoveExplanation why the move was considered invalid
     */
    public void setInvalidMoveExplanation(String invalidMoveExplanation) {
        this.invalidMoveExplanation = invalidMoveExplanation;
    }
}

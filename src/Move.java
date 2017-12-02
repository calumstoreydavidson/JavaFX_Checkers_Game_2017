public class Move {

    private final Coordinates origin;
    private final Coordinates target;
    //TODO this could hold another move if it were a kill move, and so on until no more kill moves available
    private MoveType type;
    private boolean kingCreated;

    public Move(Coordinates origin, Coordinates target, MoveType type) {
        this.origin = origin;
        this.target = target;
        this.type = type;
    }

    public MoveType getType() {
        return type;
    }

    public boolean isKingCreated() {
        return kingCreated;
    }

    public void createKing() {
        kingCreated = true;
    }

    public Coordinates getOrigin() {
        return origin;
    }

    public Coordinates getTarget() {
        return target;
    }

}

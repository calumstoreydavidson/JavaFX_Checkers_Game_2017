public class Move {

    private final Coordinates origin;
    private final Coordinates target;
    private final MoveResult result;//TODO this could hold another move if it were a kill move, and so on until no more kill moves available

    public Move(Coordinates origin, Coordinates target, MoveResult result) {
        this.origin = origin;
        this.target = target;
        this.result = result;
    }

    public Coordinates getOrigin() {
        return origin;
    }

    public Coordinates getTarget() {
        return target;
    }

    public MoveResult getResult() {
        return result;
    }
}

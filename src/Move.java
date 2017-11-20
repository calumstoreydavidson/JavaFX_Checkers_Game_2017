public class Move {

    private final Unit unit;
    private final Coordinates target;
    private final MoveResult result;

    public Move(Unit unit, Coordinates target, MoveResult result) {
        this.unit = unit;
        this.target = target;
        this.result = result;
    }

    public Unit getUnit() {
        return unit;
    }

    public Coordinates getTarget() {
        return target;
    }

    public MoveResult getResult() {
        return result;
    }

}

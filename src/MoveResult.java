

public class MoveResult {

    private MoveType type;
    private Unit attackedUnit;
    private boolean kingCreated;

    public MoveResult(MoveType type) {
        this(type, null);
    }

    public MoveResult(MoveType type, Unit attackedUnit) {
        this.type = type;
        this.attackedUnit = attackedUnit;
    }

    public MoveType getType() {
        return type;
    }

    public Unit getAttackedUnit() {
        return attackedUnit;
    }

    public boolean isKingCreated(){
        return kingCreated;
    }

    public void kingIsCreated(){
        kingCreated = true;
    }
}
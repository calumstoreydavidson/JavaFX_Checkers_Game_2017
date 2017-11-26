public class MoveResult {

    private MoveType type;
    private boolean kingCreated;

    public MoveResult(MoveType type) {
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
}
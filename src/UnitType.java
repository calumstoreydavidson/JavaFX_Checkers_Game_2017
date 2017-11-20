public enum UnitType {
    PAWN(1), KING(2);

    final int layers;

    UnitType(int layers) {
        this.layers = layers;
    }
}
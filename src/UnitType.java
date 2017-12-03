/**
 * represents the possible types of unit
 */
public enum UnitType {
    PAWN(1), KING(2);

    // the number of visible layers on the displayed unit
    final int layers;

    /**
     * creates the enum states with the layers value
     *
     * @param layers the layers a UnitType should have
     */
    UnitType(int layers) {
        this.layers = layers;
    }
}
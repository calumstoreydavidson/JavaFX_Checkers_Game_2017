import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Tile extends Rectangle {

    private Unit unit;
    private boolean light;

    public Tile(boolean light, int x, int y) {
        this.light = light;
        setWidth(Game.TILE_SIZE);
        setHeight(Game.TILE_SIZE);

        relocate(x * Game.TILE_SIZE, y * Game.TILE_SIZE);

        resetTileColor();
    }

    public void resetTileColor() {
        setFill(light ? Color.valueOf("white") : Color.valueOf("black"));
    }

    public boolean hasUnit() {
        return unit != null;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public void highlightAttackDestination() {
        setFill(Color.valueOf("red"));
    }

    public void highlightMoveDestination() {
        setFill(Color.valueOf("blue"));
    }

    public void highlightUnit() {
        setFill(Color.valueOf("green"));
    }

    public void highlightAIMove() {
        setFill(Color.valueOf("orange"));
    }
}
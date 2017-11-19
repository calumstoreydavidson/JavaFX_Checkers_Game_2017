

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Tile extends Rectangle {

    private Unit unit;
    private boolean light;

    public Tile(boolean light, int x, int y) {
        this.light = light;
        setWidth(CheckersGame.TILE_SIZE);
        setHeight(CheckersGame.TILE_SIZE);

        relocate(x * CheckersGame.TILE_SIZE, y * CheckersGame.TILE_SIZE);

        resetTileColor();
    }

    public void resetTileColor(){
//        setFill(light ? Color.valueOf("#feb") : Color.valueOf("#582"));
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

    public void highlightAttackDestination(){
        setFill(Color.valueOf("red"));
    }

    public void highlightMoveDestination(){
        setFill(Color.valueOf("blue"));
    }

    public void highlightUnit(){
        setFill(Color.valueOf("green"));
    }
}
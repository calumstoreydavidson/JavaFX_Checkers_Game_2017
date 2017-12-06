import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * represents a square on the game board
 */
public class Tile extends Rectangle {

    // the unit on this tile
    private Unit unit;

    //whether the tile is black or white
    private final boolean light;

    /**
     * create a tile that is light or dark and has a board position
     *
     * @param position the position this tile will be located at on the game board
     */
    public Tile(Coordinates position) {
        setWidth(Game.TILE_SIZE);
        setHeight(Game.TILE_SIZE);

        light = (position.x + position.y) % 2 == 0;
        resetTileColor();

        relocate(position.x * Game.TILE_SIZE, position.y * Game.TILE_SIZE);
    }

    /**
     * reset the color of the tile as seen by the user in the GUI to its original pre highlighted color
     */
    public void resetTileColor() {
        setFill(light ? Color.valueOf("white") : Color.valueOf("black"));
    }

    /**
     * check whether the tile has a unit
     *
     * @return whether the tile has a unit
     */
    public boolean hasUnit() {
        return unit != null;
    }

    /**
     * get the unit on this tile
     *
     * @return the unit on this tile
     */
    public Unit getUnit() {
        return unit;
    }

    /**
     * set the unit on this tile
     *
     * @param unit the unit on this tile
     */
    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    /**
     * recolor this tile to highlight it to the user as the destination of an attack move
     */
    public void highlightAttackDestination() {
        setFill(Color.valueOf("red"));
    }

    /**
     * recolor this tile to highlight it to the user as the destination of a normal move
     */
    public void highlightMoveDestination() {
        setFill(Color.valueOf("blue"));
    }

    /**
     * recolor this tile to highlight it to the user as the location of a unit that can move this turn
     */
    public void highlightUnit() {
        setFill(Color.valueOf("green"));
    }

    /**
     * recolor this tile to highlight it to the user as the origin or destination of an AI's move (both are implemented)
     */
    public void highlightAIMove() {
        setFill(Color.valueOf("orange"));
    }

    /**
     * recolor this tile to highlight it to the user as the origin or destination of the AI advisors suggested move (both are implemented)
     */
    public void highlightAdvisorSuggestedMove() {
        setFill(Color.valueOf("gold"));
    }
}
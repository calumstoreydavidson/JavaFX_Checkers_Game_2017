import java.util.Objects;

/**
 * represents the coordinate position of a location on the board and holds useful logic for manipulating position
 * coordinate math
 */
public class Coordinates {

    // the x component of the coordinate / the column
    public final int x;

    // the y component of the coordinate / the row
    public final int y;

    // the parent coordinate that generated this coordinate
    public final Coordinates origin;

    /**
     * creates a Coordinate with specified x and y values and no parent Coordinate
     *
     * @param x the x component of the coordinate / the column
     * @param y the y component of the coordinate / the row
     */
    public Coordinates(int x, int y) {
        this(null, x, y);
    }

    /**
     * creates a Coordinate with specified x and y values and a parent Coordinate
     *
     * @param origin the parent Coordinate of this new Coordinate
     * @param x      the x component of the coordinate / the column
     * @param y      the y component of the coordinate / the row
     */
    public Coordinates(Coordinates origin, int x, int y) {
        this.x = x;
        this.y = y;
        this.origin = origin;
    }

    /**
     * convert a board scale value e.g. 0 - 8 into a pixel scale value e.g. 0 - 800 for getting actual screen positions
     *
     * @param pixel an x or y value at board scale
     * @return an x or y value converted to pixel scale
     */
    public static int toBoard(double pixel) {
        return (int) (pixel + Game.TILE_SIZE / 2) / Game.TILE_SIZE;
    }

    /**
     * check if coordinates are on the edge of the game board
     *
     * @param pos the position to be checked against the board size
     * @return whether or not the provided position is on the edge of the current board
     */
    public static boolean isBoardEdge(Coordinates pos) {
        return pos.x == 0 || pos.y == 0 || pos.x == Game.SCALE - 1 || pos.y == Game.SCALE - 1;
    }

    /**
     * get the coordinates of the square between an attack moves origin and its target
     *
     * @param move the move to find the center point of
     * @return the coordinates of the average point of the moves origin and target
     */
    public static Coordinates getKillCoords(Move move) {
        Coordinates attackOrigin = move.getOrigin();
        Coordinates attackTarget = move.getTarget();

        int enemyX = (attackOrigin.x + attackTarget.x) / 2;
        int enemyY = (attackOrigin.y + attackTarget.y) / 2;
        return new Coordinates(enemyX, enemyY);
    }

    /**
     * check if this Coordinate is beyond the bounds of the board
     *
     * @return whether or not this Coordinate is beyond the bounds of the board
     */
    public boolean isOutsideBoard() {
        return x < 0 || y < 0 || x >= Game.SCALE || y >= Game.SCALE;
    }

    /**
     * check if this Coordinate is on the kings row of the team opposing the given one
     *
     * @param team the team of the unit that has moved
     * @return whether or not this coordinate is on the king row of the team opposing the given team
     */
    public boolean isEnemyKingRow(Team team) {
        if (team == Team.RED) {
            return y == Game.SCALE - 1; //white side
        } else {
            return y == 0; //red side
        }
    }

    /**
     * get the Coordinate position up and to the right of this Coordinates position
     *
     * @return the Coordinate position up and to the right of this Coordinates position
     */
    public Coordinates NE() {
        return new Coordinates(this, x + 1, y - 1);
    }

    /**
     * get the Coordinate position up and to the left of this Coordinates position
     *
     * @return the Coordinate position up and to the left of this Coordinates position
     */
    public Coordinates NW() {
        return new Coordinates(this, x - 1, y - 1);
    }

    /**
     * get the Coordinate position down and to the right of this Coordinates position
     *
     * @return the Coordinate position down and to the right of this Coordinates position
     */
    public Coordinates SE() {
        return new Coordinates(this, x + 1, y + 1);
    }

    /**
     * get the Coordinate position down and to the left of this Coordinates position
     *
     * @return the Coordinate position down and to the left of this Coordinates position
     */
    public Coordinates SW() {
        return new Coordinates(this, x - 1, y + 1);
    }

    /**
     * gets the next Coordinate on the path implied by this coordinate and its parent
     * e.g. if parent = 0,0 and this = 1,1 then the next coordinate must be 2,2
     *
     * @return the next Coordinate on the path implied by this coordinate and its parent
     */
    public Coordinates getNextOnPath() {
        int xDiff = x - origin.x;
        int yDiff = y - origin.y;

        int xDest = x + xDiff;
        int yDest = y + yDiff;

        return new Coordinates(origin, xDest, yDest);
    }

    /**
     * checks if this Coordinates position is on the correct color tile - the game can be played on only one colour at
     * a time
     *
     * @return whether or not this Coordinates position is on the currently selected color tiles to be played on
     */
    public boolean isPlaySquare() {
        return (x + y) % 2 != Game.PLAY_SQUARE;
    }

    /**
     * evaluate whether or not this Coordinate and another are equivalent
     *
     * @param o the other object, typically another Coordinate instance to compare to this Coordinate instance
     * @return whether or not this Coordinate and another are equivalent
     */
    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Coordinates that = (Coordinates) o;
        return x == that.x && y == that.y;
    }

    /**
     * convert this Coordinate into a hashcode for use as needed in some data structures
     *
     * @return the hashcode produced by this Coordinate
     */
    @Override public int hashCode() {
        return Objects.hash(x, y);
    }
}

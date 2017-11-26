import java.util.Objects;

public class Coordinates {

    public final int x;
    public final int y;
    public final Coordinates origin;

    public Coordinates(int x, int y) {
        this(null, x, y);
    }

    public Coordinates(Coordinates origin, int x, int y) {
        this.x = x;
        this.y = y;
        this.origin = origin;
    }

    public static int toBoard(double pixel) {
        return (int) (pixel + Game.TILE_SIZE / 2) / Game.TILE_SIZE;
    }

    public double getPixelX(){
        return (double) (x * Game.TILE_SIZE) + (Game.TILE_SIZE / 2);
    }

    public double getPixelY(){
        return (double) (y * Game.TILE_SIZE) + (Game.TILE_SIZE / 2);
    }

    public static Coordinates getKillCoords(Move move) {
        Coordinates attackOrigin = move.getOrigin();
        Coordinates attackTarget = move.getTarget();

        int enemyX = (attackOrigin.x + attackTarget.x) / 2;
        int enemyY = (attackOrigin.y + attackTarget.y) / 2;
        return new Coordinates(enemyX, enemyY);
    }

    public static boolean isOutsideBoard(Coordinates location) {
        return location.x < 0 || location.y < 0 || location.x >= Game.SCALE || location.y >= Game.SCALE;
    }

    public boolean isEnemyKingRow(Team team) {
        if (team == Team.RED) {
            return y == Game.SCALE - 1;
        } else {
            return y == 0;
        }
    }

    public Coordinates NE() {
        return new Coordinates(this, x + 1, y - 1);
    }

    public Coordinates NW() {
        return new Coordinates(this, x - 1, y - 1);
    }

    public Coordinates SE() {
        return new Coordinates(this, x + 1, y + 1);
    }

    public Coordinates SW() {
        return new Coordinates(this, x - 1, y + 1);
    }

    public Coordinates getNextOnPath() {
        int xDiff = x - origin.x;
        int yDiff = y - origin.y;

        int xDest = x + xDiff;
        int yDest = y + yDiff;

        return new Coordinates(origin, xDest, yDest);
    }

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

    @Override public int hashCode() {
        return Objects.hash(x, y);
    }
}

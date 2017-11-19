

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
        return (int) (pixel + CheckersGame.TILE_SIZE / 2) / CheckersGame.TILE_SIZE;
    }

    public static Coordinates getKillCoords(Coordinates attackOrigin, int xMoveDistance, int yMoveDistance) {
        int enemyX = attackOrigin.x + (xMoveDistance) / 2;
        int enemyY = attackOrigin.y + (yMoveDistance) / 2;
        return new Coordinates(enemyX, enemyY);
    }

    public static boolean isOutsideBoard(Coordinates location) {
        return location.x < 0 || location.y < 0 || location.x >= CheckersGame.SCALE || location.y >= CheckersGame.SCALE;
    }

    public boolean isEnemySide(Team team) {
        if (team == Team.RED) {
            return y == CheckersGame.SCALE - 1;
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
}

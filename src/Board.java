import javafx.scene.Group;
import javafx.scene.Node;

public class Board {

    private Group components = new Group();
    private Group tiles = new Group();

    private Tile[][] board;

    public Board(){
        board = new Tile[Game.SCALE][Game.SCALE];
        components.getChildren().setAll(tiles);

        generateBoard();
    }

    private void generateBoard() {
        for (int y = 0; y < Game.SCALE; y++) {
            for (int x = 0; x < Game.SCALE; x++) {
                generateTile(y, x);
            }
        }
    }

    private void generateTile(int y, int x) {
        Tile tile = new Tile((x + y) % 2 == 0, x, y);
        board[x][y] = tile;
        tiles.getChildren().add(tile);
    }

    public static boolean isBoardEdge(Coordinates pos) {
        return pos.x == 0 || pos.y == 0 || pos.x == Game.SCALE - 1 || pos.y == Game.SCALE - 1;
    }

    public Group getComponents() {
        return components;
    }

    public void resetTileColors() {
        for (Node node : tiles.getChildren()) {
            Tile tile = (Tile) node;
            tile.resetTileColor();
        }
    }

    public Tile getTile(Coordinates position) {
        return board[position.x][position.y];
    }

    public boolean isPlaySquare(Coordinates c) {
        return (c.x + c.y) % 2 != Game.PLAY_SQUARE;
    }

    public void moveUnit(Coordinates origin, Coordinates target, Unit unit){
        getTile(origin).setUnit(null);
        getTile(target).setUnit(unit);
    }

}

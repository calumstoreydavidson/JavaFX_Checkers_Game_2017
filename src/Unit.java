

import java.util.ArrayList;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

public class Unit extends StackPane {

    private UnitType type;
    private Team team;
    private CheckersGame game;

    private double mouseX, mouseY;
    private double currentX, currentY;

    public Unit(UnitType type, Team team, int x, int y, CheckersGame game) {
        this.game = game;
        this.team = team;
        this.type = type;

        move(new Coordinates(x, y));

        Ellipse bg = new Ellipse(CheckersGame.TILE_SIZE * 0.3125, CheckersGame.TILE_SIZE * 0.26);
        bg.setFill(Color.BLACK);

        bg.setStroke(Color.BLACK);
        bg.setStrokeWidth(CheckersGame.TILE_SIZE * 0.03);

        bg.setTranslateX((CheckersGame.TILE_SIZE - CheckersGame.TILE_SIZE * 0.3125 * 2) / 2);
        bg.setTranslateY((CheckersGame.TILE_SIZE - CheckersGame.TILE_SIZE * 0.26 * 2) / 2 + CheckersGame.TILE_SIZE * 0.07);

        Ellipse ellipse = new Ellipse(CheckersGame.TILE_SIZE * 0.3125, CheckersGame.TILE_SIZE * 0.26);
        ellipse.setFill(team == Team.RED ? Color.valueOf("#c40003") : Color.valueOf("#fff9f4"));

        ellipse.setStroke(Color.BLACK);
        ellipse.setStrokeWidth(CheckersGame.TILE_SIZE * 0.03);

        ellipse.setTranslateX((CheckersGame.TILE_SIZE - CheckersGame.TILE_SIZE * 0.3125 * 2) / 2);
        ellipse.setTranslateY((CheckersGame.TILE_SIZE - CheckersGame.TILE_SIZE * 0.26 * 2) / 2);

        if (isKing()) {
            Rectangle crown = new Rectangle(ellipse.getRadiusX(), ellipse.getRadiusY());
            ellipse.setFill(Color.valueOf("gold"));

            getChildren().addAll(bg, ellipse, crown);
        }else {
            getChildren().addAll(bg, ellipse);
        }

        setOnMousePressed(e -> {
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
        });

        setOnMouseDragged(e -> {
            relocate(e.getSceneX() - mouseX + currentX, e.getSceneY() - mouseY + currentY);
        });
    }

    public UnitType getType() {
        return type;
    }

    public Team getTeam() {
        return team;
    }

    public int getCurrentX() {
        return Coordinates.toBoard(currentX);
    }

    public int getCurrentY() {
        return Coordinates.toBoard(currentY);
    }

    public Coordinates getCurrentCoords() {
        return new Coordinates(getCurrentX(), getCurrentY());
    }

    public void move(Coordinates targetCoords) {
        currentX = targetCoords.x * CheckersGame.TILE_SIZE;
        currentY = targetCoords.y * CheckersGame.TILE_SIZE;
        relocate(currentX, currentY);
    }

    public void abortMove() {
        relocate(currentX, currentY);
    }

    public boolean isRed() {
        return team == Team.RED;
    }

    public boolean isWhite() {
        return team == Team.WHITE;
    }

    //returns moves to empty adjacent spaces and spaces beyond adjacent enemies
    public ArrayList<Move> getPossibleMoves() {
        ArrayList<Move> moves = new ArrayList<>();

        for (Coordinates adjacentTile : getAdjacentTiles(this)) {
            if (!isOccupiedTile(adjacentTile)) {
                MoveResult result = new MoveResult(MoveType.NORMAL, game.getBoard()[adjacentTile.x][adjacentTile.y].getUnit());
                moves.add(new Move(this, adjacentTile, result));
            } else if (isEnemyUnit(this, adjacentTile) && !isEnemyOnEdge(adjacentTile) && !isOccupiedTile(adjacentTile.getNextOnPath())) {
                MoveResult result = new MoveResult(MoveType.KILL, game.getBoard()[adjacentTile.x][adjacentTile.y].getUnit());
                moves.add(new Move(this, adjacentTile.getNextOnPath(), result));
            }
        }
        return moves;
    }

    //returns positions adjacent to the unit, that exist on the board
    private ArrayList<Coordinates> getAdjacentTiles(Unit unit) {
        ArrayList<Coordinates> adjacentTiles = new ArrayList<>();

        Coordinates origin = unit.getCurrentCoords();

        if (isKing() || unit.isRed()) {
            adjacentTiles.add(origin.SW());
            adjacentTiles.add(origin.SE());
        }
        if (isKing() || unit.isWhite()) { // if unit is king or red
            adjacentTiles.add(origin.NW());
            adjacentTiles.add(origin.NE());
        }

        ArrayList<Coordinates> validAdjacentTiles = new ArrayList<>();
        for (Coordinates position: adjacentTiles) {
            if (!Coordinates.isOutsideBoard(position)){
                validAdjacentTiles.add(position);
            }
        }

        return validAdjacentTiles;
    }

    public boolean isOccupiedTile(Coordinates c) {
        return game.getBoard()[c.x][c.y].hasUnit();
    }

    public boolean isEnemyUnit(Unit unit, Coordinates c) {
        Unit enemyUnit = game.getBoard()[c.x][c.y].getUnit();
        return enemyUnit.getTeam() != unit.getTeam();
    }

    public boolean isEnemyOnEdge(Coordinates enemyPos){
        return CheckersGame.isBoardEdge(enemyPos);
    }

    public boolean isKing(){
        return type == UnitType.KING;
    }

    public void crownKing(){
        if (team == Team.RED ){
            type = UnitType.KING;
        }
        if (team == Team.WHITE){
            type = UnitType.KING;
        }
    }
}
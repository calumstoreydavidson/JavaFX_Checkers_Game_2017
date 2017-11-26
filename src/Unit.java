import java.util.ArrayList;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

public class Unit extends StackPane {

    private UnitType type;
    private Team team;

    private double mouseX, mouseY;
    private double currentX, currentY;

    public Unit(UnitType type, Team team, Coordinates c) {
        this.team = team;
        this.type = type;

        move(c);

        PaintUnit();

        setOnMousePressed(e -> {
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
        });

        setOnMouseDragged(e -> {
            relocate(e.getSceneX() - mouseX + currentX, e.getSceneY() - mouseY + currentY);
        });

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

    public Coordinates getPos() {
        return new Coordinates(getCurrentX(), getCurrentY());
    }

    public void move(Coordinates targetCoords) {
        currentX = targetCoords.x * Game.TILE_SIZE;
        currentY = targetCoords.y * Game.TILE_SIZE;
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

    //returns positions adjacent to the unit, that exist on the board
    public ArrayList<Coordinates> getPossiblePositions() {
        ArrayList<Coordinates> potentiallyAdjacentTiles = new ArrayList<>();

        Coordinates origin = getPos();

        if (isKing() || isRed()) {
            potentiallyAdjacentTiles.add(origin.SW());
            potentiallyAdjacentTiles.add(origin.SE());
        }
        if (isKing() || isWhite()) {
            potentiallyAdjacentTiles.add(origin.NW());
            potentiallyAdjacentTiles.add(origin.NE());
        }

        ArrayList<Coordinates> validAdjacentTiles = new ArrayList<>();
        for (Coordinates position : potentiallyAdjacentTiles) {
            if (!Coordinates.isOutsideBoard(position)) {
                validAdjacentTiles.add(position);
            }
        }

        return validAdjacentTiles;
    }

    public void toggleKing(){
        type = isKing() ? UnitType.PAWN : UnitType.KING;
        PaintUnit();
    }

    public boolean isKing() {
        return type == UnitType.KING;
    }

    private void PaintUnit() {
        int verticalOffset = -15 * (type.layers - 1);

        double width = Game.TILE_SIZE * 0.3125;
        double height = Game.TILE_SIZE * 0.26;

        Ellipse bg = new Ellipse(width, height);
        bg.setFill(Color.BLACK);

        bg.setStroke(Color.BLACK);
        bg.setStrokeWidth(Game.TILE_SIZE * 0.03);

        bg.setTranslateX((Game.TILE_SIZE - width * 2) / 2);
        bg.setTranslateY(((Game.TILE_SIZE - height * 2) / 2 + Game.TILE_SIZE * 0.07) + verticalOffset);


        Ellipse ellipse = new Ellipse(width, height);
        ellipse.setFill(team == Team.RED ? Color.valueOf("#c40003") : Color.valueOf("#fff9f4"));

        ellipse.setStroke(Color.BLACK);
        ellipse.setStrokeWidth(Game.TILE_SIZE * 0.03);

        ellipse.setTranslateX((Game.TILE_SIZE - width * 2) / 2);
        ellipse.setTranslateY(((Game.TILE_SIZE - height * 2) / 2) + verticalOffset);
        
        if (type == UnitType.PAWN) {
            getChildren().setAll(bg, ellipse);
        }else {
            getChildren().addAll(bg, ellipse);
        }
    }
}
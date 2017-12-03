import java.util.ArrayList;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

/**
 * represents a displayed unit on the game board
 */
public class Unit extends StackPane {

    //the type of this unit e.g. pawn or king
    private UnitType type;

    //the team of this unit e.g. red or white
    private Team team;

    //the x and y values of the mouse for use in displaying click drag animation
    private double mouseX, mouseY;

    //the x and y values of the unit
    private double currentX, currentY;

    /**
     * create a new unit with a type, a team and a board position
     *
     * @param type     the type of the unit
     * @param team     the units team
     * @param position the board position of the unit
     */
    public Unit(UnitType type, Team team, Coordinates position) {
        this.team = team;
        this.type = type;

        move(position);

        PaintUnit();

        setOnMousePressed(event -> {
            mouseX = event.getSceneX();
            mouseY = event.getSceneY();
        });

        setOnMouseDragged(event -> relocate(event.getSceneX() - mouseX + currentX, event.getSceneY() - mouseY + currentY));
    }

    /**
     * get the units team
     *
     * @return the units team
     */
    public Team getTeam() {
        return team;
    }

    /**
     * get the current x location of the unit converted to a pixel scale value
     *
     * @return the current x location of the unit converted to a pixel scale value
     */
    public int getCurrentX() {
        return Coordinates.toBoard(currentX);
    }

    /**
     * the current y location of the unit converted to a pixel scale value
     *
     * @return current y location of the unit converted to a pixel scale value
     */
    public int getCurrentY() {
        return Coordinates.toBoard(currentY);
    }

    /**
     * get the current position of the unit at tile scale as a Coordinate
     *
     * @return the current position of the unit at tile scale as a Coordinate
     */
    public Coordinates getPos() {
        return new Coordinates(getCurrentX(), getCurrentY());
    }

    /**
     * set the units position at pixel scale to the given position
     *
     * @param targetPosition the position to move the unit too
     */
    public void move(Coordinates targetPosition) {
        currentX = targetPosition.x * Game.TILE_SIZE;
        currentY = targetPosition.y * Game.TILE_SIZE;
        relocate(currentX, currentY);
    }

    /**
     * reset the unit back to its starting position when it is dropped somewhere invalid
     */
    public void abortMove() {
        relocate(currentX, currentY);
    }

    /**
     * check whether the units team is red
     *
     * @return whether the units team is red
     */
    public boolean isRed() {
        return team == Team.RED;
    }

    /**
     * check whether the units team is white
     *
     * @return whether the units team is white
     */
    public boolean isWhite() {
        return team == Team.WHITE;
    }

    //returns positions adjacent to the unit, that exist on the board

    /**
     * get the adjacent positions to this units position,
     * where an adjacent position is a connected square in a valid direction of travel
     *
     * @return the list of adjacent positions to this unit
     */
    public ArrayList<Coordinates> getAdjacentPositions() {
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
            if (!position.isOutsideBoard()) {
                validAdjacentTiles.add(position);
            }
        }

        return validAdjacentTiles;
    }

    /**
     * make unit a king if it is a pawn, and vice versa
     */
    public void toggleKing() {
        type = isKing() ? UnitType.PAWN : UnitType.KING;
        PaintUnit();
    }

    /**
     * check if the unit is a king
     *
     * @return whether this unit is a king
     */
    public boolean isKing() {
        return type == UnitType.KING;
    }

    /**
     * assemble, position and display the units visible layer components
     */
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
        } else {
            getChildren().addAll(bg, ellipse);
        }
    }
}
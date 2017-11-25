import java.util.ArrayList;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

public class Unit extends StackPane {

    private UnitType type;
    private Team team;
    private Game game;

    private double mouseX, mouseY;
    private double currentX, currentY;

    public Unit(UnitType type, Team team, Coordinates c, Game game) {
        this.game = game;
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

        setOnMouseReleased(e -> {
            int targetX = Coordinates.toBoard(getLayoutX());
            int targetY = Coordinates.toBoard(getLayoutY());

            Coordinates origin = getCurrentCoords();
            Coordinates target = new Coordinates(origin, targetX, targetY);

            if (game.DEVELOPMENT_MODE_ENABLED) {
                if (origin.equals(target)) {
                    toggleKing();
                    game.moveUnit(origin, target, this, false);
                } else {
                    game.moveUnit(origin, target, this, false);
                }
            } else {
                Move actualMove = null;
                for (Move move : game.getPossibleMoves()) {
                    if (move.getUnit().getCurrentCoords().equals(origin) && move.getTarget().equals(target)) {
                        actualMove = move;
                        break;
                    }
                }
                if (actualMove == null) {
                    MoveResult result = new MoveResult(MoveType.NONE);
                    actualMove = new Move(this, target, result);
                }

                game.executeMove(actualMove);
            }
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

    public Coordinates getCurrentCoords() {
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

    //returns moves to empty adjacent spaces and spaces beyond adjacent enemies
    public ArrayList<Move> getPossibleMoves() {
        ArrayList<Move> moves = new ArrayList<>();

        for (Coordinates adjacentTile : getAdjacentTiles(this)) {
            if (!isOccupiedTile(adjacentTile)) {
                MoveResult result = new MoveResult(MoveType.NORMAL);
                if (adjacentTile.isEnemyKingRow(team) && !isKing()){
                    result.createKing();
                }
                moves.add(new Move(this, adjacentTile, result));
            } else if (isEnemyUnit(this, adjacentTile) && isAttackPossible(adjacentTile)) {
                Unit attackedUnit = game.getBoard().getTile(adjacentTile).getUnit();
                MoveResult result = new MoveResult(MoveType.KILL, attackedUnit);
                if (adjacentTile.getNextOnPath().isEnemyKingRow(team) && !isKing() || attackedUnit.isKing() && !isKing() && Game.CROWN_STEALING_ALLOWED){
                    result.createKing();
                }
                moves.add(new Move(this, adjacentTile.getNextOnPath(), result));
            }
        }
        return moves;
    }

    private boolean isAttackPossible(Coordinates adjacentTile) {
        return !isEnemyOnEdge(adjacentTile) && !isOccupiedTile(adjacentTile.getNextOnPath());
    }

    public boolean canMove() {
        return !getPossibleMoves().isEmpty();
    }

    //returns positions adjacent to the unit, that exist on the board
    private ArrayList<Coordinates> getAdjacentTiles(Unit unit) {
        ArrayList<Coordinates> potentiallyAdjacentTiles = new ArrayList<>();

        Coordinates origin = unit.getCurrentCoords();

        if (isKing() || unit.isRed()) {
            potentiallyAdjacentTiles.add(origin.SW());
            potentiallyAdjacentTiles.add(origin.SE());
        }
        if (isKing() || unit.isWhite()) { // if unit is king or red
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

    public boolean isOccupiedTile(Coordinates c) {
        return game.getBoard().getTile(c).hasUnit();
    }

    public boolean isEnemyUnit(Unit unit, Coordinates c) {
        Unit enemyUnit = game.getBoard().getTile(c).getUnit();
        return enemyUnit.getTeam() != unit.getTeam();
    }

    public boolean isEnemyOnEdge(Coordinates enemyPos) {
        return Board.isBoardEdge(enemyPos);
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
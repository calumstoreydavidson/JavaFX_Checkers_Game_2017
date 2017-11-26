//import java.util.ArrayList;
//
//public class BoardSim {
//
//    private enum Type {
//        EMPTY, WHITE, RED, WHITE_KING, RED_KING
//    }
//
//    private Type[][] simBoard;
//
//    public BoardSim(Board oldBoard) {
//        generateSimTiles(oldBoard);
//
//    }
//
//    public void generateSimTiles(Board oldBoard) {
//        simBoard = new Type[Game.SCALE][Game.SCALE];
//        for (int i = 0; i < Game.SCALE; i++) {
//            for (int j = 0; j < Game.SCALE; j++) {
//                generateSimTile(oldBoard, i, j);
//            }
//        }
//    }
//
//    public void generateSimTile(Board oldBoard, int i, int j) {
//        Tile tile = oldBoard.getTile(new Coordinates(i, j));
//        if (tile.getUnit() == null) {
//            simBoard[i][j] = Type.EMPTY;
//        } else {
//            Unit unit = tile.getUnit();
//            if (unit.isRed()) {
//                if (unit.isKing()) {
//                    simBoard[i][j] = Type.RED_KING;
//                } else {
//                    simBoard[i][j] = Type.RED;
//                }
//            } else {
//                if (unit.isKing()) {
//                    simBoard[i][j] = Type.WHITE_KING;
//                } else {
//                    simBoard[i][j] = Type.WHITE;
//                }
//            }
//        }
//    }
//
//    public ArrayList<Move> getUnitsPossibleMoves(Coordinates origin, boolean isKing, Team team) {
//        ArrayList<Move> moves = new ArrayList<>();
//
//        for (Coordinates possiblePosition : getUnitsPossiblePositions(origin, isKing, team)) {
//            if (getTile(possiblePosition) == Type.EMPTY) {
//                MoveResult result = new MoveResult(MoveType.NORMAL);
//                if (possiblePosition.isEnemyKingRow(team) && !isKing) {
//                    result.createKing();
//                }
//                moves.add(new Move(origin, possiblePosition, result));
//            } else if (isEnemyUnit(unit, possiblePosition) && isAttackPossible(possiblePosition)) {
//                Type attackedUnit = getTile(possiblePosition);
//                MoveResult result = new MoveResult(MoveType.KILL);
//                if (possiblePosition.getNextOnPath().isEnemyKingRow(team) && !isKing || attackedUnit.isKing() && !isKing && Game.CROWN_STEALING_ALLOWED) {
//                    result.createKing();
//                }
//                moves.add(new Move(origin, possiblePosition.getNextOnPath(), result));
//            }
//        }
//        return moves;
//    }
//
//    public ArrayList<Coordinates> getUnitsPossiblePositions(Coordinates origin, boolean isKing, Team team){
//        ArrayList<Coordinates> potentiallyAdjacentTiles = new ArrayList<>();
//
//        if (isKing || team == Team.RED) {
//            potentiallyAdjacentTiles.add(origin.SW());
//            potentiallyAdjacentTiles.add(origin.SE());
//        }
//        if (isKing || team == Team.WHITE) {
//            potentiallyAdjacentTiles.add(origin.NW());
//            potentiallyAdjacentTiles.add(origin.NE());
//        }
//
//        ArrayList<Coordinates> validAdjacentTiles = new ArrayList<>();
//        for (Coordinates position : potentiallyAdjacentTiles) {
//            if (!Coordinates.isOutsideBoard(position)) {
//                validAdjacentTiles.add(position);
//            }
//        }
//
//        return validAdjacentTiles;
//    }
//
//    private Type getTile(Coordinates position){
//        return simBoard[position.x][position.y];
//    }
//}

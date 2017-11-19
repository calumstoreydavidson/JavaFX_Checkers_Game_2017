

public class Move {

    private final Unit unit;
    private final Coordinates target;
    private final MoveResult result;

//    public Move(Unit unit, Coordinates target, CheckersGame game) {
//        this.unit = unit;
//        this.target = target;
//        this.game = game;
//        this.result = validateMove();
//    }

    public Move(Unit unit, Coordinates target, MoveResult result){
        this.unit = unit;
        this.target = target;
        this.result = result;
    }

    public Unit getUnit() {
        return unit;
    }

    public Coordinates getTarget() {
        return target;
    }

    public MoveResult getResult() {
        return result;
    }

//    private MoveResult validateMove() {
//        if (!isAlliedUnit(unit) || isInvalidTile()) {
//            return new MoveResult(MoveType.NONE);
//        }

//        Coordinates origin = target.origin;
//
//        int xMoveDistance = target.x - origin.x;
//        int yMoveDistance = target.y - origin.y;
//
//        if (isNormalMove(unit, xMoveDistance, yMoveDistance)) {
//            return new MoveResult(MoveType.NORMAL);
//
//        } else if (isAttackMove(unit, xMoveDistance, yMoveDistance)) {
//            Coordinates enemyCoords = Coordinates.getKillCoords(origin, xMoveDistance, yMoveDistance);
//
//            if (game.isEnemyUnit(unit, enemyCoords)) {
//                return new MoveResult(MoveType.KILL, game.getBoard()[enemyCoords.x][enemyCoords.y].getUnit());
//            }
//        }
//        return new MoveResult(MoveType.NONE);
//    }

//    private boolean isInvalidTile() {
//        return Coordinates.isOutsideBoard(target) || !game.isPlaySquare(target) || game.isOccupiedTile(target);
//    }
//
//    private boolean isAlliedUnit(Unit unit) {
//        return unit.isRed() && game.isRedsTurn() || unit.isWhite() && !game.isRedsTurn();
//    }

//    private boolean isAttackMove(Unit unit, int xMoveDistance, int yMoveDistance) {
//        return Math.abs(xMoveDistance) == 2 && yMoveDistance == unit.getType().moveDir * 2;
//    }
//
//    private boolean isNormalMove(Unit unit, int xMoveDistance, int yMoveDistance) {
//        return Math.abs(xMoveDistance) == 1 && yMoveDistance == unit.getType().moveDir;
//    }

}

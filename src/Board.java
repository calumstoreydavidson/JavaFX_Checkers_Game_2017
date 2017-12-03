import java.util.ArrayList;

public abstract class Board {

    private Team currentTeam;
    private Unit MovingUnit;

    public ArrayList<Move> prioritiseAttackMoves(ArrayList<Move> possibleUnitMoves) {
        ArrayList<Move> attackMoves = new ArrayList<>();
        for (Move move : possibleUnitMoves) {
            if (move.getType() == MoveType.KILL) {
                attackMoves.add(move);
            }
        }

        if (!attackMoves.isEmpty()) {
            return attackMoves;
        } else {
            return possibleUnitMoves;
        }
    }

    public boolean isEnemyOnEdge(Coordinates enemyPos) {
        return Coordinates.isBoardEdge(enemyPos);
    }

    public void setNextPlayer() {
        currentTeam = currentTeam == Team.RED ? Team.WHITE : Team.RED;
    }

    public Team getCurrentTeam() {
        return currentTeam;
    }

    public void setCurrentTeam(Team currentTeam) {
        this.currentTeam = currentTeam;
    }

    public Unit getMovingUnit() {
        return MovingUnit;
    }

    public void setMovingUnit(Unit movingUnit) {
        MovingUnit = movingUnit;
    }
}

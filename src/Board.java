import java.util.ArrayList;

public abstract class Board {

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


}

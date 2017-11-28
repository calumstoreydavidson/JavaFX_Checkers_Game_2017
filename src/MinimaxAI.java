import java.util.Optional;

public class MinimaxAI implements Player {

    boolean isPlayerHuman;
    private boolean isPlayersTurn;
    private Team playerTeam;
    private int maxDepth = 7;

    public MinimaxAI(Team playerTeam) {
        this.playerTeam = playerTeam;
        //red always goes first
        isPlayersTurn = playerTeam == Team.RED;
        this.isPlayerHuman = false;
    }

    @Override public Optional<Move> getPlayerMove(Board board) {
        BoardSim sim = new BoardSim(board, playerTeam);
        return Optional.of(minimax(sim, 0, true).move);
    }

    private MoveAndScore minimax(BoardSim node, int depth, boolean maximisingPlayer) {
        MoveAndScore bestValue;
        if (node.getTeamsPossibleMoves().isEmpty() || depth == maxDepth) {
            bestValue = new MoveAndScore(null, node.evaluateState(playerTeam));
        } else if (maximisingPlayer) {
            bestValue = new MoveAndScore(null, Integer.MIN_VALUE);
            for (Move move : node.getTeamsPossibleMoves()) {
                MoveAndScore childValue = minimax(node.getChild(move), depth + 1, false);
                childValue.move = move;
                bestValue = getMax(bestValue, childValue);
            }
        } else {
            bestValue = new MoveAndScore(null, Integer.MAX_VALUE);
            for (Move move : node.getTeamsPossibleMoves()) {
                MoveAndScore childValue = minimax(node.getChild(move), depth + 1, true);
                childValue.move = move;
                bestValue = getMin(bestValue, childValue);
            }
        }
        return bestValue;
    }

    public boolean isPlayerHuman() {
        return isPlayerHuman;
    }

    public boolean isPlayersTurn() {
        return isPlayersTurn;
    }

    public void switchTurn() {
        isPlayersTurn = !isPlayersTurn;
    }

    public Team getPlayerTeam() {
        return playerTeam;
    }

    @Override public void resetPlayer() {
        isPlayersTurn = playerTeam == Team.RED;
    }

    private MoveAndScore getMax(MoveAndScore best, MoveAndScore child) {
        return child.score > best.score ? child : best;
    }

    private MoveAndScore getMin(MoveAndScore best, MoveAndScore child) {
        return child.score < best.score ? child : best;
    }

    private class MoveAndScore {
        Move move;
        int score;

        MoveAndScore(Move move, int score) {
            this.move = move;
            this.score = score;
        }
    }
}

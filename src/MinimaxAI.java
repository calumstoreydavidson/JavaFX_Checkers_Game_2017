import java.util.Optional;

public class MinimaxAI implements Player {

    boolean isPlayerHuman;
    private boolean isPlayersTurn;
    private Team playerTeam;
    private int maxDepth = Game.AI_MAX_SEARCH_DEPTH;

    public MinimaxAI(Team playerTeam) {
        this.playerTeam = playerTeam;
        //red always goes first
        isPlayersTurn = playerTeam == Team.RED;
        this.isPlayerHuman = false;
    }

    @Override public Optional<Move> getPlayerMove(Board board) {
        if (Game.VERBOSE_OUTPUT) {
            Main.output.appendText("AI is thinking \n");
        }

        BoardSim sim = new BoardSim(board, playerTeam);
        return Optional.of(minimax(sim, 0, true).move);
    }

    private MoveAndScore minimax(BoardSim node, int depth, boolean maximisingPlayer) {
        MoveAndScore best;
        if (node.getTeamsPossibleMoves().isEmpty() || depth == maxDepth) {
            best = new MoveAndScore(null, node.evaluateState());
        } else if (maximisingPlayer) {
            best = new MoveAndScore(null, Integer.MIN_VALUE);
            for (Move move : node.getTeamsPossibleMoves()) {
                MoveAndScore score = minimax(node.getChild(move), depth + 1, false);
                score.move = move;
                best = getMax(best, score);
            }

        } else {
            best = new MoveAndScore(null, Integer.MAX_VALUE);
            for (Move move : node.getTeamsPossibleMoves()) {
                MoveAndScore childValue = minimax(node.getChild(move), depth + 1, true);
                childValue.move = move;
                best = getMin(best, childValue);
            }
        }
        return best;
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
        double score;

        MoveAndScore(Move move, double score) {
            this.move = move;
            this.score = score;
        }
    }
}

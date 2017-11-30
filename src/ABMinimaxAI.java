import java.util.Optional;

public class ABMinimaxAI implements Player {

    boolean isPlayerHuman;
    private boolean isPlayersTurn;
    private Team playerTeam;
    private int maxDepth = Game.AI_MAX_SEARCH_DEPTH;

    public ABMinimaxAI(Team playerTeam) {
        this.playerTeam = playerTeam;
        //red always goes first
        isPlayersTurn = playerTeam == Team.RED;
        this.isPlayerHuman = false;
    }

    @Override public Optional<Move> getPlayerMove(Board board){
        if (Game.VERBOSE_OUTPUT) {
            Main.output.appendText("AI is thinking \n");
        }

        MoveAndScore alpha = new MoveAndScore(null,Integer.MIN_VALUE);
        MoveAndScore beta = new MoveAndScore(null,Integer.MAX_VALUE);
        BoardSim sim = new BoardSim(board, playerTeam);

        System.out.println("");
        return Optional.of(minimax(sim, 0, alpha, beta, true).move);
    }

    private MoveAndScore minimax(BoardSim node, int depth, MoveAndScore alpha, MoveAndScore beta, boolean maximisingPlayer) {
        MoveAndScore best;
        if (node.getTeamsPossibleMoves().isEmpty() || depth == maxDepth) {
            best = new MoveAndScore(null, node.evaluateState());
        } else if (maximisingPlayer) {
            best = alpha;

            // Recurse for all children of node.
            for (Move move : node.getTeamsPossibleMoves()) {
                MoveAndScore child = minimax(node.getChild(move), depth + 1, best, beta, false);
                child.move = move;
                best = Math.max(best.score, child.score) == best.score ? best : child;
                if (beta.score <= best.score) {
                    break;
                }
            }
        } else {
            best = beta;

            // Recurse for all children of node.
            for (Move move : node.getTeamsPossibleMoves()) {
                MoveAndScore child = minimax(node.getChild(move), depth + 1, alpha, best, true);
                child.move = move;
                best = Math.min(best.score, child.score) == best.score ? best : child;
                if (best.score <= alpha.score) {
                    break;
                }
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

    private class MoveAndScore {
        Move move;
        double score;

        MoveAndScore(Move move, double score) {
            this.move = move;
            this.score = score;
        }
    }
}

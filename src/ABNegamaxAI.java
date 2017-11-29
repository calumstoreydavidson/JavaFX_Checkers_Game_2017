import java.util.Optional;

public class ABNegamaxAI implements Player {

    boolean isPlayerHuman;
    private boolean isPlayersTurn;
    private Team playerTeam;
    private int maxDepth = Game.AI_MAX_SEARCH_DEPTH;

    public ABNegamaxAI(Team playerTeam) {
        this.playerTeam = playerTeam;
        //red always goes first
        isPlayersTurn = playerTeam == Team.RED;
        this.isPlayerHuman = false;
    }

    @Override public Optional<Move> getPlayerMove(Board board) {
        if (Game.VERBOSE_OUTPUT) {
            Main.output.appendText("AI is thinking \n");
        }

        MoveAndScore alpha = new MoveAndScore(null,Integer.MIN_VALUE);
        MoveAndScore beta = new MoveAndScore(null,Integer.MAX_VALUE);
        BoardSim sim = new BoardSim(board, playerTeam);

        System.out.println("");
        return Optional.of(negamax(sim, 0, alpha, beta).move);
    }

    private MoveAndScore negamax(BoardSim node, int depth, MoveAndScore alpha, MoveAndScore beta) {
        if (node.getTeamsPossibleMoves().isEmpty() || depth == maxDepth) {
            return new MoveAndScore(null, node.evaluateState(playerTeam));
        }
        MoveAndScore max = new MoveAndScore(null, Integer.MIN_VALUE);

        //for all moves
        for (Move move : node.getTeamsPossibleMoves()) {
            MoveAndScore child = negate(negamax(node.getChild(move), depth + 1, negate(beta), negate(alpha)));
            child.move = move;
            max = child.score > max.score ? child : max;
            alpha = child.score > alpha.score ? child : alpha;
            if (alpha.score >= beta.score) {
                return alpha;
            }
        }

        return max;
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

    public MoveAndScore negate(MoveAndScore value) {
        value.negateScore();
        return value;
    }

    @Override public void resetPlayer() {
        isPlayersTurn = playerTeam == Team.RED;
    }

    private class MoveAndScore {
        Move move;
        int score;

        MoveAndScore(Move move, int score) {
            this.move = move;
            this.score = score;
        }

        public void negateScore() {
            score = -score;
        }
    }
}

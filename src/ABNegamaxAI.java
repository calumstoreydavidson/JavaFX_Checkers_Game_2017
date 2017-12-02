import java.util.Optional;

public class ABNegamaxAI implements Player {

    boolean isPlayerHuman;
    private boolean isPlayersTurn;
    private Team playerTeam;

    public ABNegamaxAI(Team playerTeam) {
        this.playerTeam = playerTeam;
        //red always goes first
        isPlayersTurn = playerTeam == Team.RED;
        this.isPlayerHuman = false;
    }

    @Override public Optional<Move> getPlayerMove(DisplayBoard displayBoard) {
        if (Game.VERBOSE_OUTPUT) {
            Main.output.appendText("AI is thinking \n");
        }
        double alpha = Integer.MIN_VALUE;
        double beta = Integer.MAX_VALUE;
        SimulationBoard sim = new SimulationBoard(displayBoard, playerTeam);

        return Optional.of(negamax(sim, 0, 1, alpha, beta).move);
    }

    private MoveAndScore negamax(SimulationBoard node, int depth, int team, double alpha, double beta) {
        if (node.getTeamsPossibleMoves().isEmpty() || depth == getMaxSearchDepth()) {
            MoveAndScore result = new MoveAndScore(null, node.evaluateState());
            return result;
        }
        MoveAndScore max = new MoveAndScore(null, Integer.MIN_VALUE);

        //for all moves
        for (Move move : node.getTeamsPossibleMoves()) {
            MoveAndScore child = negate(negamax(node.getChild(move), depth + 1, -team, -beta, -alpha));
            child.move = move;
            max = child.score > max.score ? child : max;
            alpha = max.score > alpha ? max.score : alpha;
            if (beta <= alpha) {
                break;
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
        MoveAndScore inverted = new MoveAndScore(value.move, value.score);
        inverted.negateScore();
        return inverted;
    }

    @Override public void resetPlayer() {
        isPlayersTurn = playerTeam == Team.RED;
    }

    private int getMaxSearchDepth() {
        return (int) ((double) Game.AI_MAX_SEARCH_DEPTH * 1.7);//get 1.4 of 1..8 then round it to an int
    }

    private class MoveAndScore {
        Move move;
        double score;

        MoveAndScore(Move move, double score) {
            this.move = move;
            this.score = score;
        }

        public void negateScore() {
            score = -score;
        }
    }
}

import java.util.Optional;

import com.sun.org.glassfish.gmbal.GmbalException;

public class NegamaxAI implements Player {

    boolean isPlayerHuman;
    private boolean isPlayersTurn;
    private Team playerTeam;
    private int maxDepth = Game.AI_MAX_SEARCH_DEPTH;

    public NegamaxAI(Team playerTeam) {
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

        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.println("--------------------------------------------------------------------------------------------------");
        return Optional.of(negamax(sim, 0, 1).move);
    }

    private MoveAndScore negamax(BoardSim node, int depth, int team) {
        if (node.getTeamsPossibleMoves().isEmpty() || depth == maxDepth) {
            MoveAndScore result = new MoveAndScore(null, node.evaluateState());
            result.score *= team;
            return result;
        }
        MoveAndScore max = new MoveAndScore(null, Integer.MIN_VALUE);

        //for all moves
        for (Move move : node.getTeamsPossibleMoves()) {
            MoveAndScore score = negate(negamax(node.getChild(move), depth + 1, -team));
            score.move = move;
            max = score.score > max.score ? score : max;
        }
        return max;
    }

    public MoveAndScore negate(MoveAndScore value) {
        value.negateScore();
        return value;
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

        public void negateScore(){
            score = -score;
        }
    }
}

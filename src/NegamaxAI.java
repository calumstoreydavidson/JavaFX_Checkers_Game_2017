import java.util.Optional;

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

        System.out.println("");
        return Optional.of(negamax(sim, 0).move);
    }

    private MoveAndScore negamax(BoardSim node, int depth) {
        if (node.getTeamsPossibleMoves().isEmpty() || depth == maxDepth) {
            return new MoveAndScore(null, node.evaluateState(playerTeam));
        }
        MoveAndScore max = new MoveAndScore(null, Integer.MIN_VALUE);

        //for all moves
        for (Move move : node.getTeamsPossibleMoves()) {
            MoveAndScore score = negamax(node.getChild(move), depth + 1);
            score.move = move;
            max = Math.max(max.score, score.score) == max.score ? max : score;
        }

        max.negateScore();
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

        public void negateScore(){
            score = -score;
        }
    }
}

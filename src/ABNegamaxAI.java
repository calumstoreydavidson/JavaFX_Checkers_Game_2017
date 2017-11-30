import java.util.Optional;

public class ABNegamaxAI implements Player {

    boolean isPlayerHuman;
    private boolean isPlayersTurn;
    private Team playerTeam;
    private int maxDepth;

    public ABNegamaxAI(Team playerTeam) {
        this.playerTeam = playerTeam;
        //red always goes first
        isPlayersTurn = playerTeam == Team.RED;
        this.isPlayerHuman = false;
    }

    @Override public Optional<Move> getPlayerMove(Board board) {
        maxDepth = Game.AI_MAX_SEARCH_DEPTH;
        if (Game.VERBOSE_OUTPUT) {
            Main.output.appendText("AI is thinking \n");
        }
        System.out.flush();
        MoveAndScore alpha = new MoveAndScore(null,Integer.MIN_VALUE);
        MoveAndScore beta = new MoveAndScore(null,Integer.MAX_VALUE);
        BoardSim sim = new BoardSim(board, playerTeam);

        System.out.println("");
        return Optional.of(negamax(sim, 0, 1, alpha, beta).move);
    }

    private MoveAndScore negamax(BoardSim node, int depth, int team, MoveAndScore alpha, MoveAndScore beta) {
        if (node.getTeamsPossibleMoves().isEmpty() || depth == maxDepth) {
            MoveAndScore result = new MoveAndScore(null, node.evaluateState());
            result.score *= team;
            debugBaseCase(node, depth, result);
            return result;
        }
        MoveAndScore max = new MoveAndScore(null, Integer.MIN_VALUE);

        //for all moves
        for (Move move : node.getTeamsPossibleMoves()) {
            debugInitialState(depth, max, depth%2==0, move);
            MoveAndScore child = negate(negamax(node.getChild(move), depth + 1, -team, negate(beta), negate(alpha)));
            child.move = move;
            max = child.score > max.score ? child : max;
            alpha = child.score > alpha.score ? child : alpha;
            debugFinalState(depth, max, depth%2==0);
            if (alpha.score >= beta.score) {
                debugPruning(depth);
//                return alpha;
                break;
            }
        }

        return max;
    }

    private void debugBaseCase(BoardSim node, int depth, MoveAndScore bestValue) {
        if (Game.VERBOSE_OUTPUT) {
            debugDepth(depth);
            System.out.print("base case: " + bestValue.score + " ");
            System.out.println("Reds: " + node.getRedUnits().size() + " -Whites: " + node.getWhiteUnits().size());
        }
    }

    private void debugInitialState(int depth, MoveAndScore best, boolean maximisingPlayer, Move move) {
        if (Game.VERBOSE_OUTPUT) {
            String player = maximisingPlayer ? " -Max " : " -Min ";
            Team team = getMaximisingPlayerTeam(maximisingPlayer);
            MoveType moveType = move.getResult().getType();
            String moveOrigin = (move.getOrigin().x + 1) + ", " + (move.getOrigin().y + 1) + " -> ";
            String moveTarget = (move.getTarget().x + 1) + ", " + (move.getTarget().y + 1);

            debugDepth(depth);
            System.out.print("init" + player + team + (" Best: " + best.score) + " " + moveType + " " + moveOrigin + moveTarget + "\n");
        }
    }

    private void debugFinalState(int depth, MoveAndScore best, boolean maximisingPlayer) {
        if (Game.VERBOSE_OUTPUT) {
            String player = maximisingPlayer ? " -Max " : " -Min ";
            Team team = getMaximisingPlayerTeam(maximisingPlayer);

            debugDepth(depth);
            System.out.print("fin" + player + team + (" Best: " + best.score) + "\n");
        }
    }

    private void debugDepth(int depth) {
        if (Game.VERBOSE_OUTPUT) {
            for (int i = 1; i <= depth; i++) {
                System.out.print(" | ");
            }
        }
    }

    private void debugPruning(int depth) {
        if (Game.VERBOSE_OUTPUT) {
            debugDepth(depth);
            System.out.println("PRUNING OCCURRED");
        }
    }

    private Team getMaximisingPlayerTeam(boolean maximisingPlayer){
        if (playerTeam == Team.RED){
            return maximisingPlayer ? Team.RED : Team.WHITE;
        }else {
            return maximisingPlayer ? Team.WHITE : Team.RED;
        }
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

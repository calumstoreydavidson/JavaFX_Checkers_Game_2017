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
        double alpha = Integer.MIN_VALUE;
        double beta = Integer.MAX_VALUE;
        BoardSim sim = new BoardSim(board, playerTeam);

        return Optional.of(negamax(sim, 0, 1, alpha, beta).move);
    }

    private MoveAndScore negamax(BoardSim node, int depth, int team, double alpha, double beta) {
        if (node.getTeamsPossibleMoves().isEmpty() || depth == 12) {
            MoveAndScore result = new MoveAndScore(null, node.evaluateState());
//            result.score = result.score * team;
            debugBaseCase(node, depth, result, team);
            return result;
        }
        MoveAndScore max = new MoveAndScore(null, Integer.MIN_VALUE);

        //for all moves
        for (Move move : node.getTeamsPossibleMoves()) {
            debugInitialState(depth, max, depth%2==0, move, team, alpha, beta);
            MoveAndScore child = negate(negamax(node.getChild(move), depth + 1, -team, -beta, -alpha));
            child.move = move;
            max = child.score > max.score ? child : max;
            alpha = max.score > alpha ? max.score : alpha;
            debugFinalState(depth, max, depth%2==0, alpha, beta);
            if (beta <= alpha) {
                debugPruning(depth);
                break;
            }
        }

        return max;
    }
    
    private void debugBaseCase(BoardSim node, int depth, MoveAndScore bestValue, int team) {
        if (Game.VERBOSE_OUTPUT) {
            debugDepth(depth);
            System.out.print("base case: " + bestValue.score + " Team: " + team);
            System.out.println(" Reds: " + node.getRedUnits().size() + " Whites: " + node.getWhiteUnits().size());
        }
    }

    private void debugInitialState(int depth, MoveAndScore best, boolean maximisingPlayer, Move move, int teamNumber, double alpha, double beta) {
        if (Game.VERBOSE_OUTPUT) {
            String player = maximisingPlayer ? " -Max " : " -Min ";
            Team team = getMaximisingPlayerTeam(maximisingPlayer);
            MoveType moveType = move.getResult().getType();
            String moveOrigin = (move.getOrigin().x + 1) + ", " + (move.getOrigin().y + 1) + " -> ";
            String moveTarget = (move.getTarget().x + 1) + ", " + (move.getTarget().y + 1);

                    debugDepth(depth);
            System.out.print("Init" + player + team + " " + teamNumber + " Best: " + best.score + " Alpha: " + alpha + " Beta: " + beta + " " + moveType + " " + moveOrigin + moveTarget + "\n");
        }
    }

    private void debugFinalState(int depth, MoveAndScore best, boolean maximisingPlayer, double alpha, double beta) {
        if (Game.VERBOSE_OUTPUT) {
            String player = maximisingPlayer ? " -Max " : " -Min ";
            Team team = getMaximisingPlayerTeam(maximisingPlayer);

                    debugDepth(depth);
            System.out.print("fin" + player + team + " Best: " + best.score + " Alpha: " + alpha + " Beta: " + beta + "\n");
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
        MoveAndScore inverted = new MoveAndScore(value.move, value.score);
        inverted.negateScore();
        return inverted;
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

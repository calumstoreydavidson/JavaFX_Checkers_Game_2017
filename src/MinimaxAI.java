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
        System.out.println("");
        return Optional.of(minimax(sim, 0, true).move);
    }

    private MoveAndScore minimax(BoardSim node, int depth, boolean maximisingPlayer) {
        MoveAndScore best;
        if (node.getTeamsPossibleMoves().isEmpty() || depth == maxDepth) {
            best = new MoveAndScore(null, node.evaluateState(playerTeam));
            debugBaseCase(depth, best);

        } else if (maximisingPlayer) {
            best = new MoveAndScore(null, Integer.MIN_VALUE);
            for (Move move : node.getTeamsPossibleMoves()) {
                debugInitialState(depth, best, maximisingPlayer, move);//----------------------------------------------------------------------------------------------
                MoveAndScore score = minimax(node.getChild(move), depth + 1, false);
                score.move = move;
                best = getMax(best, score);
                debugFinalState(depth, best, maximisingPlayer);//------------------------------------------------------------------------------------------------------
            }

        } else {
            best = new MoveAndScore(null, Integer.MAX_VALUE);
            for (Move move : node.getTeamsPossibleMoves()) {
                debugInitialState(depth, best, maximisingPlayer, move);//----------------------------------------------------------------------------------------------
                MoveAndScore childValue = minimax(node.getChild(move), depth + 1, true);
                childValue.move = move;
                best = getMin(best, childValue);
                debugFinalState(depth, best, maximisingPlayer);//------------------------------------------------------------------------------------------------------
            }
        }
        return best;
    }

    private void debugBaseCase(int depth, MoveAndScore bestValue) {
        debugDepth(depth);
        System.out.println("base case: " + bestValue.score);
//        System.out.println("Reds: " + node.getRedUnits().size() + " -Whites: " + node.getWhiteUnits().size());
    }

    private void debugInitialState(int depth, MoveAndScore best, boolean maximisingPlayer, Move move) {
        String player = maximisingPlayer ? " -Max " : " -Min ";
        Team team = getMaximisingPlayerTeam(maximisingPlayer);
        MoveType moveType = move.getResult().getType();
        String moveOrigin = (move.getOrigin().x + 1) + ", " + (move.getOrigin().y + 1) + " -> ";
        String moveTarget = (move.getTarget().x + 1) + ", " + (move.getTarget().y + 1);

        debugDepth(depth);
        System.out.print("init" + player + team + (" Best: " + best.score) + " " + moveType + " " + moveOrigin + moveTarget + "\n");
    }

    private void debugFinalState(int depth, MoveAndScore best, boolean maximisingPlayer) {
        String player = maximisingPlayer ? " -Max " : " -Min ";
        Team team = getMaximisingPlayerTeam(maximisingPlayer);

        debugDepth(depth);
        System.out.print("fin" + player + team + (" Best: " + best.score) + "\n");
    }

    private void debugDepth(int depth) {
        for(int i = 1; i<=depth;i++){
            System.out.print(" | ");
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

    @Override public void resetPlayer() {
        isPlayersTurn = playerTeam == Team.RED;
    }

    private Team getMaximisingPlayerTeam(boolean maximisingPlayer){
        if (playerTeam == Team.RED){
            return maximisingPlayer ? Team.RED : Team.WHITE;
        }else {
            return maximisingPlayer ? Team.WHITE : Team.RED;
        }
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

import java.util.Optional;

public class ABMinimaxAI implements Player {

    boolean isPlayerHuman;
    private boolean isPlayersTurn;
    private Team playerTeam;
    private int maxDepth = 3;

    public ABMinimaxAI(Team playerTeam) {
        this.playerTeam = playerTeam;
        //red always goes first
        isPlayersTurn = playerTeam == Team.RED;
        this.isPlayerHuman = false;
    }

    @Override public Optional<Move> getPlayerMove(Board board){
        MoveAndScore alpha = new MoveAndScore(null,Integer.MIN_VALUE);
        MoveAndScore beta = new MoveAndScore(null,Integer.MAX_VALUE);
        BoardSim sim = new BoardSim(board, playerTeam);

        return Optional.of(minimax(sim, 0, alpha, beta, true).move);
    }
//
//    public static int minimax(Board board, int depth, Team player, int alpha, int beta) {
//        ArrayList<Move> possibleMoves = game.getPossibleMoves();
//        int bestMove;
//        if (player == 1) {
//            bestMove = -10;
//        } else {
//            bestMove = 10;
//        }
//
//        //int bestScoreMax = Integer.MIN_VALUE; // initialise MAX worst case score
//        //int bestScoreMin = Integer.MAX_VALUE; // initialise MIN worst case score
//        // Terminal test
//        if (player == 1 && (sim_pile == 0 || playerStuck(mmAIhistory))) {
//            return -1;
//        }
//        if (player == 2 && (sim_pile == 0 || playerStuck(mmHUMANhistory))) {
//            return +1;
//        }
//
//        // A list to store pairs of takes and scores in
//        ArrayList<Integer> availableTakes = getAvailableTakes(player);
//        // Clear successor evaluations after each completed playthrough
//        if (depth == 0) {
//            successorEvaluations.clear();
//        }
//
//        for (int i = 0; i < availableTakes.size(); i++) {
//
//            int take = availableTakes.get(i);
//            int currentScore = 0;
//
//            if (player == 1) { // get the highest result returned by minimax
//                mmAIhistory[take] = 1;
//                sim_pile -= take;
//                currentScore = minimax(depth + 1, 2, alpha, beta);
//                bestMove = Math.max(bestMove, currentScore);
//                alpha = Math.max(currentScore, alpha);
//            } else if (player == 2) {// AI turn: get the lowest result returned by minimax
//                mmHUMANhistory[take] = 1;
//                sim_pile -= take;
//
//                currentScore = minimax(depth + 1, 1, alpha, beta);
//                bestMove = Math.min(bestMove, currentScore);
//                beta = Math.min(currentScore, beta);
//            }
//            if (depth == 0 && bestMove.score < currentScore) {
//                bestMove = new TakesAndScores(currentScore, take);
//
//            }
//            // reset changes
//            sim_pile += take;
//            if (player == 1) {
//                mmAIhistory[take] = 0;
//            } else {
//                mmHUMANhistory[take] = 0;
//            }
//            if (alpha >= beta) {
//                break;
//            }
//        }
//
//        return bestMove;
//    }
//
//    public int getHeuristic(Game game){
//        if(game.getCurrentPlayer().getPlayerTeam() == Team.WHITE)
//            return game.getPlayerUnits(Team.WHITE).size() - game.getPlayerUnits(Team.RED).size();
//        return game.getPlayerUnits(Team.RED).size() - game.getPlayerUnits(Team.WHITE).size();
//    }

//    public Move minimax(Board board, int depth, Team team, int alpha, int beta) {
//    public Move minimax(Board board, int depth, Team team) {
//        return maxMove(board, 10, team);
//    }
//
//    Move maxMove(Board board, int depth, Team team) {
//        if (board.getPossibleMoves().isEmpty()) {
//            return board.evaluateState(team);
//        } else {
//            Move best_move = null;
//            ArrayList<Move> moves = board.getPossibleMoves();
//            for (Move possibleMove : moves) {
//                board.executeMove(possibleMove);
//                Move move = minMove(getClone(board));
//                if (Value(move) > Value(best_move)) {
//                    best_move = move;
//                }
//            }
//            return best_move;
//        }
//    }
//
//    Move minMove(Board board, int depth, Team team) {
//        Move best_move = null;
//        ArrayList<Move> moves = board.getPossibleMoves();
//        for (Move possibleMove : moves) {
//            board.executeMove(possibleMove);
//            Move move = maxMove(getClone(board));
//            if (Value(move) > Value(best_move)) {
//                best_move = move;
//            }
//        }
//
//        return best_move;
//    }
//    private Move minimaxBase(Board node, int depth, int alpha, int beta, boolean maximisingPlayer){
//        ArrayList<Integer> MoveScores = new ArrayList<>();
//        for (Move move : node.getPossibleMoves()) {
//
//        }
//    }

//    private int minimax(Board node, int depth, int alpha, int beta, boolean maximisingPlayer) {
//        int bestValue;
//        if (node.getPossibleMoves().isEmpty() || depth == maxDepth) {
//            bestValue = node.evaluateState(getMaximisingPlayerTeam(maximisingPlayer));
//        } else if (maximisingPlayer) {
//            bestValue = alpha;
//
//            // Recurse for all children of node.
//            for (Move move : node.getPossibleMoves()) {
//                int childValue = minimax(getChild(node, move), depth + 1, bestValue, beta, false);
//                bestValue = Math.max(bestValue, childValue);
//                if (beta <= bestValue) {
//                    break;
//                }
//            }
//        } else {
//            bestValue = beta;
//
//            // Recurse for all children of node.
//            for (Move move : node.getPossibleMoves()) {
//                int childValue = minimax(getChild(node, move), depth + 1, alpha, bestValue, true);
//                bestValue = Math.min(bestValue, childValue);
//                if (bestValue <= alpha) {
//                    break;
//                }
//            }
//        }
//        return bestValue;
//    }

    private MoveAndScore minimax(BoardSim node, int depth, MoveAndScore alpha, MoveAndScore beta, boolean maximisingPlayer) {
        MoveAndScore best;
        if (node.getTeamsPossibleMoves().isEmpty() || depth == maxDepth) {
            best = new MoveAndScore(null, node.evaluateState(playerTeam));
            debugBaseCase(node, depth, best);
        } else if (maximisingPlayer) {
            best = alpha;

            // Recurse for all children of node.
            for (Move move : node.getTeamsPossibleMoves()) {
                System.out.println("");
//                node.outputSimBoard(depth);//-----------------------------------------------
                debugInitialState(depth, best, beta, maximisingPlayer, move);//----------------------------------------------------------------------------------------------
                MoveAndScore child = minimax(node.getChild(move), depth + 1, best, beta, false);
                child.move = move;
                debugAlphaUpdate(depth, best, child);//------------------------------------------------------------------------------------------------------------------
                best = Math.max(best.score, child.score) == best.score ? best : child;
                debugFinalState(depth, best, beta, maximisingPlayer);//------------------------------------------------------------------------------------------------------
                if (beta.score <= best.score) {
                    debugPruning(depth);
                    break;
                }
            }
        } else {
            best = beta;

            // Recurse for all children of node.
            for (Move move : node.getTeamsPossibleMoves()) {
                System.out.println("");
//                node.outputSimBoard(depth);//-----------------------------------------------
                debugInitialState(depth, alpha, best, maximisingPlayer, move);//----------------------------------------------------------------------------------------------
                MoveAndScore child = minimax(node.getChild(move), depth + 1, alpha, best, true);
                child.move = move;
                debugBetaUpdate(depth, best, child);//-------------------------------------------------------------------------------------------------------------------
                best = Math.min(best.score, child.score) == best.score ? best : child;
                debugFinalState(depth, alpha, best, maximisingPlayer);//------------------------------------------------------------------------------------------------------
                if (best.score <= alpha.score) {
                    debugPruning(depth);
                    break;
                }
            }
        }
        return best;
    }

    private void debugBaseCase(BoardSim node, int depth, MoveAndScore bestValue) {
        debugDepth(depth);
        System.out.println("base case: " + bestValue.score);
//        System.out.println("Reds: " + node.getRedUnits().size() + " -Whites: " + node.getWhiteUnits().size());
    }

    private void debugInitialState(int depth, MoveAndScore alpha, MoveAndScore beta, boolean maximisingPlayer, Move move) {
        String player = maximisingPlayer ? " -Max " : " -Min ";
        Team team = getMaximisingPlayerTeam(maximisingPlayer);
        MoveType moveType = move.getResult().getType();
        String moveOrigin = "";//(move.getOrigin().x + 1) + ", " + (move.getOrigin().y + 1) + " -> ";
        String moveTarget = "";//(move.getTarget().x + 1) + ", " + (move.getTarget().y + 1);
        String alphaOutput = " ALPHA: " + alpha.score;
        String betaOutput = " BETA: " + beta.score;

        debugDepth(depth);
        System.out.print("init" + player + team + alphaOutput + betaOutput + " " + moveType + " " + moveOrigin + moveTarget + "\n");
    }

    private void debugAlphaUpdate(int depth, MoveAndScore alpha, MoveAndScore child) {
        String childOutput = "Child: " + child.score;
        String bestOutput = "Alpha: " + alpha.score;

        debugDepth(depth);
        System.out.println("Alpha = M.Max(" + childOutput +" "+ bestOutput + ")");
    }

    private void debugBetaUpdate(int depth, MoveAndScore beta, MoveAndScore child) {
        String childOutput = "Child: " + child.score;
        String bestOutput = "Beta: " + beta.score;

        debugDepth(depth);
        System.out.println("Beta = M.Min(" + childOutput +" "+ bestOutput + ")");
    }

    private void debugFinalState(int depth, MoveAndScore alpha, MoveAndScore beta, boolean maximisingPlayer) {
        String player = maximisingPlayer ? " -Max " : " -Min ";
        Team team = getMaximisingPlayerTeam(maximisingPlayer);
        String alphaOutput = " ALPHA: " + alpha.score;
        String betaOutput = " BETA: " + beta.score;

        debugDepth(depth);
        System.out.print("fin" + player + team + alphaOutput + betaOutput + "\n");
    }

    private void debugPruning(int depth) {
        debugDepth(depth);
        System.out.println("PRUNING OCCURRED");
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

    public void setPlayerTeam(Team playerTeam) {
        this.playerTeam = playerTeam;
    }

    private Team getMaximisingPlayerTeam(boolean maximisingPlayer){
        if (playerTeam == Team.RED){
            return maximisingPlayer ? Team.RED : Team.WHITE;
        }else {
            return maximisingPlayer ? Team.WHITE : Team.RED;
        }
    }

//    private Board getChild(Board board, Move move){
//        Board childNode = getClone(board);
//        childNode.executeMove(move);
//        return childNode;
//    }

//    public Board getClone(Board board){
//        Board simBoard = (Board) deepClone(board);
//        simBoard.setSimulation();
//        return simBoard;
//    }

    private MoveAndScore getMax(MoveAndScore best, MoveAndScore child){
        return Math.max(best.score, child.score) == best.score ? best : child;

//        return child.score >= best.score ? child : best;
    }

    private MoveAndScore getMin(MoveAndScore best, MoveAndScore child){
        return Math.min(best.score, child.score) == best.score ? best : child;
//        return child.score <= best.score ? child : best;
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

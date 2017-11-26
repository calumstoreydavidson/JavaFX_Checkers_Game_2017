//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.util.Optional;
//
//public class ABMinimaxAI implements Player {
//
//    boolean isPlayerHuman;
//    private boolean isPlayersTurn;
//    private Team playerTeam;
//    private int maxDepth = 10;
//
//    public ABMinimaxAI(Team playerTeam) {
//        this.playerTeam = playerTeam;
//        //red always goes first
//        isPlayersTurn = playerTeam == Team.RED;
//        this.isPlayerHuman = false;
//    }
//
//    @Override public Optional<Move> getPlayerMove(Board board){
//        MoveAndScore alpha = new MoveAndScore(null,0);
//        MoveAndScore beta = new MoveAndScore(null,0);
//        return Optional.of(minimax(board, 1, alpha, beta, true).move);
//    }
////
////    public static int minimax(Board board, int depth, Team player, int alpha, int beta) {
////        ArrayList<Move> possibleMoves = game.getPossibleMoves();
////        int bestMove;
////        if (player == 1) {
////            bestMove = -10;
////        } else {
////            bestMove = 10;
////        }
////
////        //int bestScoreMax = Integer.MIN_VALUE; // initialise MAX worst case score
////        //int bestScoreMin = Integer.MAX_VALUE; // initialise MIN worst case score
////        // Terminal test
////        if (player == 1 && (sim_pile == 0 || playerStuck(mmAIhistory))) {
////            return -1;
////        }
////        if (player == 2 && (sim_pile == 0 || playerStuck(mmHUMANhistory))) {
////            return +1;
////        }
////
////        // A list to store pairs of takes and scores in
////        ArrayList<Integer> availableTakes = getAvailableTakes(player);
////        // Clear successor evaluations after each completed playthrough
////        if (depth == 0) {
////            successorEvaluations.clear();
////        }
////
////        for (int i = 0; i < availableTakes.size(); i++) {
////
////            int take = availableTakes.get(i);
////            int currentScore = 0;
////
////            if (player == 1) { // get the highest result returned by minimax
////                mmAIhistory[take] = 1;
////                sim_pile -= take;
////                currentScore = minimax(depth + 1, 2, alpha, beta);
////                bestMove = Math.max(bestMove, currentScore);
////                alpha = Math.max(currentScore, alpha);
////            } else if (player == 2) {// AI turn: get the lowest result returned by minimax
////                mmHUMANhistory[take] = 1;
////                sim_pile -= take;
////
////                currentScore = minimax(depth + 1, 1, alpha, beta);
////                bestMove = Math.min(bestMove, currentScore);
////                beta = Math.min(currentScore, beta);
////            }
////            if (depth == 0 && bestMove.score < currentScore) {
////                bestMove = new TakesAndScores(currentScore, take);
////
////            }
////            // reset changes
////            sim_pile += take;
////            if (player == 1) {
////                mmAIhistory[take] = 0;
////            } else {
////                mmHUMANhistory[take] = 0;
////            }
////            if (alpha >= beta) {
////                break;
////            }
////        }
////
////        return bestMove;
////    }
////
////    public int getHeuristic(Game game){
////        if(game.getCurrentPlayer().getPlayerTeam() == Team.WHITE)
////            return game.getPlayerUnits(Team.WHITE).size() - game.getPlayerUnits(Team.RED).size();
////        return game.getPlayerUnits(Team.RED).size() - game.getPlayerUnits(Team.WHITE).size();
////    }
//
////    public Move minimax(Board board, int depth, Team team, int alpha, int beta) {
////    public Move minimax(Board board, int depth, Team team) {
////        return maxMove(board, 10, team);
////    }
////
////    Move maxMove(Board board, int depth, Team team) {
////        if (board.getPossibleMoves().isEmpty()) {
////            return board.evaluateState(team);
////        } else {
////            Move best_move = null;
////            ArrayList<Move> moves = board.getPossibleMoves();
////            for (Move possibleMove : moves) {
////                board.executeMove(possibleMove);
////                Move move = minMove(getClone(board));
////                if (Value(move) > Value(best_move)) {
////                    best_move = move;
////                }
////            }
////            return best_move;
////        }
////    }
////
////    Move minMove(Board board, int depth, Team team) {
////        Move best_move = null;
////        ArrayList<Move> moves = board.getPossibleMoves();
////        for (Move possibleMove : moves) {
////            board.executeMove(possibleMove);
////            Move move = maxMove(getClone(board));
////            if (Value(move) > Value(best_move)) {
////                best_move = move;
////            }
////        }
////
////        return best_move;
////    }
////    private Move minimaxBase(Board node, int depth, int alpha, int beta, boolean maximisingPlayer){
////        ArrayList<Integer> MoveScores = new ArrayList<>();
////        for (Move move : node.getPossibleMoves()) {
////
////        }
////    }
//
////    private int minimax(Board node, int depth, int alpha, int beta, boolean maximisingPlayer) {
////        int bestValue;
////        if (node.getPossibleMoves().isEmpty() || depth == maxDepth) {
////            bestValue = node.evaluateState(getMaximisingPlayerTeam(maximisingPlayer));
////        } else if (maximisingPlayer) {
////            bestValue = alpha;
////
////            // Recurse for all children of node.
////            for (Move move : node.getPossibleMoves()) {
////                int childValue = minimax(getChild(node, move), depth + 1, bestValue, beta, false);
////                bestValue = Math.max(bestValue, childValue);
////                if (beta <= bestValue) {
////                    break;
////                }
////            }
////        } else {
////            bestValue = beta;
////
////            // Recurse for all children of node.
////            for (Move move : node.getPossibleMoves()) {
////                int childValue = minimax(getChild(node, move), depth + 1, alpha, bestValue, true);
////                bestValue = Math.min(bestValue, childValue);
////                if (bestValue <= alpha) {
////                    break;
////                }
////            }
////        }
////        return bestValue;
////    }
//
//    private MoveAndScore minimax(Board node, int depth, MoveAndScore alpha, MoveAndScore beta, boolean maximisingPlayer) {
//        MoveAndScore bestValue;
//        if (node.getPossibleMoves().isEmpty() || depth == maxDepth) {
//            bestValue = new MoveAndScore(null, node.evaluateState(getMaximisingPlayerTeam(maximisingPlayer)));
//        } else if (maximisingPlayer) {
//            bestValue = alpha;
//
//            // Recurse for all children of node.
//            for (Move move : node.getPossibleMoves()) {
//                MoveAndScore childValue = minimax(getChild(node, move), depth + 1, bestValue, beta, false);
//                childValue.move = move;
//                bestValue = getMax(bestValue, childValue);
//                if (beta.score <= bestValue.score) {
//                    break;
//                }
//            }
//        } else {
//            bestValue = beta;
//
//            // Recurse for all children of node.
//            for (Move move : node.getPossibleMoves()) {
//                MoveAndScore childValue = minimax(getChild(node, move), depth + 1, alpha, bestValue, true);
//                childValue.move = move;
//                bestValue = getMin(bestValue, childValue);
//                if (bestValue.score <= alpha.score) {
//                    break;
//                }
//            }
//        }
//        return bestValue;
//    }
//
//    public boolean isPlayerHuman() {
//        return isPlayerHuman;
//    }
//
//    public boolean isPlayersTurn() {
//        return isPlayersTurn;
//    }
//
//    public void switchTurn() {
//        isPlayersTurn = !isPlayersTurn;
//    }
//
//    public Team getPlayerTeam() {
//        return playerTeam;
//    }
//
//    @Override public void resetPlayer() {
//        isPlayersTurn = playerTeam == Team.RED;
//    }
//
//    public void setPlayerTeam(Team playerTeam) {
//        this.playerTeam = playerTeam;
//    }
//
//    private Team getMaximisingPlayerTeam(boolean maximisingPlayer){
//        if (playerTeam == Team.RED){
//            return maximisingPlayer ? Team.RED : Team.WHITE;
//        }else {
//            return maximisingPlayer ? Team.WHITE : Team.RED;
//        }
//    }
//
////    private Board getChild(Board board, Move move){
////        Board childNode = getClone(board);
////        childNode.executeMove(move);
////        return childNode;
////    }
//
////    public Board getClone(Board board){
////        Board simBoard = (Board) deepClone(board);
////        simBoard.setSimulation();
////        return simBoard;
////    }
//
//    private MoveAndScore getMax(MoveAndScore best, MoveAndScore child){
//        return best.score > child.score ? best : child;
//    }
//
//    private MoveAndScore getMin(MoveAndScore best, MoveAndScore child){
//        return best.score < child.score ? best : child;
//    }
//
//    /**
//     * This method makes a "deep clone" of any Java object it is given.
//     */
//    public static Object deepClone(Object object) {
//        try {
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ObjectOutputStream oos = new ObjectOutputStream(baos);
//            oos.writeObject(object);
//            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
//            ObjectInputStream ois = new ObjectInputStream(bais);
//            return ois.readObject();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    class MoveAndScore {
//        Move move;
//        int score;
//
//        MoveAndScore(Move move, int score) {
//            this.move = move;
//            this.score = score;
//        }
//    }
//}

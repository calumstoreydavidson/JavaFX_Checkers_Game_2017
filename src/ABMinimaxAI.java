import java.util.ArrayList;
import java.util.List;

public class ABMinimaxAI implements Player {

    boolean isPlayerHuman;
    private boolean isPlayersTurn;
    private Team playerTeam;
    static MovesAndScores bestMove;
    static List<MovesAndScores> successorEvaluations;

    public ABMinimaxAI(Team playerTeam) {
        this.playerTeam = playerTeam;
        //red always goes first
        isPlayersTurn = playerTeam == Team.RED;
        this.isPlayerHuman = false;
    }

    @Override public Move getPlayerMove(Game game) {
        successorEvaluations = new ArrayList<MovesAndScores>();
        return minimax(game, 5, game.getCurrentPlayer().getPlayerTeam(), Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

//    public static int minimax(Game game, int depth, Team team, int alpha, int beta) {
//        int bestScore;
//        if (team == Team.RED) {
//            bestScore = -10;
//        } else {
//            bestScore = 10;
//        }
//
//        //int bestScoreMax = Integer.MIN_VALUE; // initialise MAX worst case score
//        //int bestScoreMin = Integer.MAX_VALUE; // initialise MIN worst case score
//        // Terminal test
//        if (team == Team.RED && game.isGameOver()) {
//            return -1;
//        }
//        if (team == Team.WHITE && game.isGameOver()) {
//            return +1;
//        }
//
//        // A list to store pairs of takes and scores in
//        ArrayList<Move> availableMoves = game.getTeamMoves(team);
//        // Clear successor evaluations after each completed playthrough
//        if (depth == 0) {
//            successorEvaluations.clear();
//        }
//
//        for (int i = 0; i < availableMoves.size(); i++) {
//
//            int take = availableMoves.get(i);
//            int currentScore = 0;
//
//            if (team == Team.RED) { // get the highest result returned by minimax
//                mmAIhistory[take] = 1;
//                sim_pile -= take;
//                currentScore = minimax(depth + 1, Team.WHITE, alpha, beta);
//                bestScore = Math.max(bestScore, currentScore);
//                alpha = Math.max(currentScore, alpha);
//            } else if (team == Team.WHITE) {// AI turn: get the lowest result returned by minimax
//                mmHUMANhistory[take] = 1;
//                sim_pile -= take;
//
//                currentScore = minimax(depth + 1, Team.RED, alpha, beta);
//                bestScore = Math.min(bestScore, currentScore);
//                beta = Math.min(currentScore, beta);
//            }
//            if (depth == 0 && bestMove.score < currentScore) {
//                bestMove = new MovesAndScores(currentScore, take);
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
//        return bestScore;
//    }

    public Move minimax(Game game, int depth, Team team, int alpha, int beta) {
        return maxMove(game);

    }

    Move maxMove(Game game) {
        if (game.isGameOver()){
            return EvalGameState(game);
        }
        else {
           Move best_move = null;
            ArrayList<Move> moves = game.getPossibleMoves();
            for (Move possibleMove : moves){
                game.executeMove(possibleMove);
                Move move = minMove(new Game(game));
                if (Value(move) > Value(best_move)) {
                    best_move = move;
                }
            }
            return best_move;
        }
    }
    
    Move minMove(Game game) {
        Move best_move = null;
        moves = game.getPossibleMoves();
        for(Move possibleMove: moves) {
            game.executeMove(possibleMove);
            Move move = maxMove(new Game(game));
            if (Value(move) > Value(best_move)) {
                best_move = move;
            }
        }

        return best_move;
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

    public void setPlayerTeam(Team playerTeam) {
        this.playerTeam = playerTeam;
    }

    class MovesAndScores {
        int score;
        Move move;

        MovesAndScores(int score, Move move) {
            this.score = score;
            this.move = move;
        }
    }
}

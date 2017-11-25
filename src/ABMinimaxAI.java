import java.util.ArrayList;

import com.sun.org.apache.bcel.internal.generic.RET;

public class ABMinimaxAI implements Player {

    boolean isPlayerHuman;
    private boolean isPlayersTurn;
    private Team playerTeam;
    private int depth = 5;

    public ABMinimaxAI(Team playerTeam) {
        this.playerTeam = playerTeam;
        //red always goes first
        isPlayersTurn = playerTeam == Team.RED;
        this.isPlayerHuman = false;
    }

    @Override public Move getPlayerMove(Board board){
        return minimax(board, depth, playerTeam, 0, 0);
    }

    public static int minimax(Board board, int depth, Team player, int alpha, int beta) {
        ArrayList<Move> possibleMoves = game.getPossibleMoves();
        int bestMove;
        if (player == 1) {
            bestMove = -10;
        } else {
            bestMove = 10;
        }

        //int bestScoreMax = Integer.MIN_VALUE; // initialise MAX worst case score
        //int bestScoreMin = Integer.MAX_VALUE; // initialise MIN worst case score
        // Terminal test
        if (player == 1 && (sim_pile == 0 || playerStuck(mmAIhistory))) {
            return -1;
        }
        if (player == 2 && (sim_pile == 0 || playerStuck(mmHUMANhistory))) {
            return +1;
        }

        // A list to store pairs of takes and scores in
        ArrayList<Integer> availableTakes = getAvailableTakes(player);
        // Clear successor evaluations after each completed playthrough
        if (depth == 0) {
            successorEvaluations.clear();
        }

        for (int i = 0; i < availableTakes.size(); i++) {

            int take = availableTakes.get(i);
            int currentScore = 0;

            if (player == 1) { // get the highest result returned by minimax
                mmAIhistory[take] = 1;
                sim_pile -= take;
                currentScore = minimax(depth + 1, 2, alpha, beta);
                bestMove = Math.max(bestMove, currentScore);
                alpha = Math.max(currentScore, alpha);
            } else if (player == 2) {// AI turn: get the lowest result returned by minimax
                mmHUMANhistory[take] = 1;
                sim_pile -= take;

                currentScore = minimax(depth + 1, 1, alpha, beta);
                bestMove = Math.min(bestMove, currentScore);
                beta = Math.min(currentScore, beta);
            }
            if (depth == 0 && bestMove.score < currentScore) {
                bestMove = new TakesAndScores(currentScore, take);

            }
            // reset changes
            sim_pile += take;
            if (player == 1) {
                mmAIhistory[take] = 0;
            } else {
                mmHUMANhistory[take] = 0;
            }
            if (alpha >= beta) {
                break;
            }
        }

        return bestMove;
    }

    public int getHeuristic(Game game){
        if(game.getCurrentPlayer().getPlayerTeam() == Team.WHITE)
            return game.getPlayerUnits(Team.WHITE).size() - game.getPlayerUnits(Team.RED).size();
        return game.getPlayerUnits(Team.RED).size() - game.getPlayerUnits(Team.WHITE).size();
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
}

import java.util.Optional;

/**
 * Represents an AI player operating a Negamax(simplified minimax) with alpha beta pruning decision process for selecting optimal moves
 */
public class ABNegamaxAI extends Player {

    /**
     * creates an ABNegamx AI player with the specified team
     *
     * @param playerTeam the players team
     */
    public ABNegamaxAI(Team playerTeam) {
        setPlayerTeam(playerTeam);
        resetPlayer();
        setPlayerType(PlayerType.AI);
    }

    /**
     * gets the players move
     *
     * @param displayBoard the current base state of the game board to be processed
     * @return the move the player wishes to make this turn
     */
    @Override public Optional<Move> getPlayerMove(DisplayBoard displayBoard) {
        if (Game.VERBOSE_OUTPUT) {
            GUI.output.appendText("AI is thinking \n");
        }
        double alpha = Integer.MIN_VALUE;
        double beta = Integer.MAX_VALUE;
        SimulationBoard sim = new SimulationBoard(displayBoard, getPlayerTeam());
        
        return Optional.of(negamax(sim, 0, 1, alpha, beta).move);
    }

    /**
     * runs Negamax with alpha beta pruning to select the ideal move for the current turn
     *
     * @param node  the current simulatedBoard state in the Negamax process
     * @param depth the current recursive depth of the negamax run
     * @param team  the minimising of maximising player in the negamax process
     * @param alpha the maximising players best found value so far
     * @param beta  the minimising players best found value so far
     * @return the move the player wishes to make this turn
     */
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

    /**
     * invert the score of the specified MoveAndScore as is necessary in negamax while ensuring that existing objects
     * are not altered to prevent bugs
     *
     * @param value the specified MoveAndScore containing an associated move and score value pair
     * @return a new inverted copy of the provided MoveAndScore for use in negamax
     */
    public MoveAndScore negate(MoveAndScore value) {
        MoveAndScore inverted = new MoveAndScore(value.move, value.score);
        inverted.negateScore();
        return inverted;
    }

    /**
     * to avoid having to do complex things with changing GUI sliders as players are changed, get the proportional max
     * depth before lag is incurred relative to standard negamax
     *
     * @return the proportional max depth of the algorithm based on the users specified value from the GUI slider
     */
    private int getMaxSearchDepth() {
        return (int) ((double) Game.AI_MAX_SEARCH_DEPTH * 1.7);//get 1.4 of 1..8 then round it to an int
    }
}

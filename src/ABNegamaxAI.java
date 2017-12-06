import java.util.Optional;

/**
 * Represents an AI player operating a Negamax(simplified Minimax) with alpha beta pruning decision process for selecting optimal moves
 */
public class ABNegamaxAI extends Player {

    /**
     * creates an ABNegamax AI player with the specified team
     *
     * @param playerTeam the players team
     */
    public ABNegamaxAI(Team playerTeam, PlayerType playerType) {
        setPlayerTeam(playerTeam);
        resetPlayer();
        setPlayerType(playerType);
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

    //TODO - it would be nice to implement a Negascout player - given decent move sorting
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
    private MoveAndScore negamax(SimulationBoard node, int depth, int team, double alpha, double beta) {//TODO implementing transposition tables could further improve performance
        if (node.getTeamsPossibleMoves().isEmpty() || depth == getSelectedSearchDepth()) { //check if node is a leaf
            return new MoveAndScore(null, node.evaluateState());
        }
        MoveAndScore max = new MoveAndScore(null, Integer.MIN_VALUE);
        //TODO adding move sorting here would make the pruning even more effective

        for (Move move : node.getTeamsPossibleMoves()) { //for each possible move available to the current player
            MoveAndScore child = negate(negamax(node.getChild(move), depth + 1, -team, -beta, -alpha));
            child.move = move;// associate score and move
            max = child.score > max.score ? child : max;
            alpha = max.score > alpha ? max.score : alpha;
            if (beta <= alpha) { //when continuing to evaluate child nodes is pointless
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
    private MoveAndScore negate(MoveAndScore value) {
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
    private int getSelectedSearchDepth() {
        if(Game.USERS_AI_ADVISOR){
            return (int) ((double) Game.AI_MAX_SEARCH_DEPTH * 1.7) - 1;//-1 ensures the game runs at a reasonable speed, by lowering difficulty of AI and thus processing
        }else{
            return (int) ((double) Game.AI_MAX_SEARCH_DEPTH * 1.7);//get 1.4 of 1..8 then round it to an int
        }
    }
}

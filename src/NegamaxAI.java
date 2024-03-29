import java.util.Optional;

/**
 * Represents an AI player operating a Negamax(simplified Minimax) decision process for selecting optimal moves
 */
public class NegamaxAI extends Player {

    /**
     * creates an Negamax AI player with the specified team
     *
     * @param playerTeam the players team
     */
    public NegamaxAI(Team playerTeam) {
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
        SimulationBoard sim = new SimulationBoard(displayBoard, getPlayerTeam());
        return Optional.of(negamax(sim, 0, 1).move);
    }

    /**
     * runs Negamax to select the ideal move for the current turn
     *
     * @param node  the current simulatedBoard state in the Negamax process
     * @param depth the current recursive depth of the negamax run
     * @param team  the minimising of maximising player in the negamax process
     * @return the move the player wishes to make this turn
     */
    private MoveAndScore negamax(SimulationBoard node, int depth, int team) {
        if (node.getTeamsPossibleMoves().isEmpty() || depth == getSelectedSearchDepth()) {
            return new MoveAndScore(null, node.evaluateState());
        }
        MoveAndScore max = new MoveAndScore(null, Integer.MIN_VALUE);

        //for all moves
        for (Move move : node.getTeamsPossibleMoves()) {
            MoveAndScore score = negate(negamax(node.getChild(move), depth + 1, -team));
            score.move = move;
            max = score.score > max.score ? score : max;
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
        value.negateScore();
        return value;
    }//TODO create NEGAMAX AI abstract class

    /**
     * get the max depth allowed for the AI
     *
     * @return the max depth allowed for the AI
     */
    private int getSelectedSearchDepth() {
        return Game.AI_MAX_SEARCH_DEPTH;
    }

}

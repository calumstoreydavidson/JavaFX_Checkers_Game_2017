import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

/**
 * Represents an AI player operating a random decision process for selecting moves
 */
public class RandomAIPlayer extends Player {

    //the random value generator to be used by this player
    private final Random rand = new Random();

    /**
     * creates an Negamax AI player with the specified team
     *
     * @param playerTeam the players team
     */
    public RandomAIPlayer(Team playerTeam) {
        setPlayerTeam(playerTeam);
        //red always goes first
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

        ArrayList<Move> possibleMoves = displayBoard.getPossibleMoves();
        int r = rand.nextInt(possibleMoves.size());
        return Optional.of(possibleMoves.get(r));
    }

}

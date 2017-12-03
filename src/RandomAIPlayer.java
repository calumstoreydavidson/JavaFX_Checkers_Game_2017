import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

public class RandomAIPlayer extends Player {

    private Random rand = new Random();

    public RandomAIPlayer(Team playerTeam) {
        setPlayerTeam(playerTeam);
        //red always goes first
        resetPlayer();
        setPlayerHuman(false);
    }

    @Override
    public Optional<Move> getPlayerMove(DisplayBoard displayBoard) {
        if (Game.VERBOSE_OUTPUT) {
            Main.output.appendText("AI is thinking \n");
        }

        ArrayList<Move> possibleMoves = displayBoard.getPossibleMoves();
        int r = rand.nextInt(possibleMoves.size());
        return Optional.of(possibleMoves.get(r));
    }

}

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

public class RandomAIPlayer implements Player {

    boolean isPlayerHuman;
    private Random rand = new Random();
    private boolean isPlayersTurn;
    private Team playerTeam;

    public RandomAIPlayer(Team playerTeam) {
        this.playerTeam = playerTeam;
        //red always goes first
        resetPlayer();
        this.isPlayerHuman = false;
    }

    @Override public Optional<Move> getPlayerMove(Board board) {
        if (Game.VERBOSE_OUTPUT) {
            Main.output.appendText("AI is thinking \n");
        }

        ArrayList<Move> possibleMoves = board.getPossibleMoves();
        int r = rand.nextInt(possibleMoves.size());
        return Optional.of(possibleMoves.get(r));
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

    public void resetPlayer() {
        isPlayersTurn = playerTeam == Team.RED;
    }
}

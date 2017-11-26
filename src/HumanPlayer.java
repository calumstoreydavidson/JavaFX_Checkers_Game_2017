import java.util.ArrayList;
import java.util.Optional;

public class HumanPlayer implements Player {

    private boolean isPlayerHuman;
    private boolean isPlayersTurn;
    private Team playerTeam;

    public HumanPlayer(Team playerTeam) {
        this.playerTeam = playerTeam;
        //red always goes first
        resetPlayer();
        this.isPlayerHuman = true;
    }

    @Override public Optional<Move> getPlayerMove(Board board) {
        return Optional.empty();
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

    public void resetPlayer(){
        isPlayersTurn = playerTeam == Team.RED;
    }
}

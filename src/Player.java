import java.util.ArrayList;
import java.util.Optional;

public interface Player {

    boolean isPlayerHuman();

    Optional<Move> getPlayerMove(Board board);

    boolean isPlayersTurn();

    void switchTurn();

    Team getPlayerTeam();

    void resetPlayer();
}

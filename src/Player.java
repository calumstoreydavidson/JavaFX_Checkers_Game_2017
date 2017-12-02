import java.util.Optional;

public interface Player {

    boolean isPlayerHuman();

    Optional<Move> getPlayerMove(DisplayBoard displayBoard);

    boolean isPlayersTurn();

    void switchTurn();

    Team getPlayerTeam();

    void resetPlayer();
}

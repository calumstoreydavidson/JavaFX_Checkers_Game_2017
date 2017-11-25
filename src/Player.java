import java.util.ArrayList;

public interface Player {

    boolean isPlayerHuman();

    Move getPlayerMove(Board board);

    boolean isPlayersTurn();

    void switchTurn();

    Team getPlayerTeam();

    void setPlayerTeam(Team playerTeam);
}

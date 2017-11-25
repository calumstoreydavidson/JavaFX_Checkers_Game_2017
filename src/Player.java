import java.util.ArrayList;

public interface Player {

    boolean isPlayerHuman();

    Move getPlayerMove(ArrayList<Move> possibleMoves);

    boolean isPlayersTurn();

    void switchTurn();

    Team getPlayerTeam();

    void setPlayerTeam(Team playerTeam);
}

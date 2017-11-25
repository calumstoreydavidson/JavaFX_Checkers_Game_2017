public interface Player {

    boolean isPlayerHuman();

    Move getPlayerMove(Game game);

    boolean isPlayersTurn();

    void switchTurn();

    Team getPlayerTeam();

    void setPlayerTeam(Team playerTeam);
}

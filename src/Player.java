import java.util.Optional;

/**
 * this class is responsible for representing that logic which is applicable to all players in the application
 */
public abstract class Player {

    // stores what type of player this is
    private PlayerType playerType;

    // stores whether or not it is this players turn
    private boolean isPlayersTurn;

    // stores this players team
    private Team playerTeam;

    abstract Optional<Move> getPlayerMove(DisplayBoard displayBoard);

    /**
     * check what type of player this is
     *
     * @return what type of player this is
     */
    public PlayerType getPlayerType() {
        return playerType;
    }

    /**
     * set what type of player this is
     *
     * @param playerType what type of player this is
     */
    public void setPlayerType(PlayerType playerType) {
        this.playerType = playerType;
    }

    /**
     * check if the current player is human
     *
     * @return whether or not this player is human
     */
    public boolean isPlayerHuman() {
        return playerType == PlayerType.USER;
    }

    /**
     * check if its this players turn
     *
     * @return whether or not its this players turn
     */
    public boolean isPlayersTurn() {
        return isPlayersTurn;
    }

    /**
     * get this players team
     *
     * @return the team of this player
     */
    public Team getPlayerTeam() {
        return playerTeam;
    }

    /**
     * set the players team
     *
     * @param playerTeam the players team
     */
    public void setPlayerTeam(Team playerTeam) {
        this.playerTeam = playerTeam;
    }

    /**
     * flip this players turn state, if true, then make it false and visa versa
     */
    public void switchTurn() {
        isPlayersTurn = !isPlayersTurn;
    }

    /**
     * for use at the start of new games, where the players are unchanged, ensure the red player will always make the
     * first move
     */
    public void resetPlayer() {//red always goes first
        isPlayersTurn = playerTeam == Team.RED;
    }
}

import java.util.Optional;

public abstract class Player {

    // stores whether or not this player is human
    private boolean isPlayerHuman;

    // stores whether or not it is this players turn
    private boolean isPlayersTurn;

    // stores this players team
    private Team playerTeam;

    abstract Optional<Move> getPlayerMove(DisplayBoard displayBoard);

    /**
     * check if the current player is human
     *
     * @return whether or not this player is human
     */
    public boolean isPlayerHuman() {
        return isPlayerHuman;
    }

    public void setPlayerHuman(boolean playerHuman) {
        isPlayerHuman = playerHuman;
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

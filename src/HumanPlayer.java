import java.util.Optional;

/**
 * represents a player controlled by a real human user
 */
public class HumanPlayer extends Player {

    //an AI advisor for the user
    private ABNegamaxAI AIAdvisor;

    /**
     * create a human player with the given team
     *
     * @param playerTeam
     */
    public HumanPlayer(Team playerTeam) {
        setPlayerTeam(playerTeam);
        resetPlayer();
        setPlayerType(PlayerType.USER);
        AIAdvisor = new ABNegamaxAI(getPlayerTeam());
    }

    /**
     * returns nothing and thus does not initiate any successor code,
     * which allows the code to fall through until nothing is running on the core JavaFx thread,
     * at which point the user is able to input their desired move,
     * which triggers the successor code to move the system along.
     *
     * also apply's AI advisor highlighting if toggled
     *
     * @param displayBoard the current games state
     * @return nothing / an empty optional
     */
    @Override public Optional<Move> getPlayerMove(DisplayBoard displayBoard) {
        return Optional.empty();
    }

    /**
     * get the users advisor AI
     *
     * @return the users advisor AI
     */
    public ABNegamaxAI getAIAdvisor() {
        return AIAdvisor;
    }
}

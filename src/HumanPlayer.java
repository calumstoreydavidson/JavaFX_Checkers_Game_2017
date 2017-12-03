import java.util.Optional;

/**
 * represents a player controlled by a real human user
 */
public class HumanPlayer extends Player {

    /**
     *
     * @param playerTeam
     */
    public HumanPlayer(Team playerTeam) {
        setPlayerTeam(playerTeam);
        resetPlayer();
        setPlayerHuman(true);
    }

    @Override public Optional<Move> getPlayerMove(DisplayBoard displayBoard) {
        return Optional.empty();
    }

}

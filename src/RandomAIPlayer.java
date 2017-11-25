import java.util.ArrayList;
import java.util.Random;

public class RandomAIPlayer implements Player{

    private Random rand = new Random();
    private boolean isPlayersTurn;
    private Team playerTeam;
    boolean isPlayerHuman;

    public RandomAIPlayer(Team playerTeam) {
        this.playerTeam = playerTeam;
        //red always goes first
        isPlayersTurn = playerTeam == Team.RED;
        this.isPlayerHuman = false;
    }

    @Override
    public Move getPlayerMove(Game game){
        ArrayList<Move> possibleMoves = game.getPossibleMoves();
        int r = rand.nextInt(possibleMoves.size());
        Move AIMove = possibleMoves.get(r);
        return AIMove;
    }

    public boolean isPlayerHuman() {
        return isPlayerHuman;
    }

    public boolean isPlayersTurn() {
        return isPlayersTurn;
    }

    public void switchTurn(){
        isPlayersTurn = !isPlayersTurn;
    }

    public Team getPlayerTeam() {
        return playerTeam;
    }

    public void setPlayerTeam(Team playerTeam) {
        this.playerTeam = playerTeam;
    }
}

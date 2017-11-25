import java.util.ArrayList;
import java.util.Random;

import javafx.scene.layout.GridPane;

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
    public Move getPlayerMove(Board board){
        ArrayList<Move> possibleMoves = board.getPossibleMoves();
        int r = rand.nextInt(possibleMoves.size());
        Move AIMove = possibleMoves.get(r);
//        Main.output.appendText("RandomAIPlayer moving: " + AIMove.getTarget().origin.x + ", " + AIMove.getTarget().origin.y + " -> " + AIMove.getTarget().x + ", " + AIMove.getTarget().y);

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

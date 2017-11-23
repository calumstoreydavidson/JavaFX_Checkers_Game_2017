import java.util.ArrayList;
import java.util.Random;

public class RandomAIPlayer implements Player{

    private Random rand = new Random();

    @Override
    public Move getPlayerMove(ArrayList<Move> possibleMoves){
        int r = rand.nextInt(possibleMoves.size());
        Move AIMove = possibleMoves.get(r);
        Main.output.setText("RandomAIPlayer moving: " + AIMove.getTarget().origin.x + ", " + AIMove.getTarget().origin.y + " -> " + AIMove.getTarget().x + ", " + AIMove.getTarget().y);

        return AIMove;
    }
}

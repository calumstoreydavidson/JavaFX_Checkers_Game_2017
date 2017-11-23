import java.util.ArrayList;

public abstract class Player {

   private boolean isPlayersTurn;
   private Team playerTeam;

   public Player(Team playerTeam){
       this.playerTeam = playerTeam;
       //red always goes first
       isPlayersTurn = playerTeam == Team.RED;
   }

   public abstract Move getPlayerMove(ArrayList<Move> possibleMoves);

   public boolean IsPlayersTurn(){
       return isPlayersTurn;
   }

   public void endTurn(){
       isPlayersTurn = false;
   }

   public void startTurn(){
       isPlayersTurn = true;
   }

}

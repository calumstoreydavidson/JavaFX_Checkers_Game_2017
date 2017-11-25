import java.util.*;

public class SimpleNimmAB {

    static int pile = 0; // number of tokens in the pile
    static int tokensLeftInPile = 0; // current number of tokens in the pile
    static int bound = 0; // the max any player can take
    static int[] humanHistory; // previous turns of the human player
    static int[] AIHistory; // previous turns of the AI player
    static List<TakesAndScores> successorEvaluations;
    static int[] mmAIhistory; // array for storing simulated takes
    static int[] mmHUMANhistory; // array for storing simulated takes
    static int sim_pile; // pile for simulated takes
    static TakesAndScores bestMove;

    private static int[] cloneHistory(int[] source) {
        // crude and simple method for cloning an array
        int dest[] = new int[source.length];
        int temp;
        for (int i = 0; i < source.length; i++) {
            temp = source[i];
            dest[i] = temp;
        }
        return dest;
    }

    private static int getAIMove() {
        successorEvaluations = new ArrayList<TakesAndScores>();
        mmAIhistory = cloneHistory(AIHistory);
        mmHUMANhistory = cloneHistory(humanHistory);
        sim_pile = tokensLeftInPile;
        // player 1 is the AI - start evaluation
        bestMove = new TakesAndScores(0, 0);
        minimax(0, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
        return returnBestMove();
    }

    private static ArrayList<Integer> getAvailableTakes(int player) {
        int[] history = (player == 1) ? mmAIhistory : mmHUMANhistory;

        ArrayList<Integer> at = new ArrayList<Integer>();
        for (int i = 1; i < history.length; i++) {
            if (tokensLeftInPile - i >= 0 && 1 == Math.abs(history[i] - 1)) {
                at.add(i);
            }
        }
        for (Integer i:at) {
            System.out.print(i+",");
        }
        System.out.println();
        return at;
    }

//    public static ArrayList<Integer> getAvailableTakes(int player) {
//        ArrayList<Integer> moves = new ArrayList<>();
//        int[] moveHistory = (player == 1) ? mmAIhistory : mmHUMANhistory;
//        for (int i:moveHistory) {
//            if (i>0){
//                moves.add(i);
//            }
//        }
//
//        ArrayList<Integer> range = getTakeRange();
//        range.removeAll(moves);
//
//        ArrayList<Integer> possibleMoves = range;
//        return possibleMoves;
//    }
//
//    private static ArrayList<Integer> getTakeRange() {
//        ArrayList<Integer> takeRange = new ArrayList<>();
//        for (int i = 1; i <= bound; i++) {
//            takeRange.add(i);
//        }
//        return takeRange;
//    }

    public static int minimax(int depth, int player, int alpha, int beta) {
        int bestScore;
        if (player == 1) {
            bestScore = -10;
        } else {
            bestScore = 10;
        }

        //int bestScoreMax = Integer.MIN_VALUE; // initialise MAX worst case score
        //int bestScoreMin = Integer.MAX_VALUE; // initialise MIN worst case score
        // Terminal test
        if (player == 1 && (sim_pile == 0 || playerStuck(mmAIhistory))) {
            return -1;
        }
        if (player == 2 && (sim_pile == 0 || playerStuck(mmHUMANhistory))) {
            return +1;
        }

        // A list to store pairs of takes and scores in
        ArrayList<Integer> availableTakes = getAvailableTakes(player);
        // Clear successor evaluations after each completed playthrough
        if (depth == 0) {
            successorEvaluations.clear();
        }

        for (int i = 0; i < availableTakes.size(); i++) {

            int take = availableTakes.get(i);
            int currentScore = 0;

            if (player == 1) { // get the highest result returned by minimax
                mmAIhistory[take] = 1;
                sim_pile -= take;
                currentScore = minimax(depth + 1, 2, alpha, beta);
                bestScore = Math.max(bestScore, currentScore);
                alpha = Math.max(currentScore, alpha);
            } else if (player == 2) {// AI turn: get the lowest result returned by minimax
                mmHUMANhistory[take] = 1;
                sim_pile -= take;

                currentScore = minimax(depth + 1, 1, alpha, beta);
                bestScore = Math.min(bestScore, currentScore);
                beta = Math.min(currentScore, beta);
            }
            if (depth == 0 && bestMove.score < currentScore) {
                bestMove = new TakesAndScores(currentScore, take);

            }
            // reset changes
            sim_pile += take;
            if (player == 1) {
                mmAIhistory[take] = 0;
            } else {
                mmHUMANhistory[take] = 0;
            }
            if (alpha >= beta) {
                break;
            }
        }

        return bestScore;
    }

    private static int returnBestMove() {
        return bestMove.take;
    }

    private static void calcUpperBound() {
        int temp = 0;
        while (temp + bound + 1 < pile) {
            bound++;
            temp += bound;
        }
    }

    private static String getUserCommand() {
//        System.out.print("Please enter the number of tokens in the pile, or 'q' to quit: ");
//        Scanner scanner = new Scanner(System.in);
//        String s = scanner.next();
        return "20";
    }

    private static void processUserCommand(String c) {
        try {
            bound = 0;
            tokensLeftInPile = Integer.parseInt(c);
            pile = tokensLeftInPile;
            calcUpperBound();
            humanHistory = new int[bound + 1];
            AIHistory = new int[bound + 1];
            System.out.println();
            System.out.println("The pile has " + tokensLeftInPile + " tokens.");
            System.out.print("The maximum you may take in any turn is " + bound);
        } catch (Exception e) {
            System.out.print("Good Bye!");
            System.exit(0);
        }
    }

    private static boolean playerStuck(int[] history) {
        for (int i = 1; i < history.length; i++) {
            // if there is any valid move possible, go on
            if (tokensLeftInPile - i >= 0 && 1 == Math.abs(history[i] - 1)) {
                // Hint: // System.out.println("Player could take " +i+ " tokens.");
                return false;
            }
        }
        return true;
    }

    private static void play(String humanName) {
        Scanner humanInput = new Scanner(System.in);
        int humanMove = 0;
        int AImove = 0;

        while (tokensLeftInPile > 0) {

            // ** HUMAN MOVE **
            // check if a move can be made at all
            if (playerStuck(humanHistory)) {
                System.out.println("\n* " + humanName + " can't move anymore. \n\n** The AI wins! **");
                break;
            }
            // a move can be made; elicit input
            System.out.print("\nHow many do you want? ");
            System.out.println("\namount left: " + tokensLeftInPile);
            humanMove = humanInput.nextInt();

            // input validity checks
            if (humanMove < 1) {
                System.out.println("You have to take at least one token per turn.");
                continue;
            }
            if (humanMove > bound) {
                System.out.println("You can't take that many tokens! (Maximum " + bound + ")");
                continue;
            }
            if (tokensLeftInPile - humanMove < 0) {
                System.out.println("There aren't that many tokens left in the pile!");
                continue;
            }
            if (humanHistory[humanMove] == 1) {
                System.out.println("You cannot take the same amount twice!");
                continue;
            }
            // move appears to be valid; make move
            tokensLeftInPile -= humanMove;
            System.out.println("* " + humanName + " takes " + humanMove);
            // record the move in history
            humanHistory[humanMove] = 1;

            // terminal test for human player
            if (tokensLeftInPile == 0) {
                System.out.println("\n* The pile is empty. \n\n** " + humanName + " wins! **");
                break;
            }

            // ** AI MOVE **
            // stuck?
            if (playerStuck(AIHistory)) {
                System.out.println("\n* The AI player can't move anymore. \n\n** " + humanName + " wins! **");
                break;
            }

            AImove = getAIMove();

            System.out.println("* AI takes " + AImove);
            tokensLeftInPile -= AImove;
            AIHistory[AImove] = 1;

            // terminal test for AI player
            if (tokensLeftInPile == 0) {
                System.out.println("\n* The pile is empty. \n\n** The AI wins! **");
                break;
            }
        }
        System.out.println("\nWould you like to play again?");
        processUserCommand(getUserCommand());
        play(humanName);
    }

    public static void main(String[] args) {
//        Scanner humanInput = new Scanner(System.in);
        System.out.println("\n**********************");
        System.out.println("Hello, welcome to Nimm!");
//        System.out.print("What is your name? ");
//        String humanName = humanInput.next();
        processUserCommand(getUserCommand());
        play("Calum");
    }
}

class TakesAndScores {
    int score;
    int take;

    TakesAndScores(int score, int take) {
        this.score = score;
        this.take = take;
    }
}
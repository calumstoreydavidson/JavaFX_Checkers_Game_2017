/**
 * represents a move and its associated score, for use in minimax and derivative algorithms
 */
public class MoveAndScore {
    Move move;
    double score;

    /**
     * creates a moveAndScore with the specified move and score
     *
     * @param move the move to be associated with a score
     * @param score the score to be associated with a move
     */
    MoveAndScore(Move move, double score) {//TODO create NEGAMAXAI abstract class - make this private in there
        this.move = move;
        this.score = score;
    }

    /**
     * inverts the score for use in Negamax algorithms e.g. 1 -> -1 & -1 -> 1
     */
    public void negateScore() {
        score = -score;
    }
}
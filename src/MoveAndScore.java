public class MoveAndScore {
    Move move;
    double score;

    MoveAndScore(Move move, double score) {
        this.move = move;
        this.score = score;
    }

    public void negateScore() {
        score = -score;
    }
}
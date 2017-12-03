import java.util.Optional;

public class NegamaxAI extends Player {

    public NegamaxAI(Team playerTeam) {
        setPlayerTeam(playerTeam);
        resetPlayer();
        setPlayerHuman(false);
    }

    @Override public Optional<Move> getPlayerMove(DisplayBoard displayBoard) {
        if (Game.VERBOSE_OUTPUT) {
            GUI.output.appendText("AI is thinking \n");
        }
        SimulationBoard sim = new SimulationBoard(displayBoard, getPlayerTeam());

        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.println("--------------------------------------------------------------------------------------------------");
        return Optional.of(negamax(sim, 0, 1).move);
    }

    private MoveAndScore negamax(SimulationBoard node, int depth, int team) {
        if (node.getTeamsPossibleMoves().isEmpty() || depth == getMaxSearchDepth()) {
            MoveAndScore result = new MoveAndScore(null, node.evaluateState());
            result.score *= team;
            return result;
        }
        MoveAndScore max = new MoveAndScore(null, Integer.MIN_VALUE);

        //for all moves
        for (Move move : node.getTeamsPossibleMoves()) {
            MoveAndScore score = negate(negamax(node.getChild(move), depth + 1, -team));
            score.move = move;
            max = score.score > max.score ? score : max;
        }
        return max;
    }

    public MoveAndScore negate(MoveAndScore value) {
        value.negateScore();
        return value;
    }

    private int getMaxSearchDepth() {
        return Game.AI_MAX_SEARCH_DEPTH;
    }

}

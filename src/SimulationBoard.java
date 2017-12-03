import java.util.ArrayList;
import java.util.Random;

public class SimulationBoard extends Board{

    private enum Type {
        EMPTY, WHITE, RED, WHITE_KING, RED_KING
    }

    private Random rand = new Random();

    private Type[][] simBoard = new Type[Game.SCALE][Game.SCALE];
    private ArrayList<Coordinates> redUnits = new ArrayList<>();
    private ArrayList<Coordinates> whiteUnits = new ArrayList<>();
    private Team currentTeam;
    private Unit movingUnit;

    public SimulationBoard(DisplayBoard oldDisplayBoard, Team team) {
        generateSimTiles(oldDisplayBoard);
        currentTeam = team;
        movingUnit = oldDisplayBoard.getMovingUnit();
    }

    public SimulationBoard(SimulationBoard oldSimBoard){
        for (int i = 0; i < Game.SCALE; i++) {
            for (int j = 0; j < Game.SCALE; j++) {
                simBoard[i][j] = oldSimBoard.getSimBoard()[i][j];
            }
        }
        redUnits = new ArrayList<>(oldSimBoard.getRedUnits());
        whiteUnits = new ArrayList<>(oldSimBoard.getWhiteUnits());
        currentTeam = oldSimBoard.getCurrentTeam();
    }

    public void generateSimTiles(DisplayBoard oldDisplayBoard) {
        simBoard = new Type[Game.SCALE][Game.SCALE];
        for (int i = 0; i < Game.SCALE; i++) {
            for (int j = 0; j < Game.SCALE; j++) {
                generateSimTile(oldDisplayBoard, i, j);
            }
        }
    }

    public void generateSimTile(DisplayBoard oldDisplayBoard, int i, int j) {
        Tile tile = oldDisplayBoard.getTile(new Coordinates(i, j));
        if (tile.getUnit() == null) {
            simBoard[i][j] = Type.EMPTY;
        } else {
            Unit unit = tile.getUnit();
            if (unit.isRed()) {
                redUnits.add(new Coordinates(i,j));
                if (unit.isKing()) {
                    simBoard[i][j] = Type.RED_KING;
                } else {
                    simBoard[i][j] = Type.RED;
                }
            } else {
                whiteUnits.add(new Coordinates(i,j));
                if (unit.isKing()) {
                    simBoard[i][j] = Type.WHITE_KING;
                } else {
                    simBoard[i][j] = Type.WHITE;
                }
            }
        }
    }

    public ArrayList<Move> getTeamsPossibleMoves() {
        ArrayList<Move> possibleTeamMoves = new ArrayList<>();
        ArrayList<Coordinates> teamUnits = currentTeam == Team.RED ? redUnits : whiteUnits;

        if (movingUnit != null) {
            possibleTeamMoves.addAll(getUnitsPossibleMoves(movingUnit.getPos()));
        } else {
            for (Coordinates position : teamUnits) {
                possibleTeamMoves.addAll(getUnitsPossibleMoves(position));
            }
        }
        return prioritiseAttackMoves(possibleTeamMoves);
    }

    public ArrayList<Move> getUnitsPossibleMoves(Coordinates origin) {
        ArrayList<Move> moves = new ArrayList<>();

        for (Coordinates possiblePosition : getUnitsPossiblePositions(origin)) {
            if (!isOccupiedTile(possiblePosition)) {
                Move normalMove = new Move(origin, possiblePosition, MoveType.NORMAL);
                if (possiblePosition.isEnemyKingRow(currentTeam) && !isKing(origin)) {
                    normalMove.createKing();
                }
                moves.add(normalMove);
            } else if (isEnemyUnit(possiblePosition) && isAttackPossible(possiblePosition)) {
                Move attackMove = new Move(origin, possiblePosition.getNextOnPath(), MoveType.KILL);
                if (possiblePosition.getNextOnPath().isEnemyKingRow(currentTeam) && !isKing(origin) || isKing(possiblePosition) && !isKing(origin) && Game.CROWN_STEALING_ALLOWED) {
                    attackMove.createKing();
                }
                moves.add(attackMove);
            }
        }
        return prioritiseAttackMoves(moves);
    }

    public ArrayList<Coordinates> getUnitsPossiblePositions(Coordinates origin){
        ArrayList<Coordinates> potentiallyAdjacentTiles = new ArrayList<>();

        if (isKing(origin) || currentTeam == Team.RED) {
            potentiallyAdjacentTiles.add(origin.SW());
            potentiallyAdjacentTiles.add(origin.SE());
        }
        if (isKing(origin) || currentTeam == Team.WHITE) {
            potentiallyAdjacentTiles.add(origin.NW());
            potentiallyAdjacentTiles.add(origin.NE());
        }

        ArrayList<Coordinates> validAdjacentTiles = new ArrayList<>();
        for (Coordinates position : potentiallyAdjacentTiles) {
            if (!position.isOutsideBoard()) {
                validAdjacentTiles.add(position);
            }
        }

        return validAdjacentTiles;
    }

    private Type getTile(Coordinates position){
        return simBoard[position.x][position.y];
    }

    private void setTile(Coordinates position, Type type){
        simBoard[position.x][position.y] = type;
    }

    private boolean isEnemyUnit(Coordinates position){
        if (currentTeam == Team.RED){
            return getTile(position) == Type.WHITE || getTile(position) == Type.WHITE_KING;
        }else {
            return getTile(position) == Type.RED || getTile(position) == Type.RED_KING;
        }
    }

    private boolean isAttackPossible(Coordinates adjacentTile) {
        return !isEnemyOnEdge(adjacentTile) && !isOccupiedTile(adjacentTile.getNextOnPath());
    }

    public boolean isOccupiedTile(Coordinates position) {
        return getTile(position) != Type.EMPTY;
    }

    private boolean isKing(Coordinates position){
        Type type = getTile(position);
        return type == Type.WHITE_KING || type == Type.RED_KING;
    }

    private boolean canAttack(Move move) {
        ArrayList<Move> possibleMoves = getUnitsPossibleMoves(move.getTarget());
        if (!possibleMoves.isEmpty()) {
            return possibleMoves.get(0).getType() == MoveType.KILL;
        } else {
            return false;
        }
    }

    private void doMove(Move move){
        moveUnit(move);

        if(move.getType() == MoveType.KILL){
            killUnit(Coordinates.getKillCoords(move));

            if (canAttack(move) && !move.isKingCreated()) {
                doMove(getRandomMove(getUnitsPossibleMoves(move.getTarget())));
            }
        }
    }

    private void moveUnit(Move move){
        if (getTile(move.getOrigin()) == Type.RED || getTile(move.getOrigin()) == Type.RED_KING){
            redUnits.remove(move.getOrigin());
            redUnits.add(move.getTarget());
        }else {
            whiteUnits.remove(move.getOrigin());
            whiteUnits.add(move.getTarget());
        }

        setTile(move.getTarget(), getTile(move.getOrigin()));
        setTile(move.getOrigin(), Type.EMPTY);

       if (move.isKingCreated()){
           crownKing(move.getTarget());
       }
    }

    private void killUnit(Coordinates position){
        if (getTile(position) == Type.RED || getTile(position) == Type.RED_KING){
            redUnits.remove(position);
        }else {
            whiteUnits.remove(position);
        }
        setTile(position, Type.EMPTY);
    }

    private void crownKing(Coordinates position){
        Type type = getTile(position) == Type.RED ? Type.RED_KING : Type.WHITE_KING;
        setTile(position, type);
    }

    public Move getRandomMove(ArrayList<Move> moves){
        int r = rand.nextInt(moves.size());
        return moves.get(r);
    }

    public Type[][] getSimBoard() {
        return simBoard;
    }

    public ArrayList<Coordinates> getRedUnits() {
        return redUnits;
    }

    public ArrayList<Coordinates> getWhiteUnits() {
        return whiteUnits;
    }

    public double evaluateState() {
        //heuristic - absolute piece count - kings worth double
        double reds = 0;
        double whites = 0;

        for (Coordinates pos : redUnits) {
            Type type = getTile(pos);

            if (type == Type.RED) {
                reds++;
            } else if (type == Type.RED_KING) {//kings are better than pawns, get and protect them
                reds++;
                reds++;
            }
        }

        for (Coordinates pos : whiteUnits) {
            Type type = getTile(pos);
            if (type == Type.WHITE) {
                whites++;
            } else if (type == Type.WHITE_KING) {
                whites++;
                whites++;
            }
        }

        if (currentTeam == Team.WHITE) {
            return whites - reds;
        } else {
            return reds - whites;
        }
    }

    public SimulationBoard getChild(Move move){
        SimulationBoard child = new SimulationBoard(this);
        child.doMove(move);
        child.setNextPlayer();
        return child;
    }

    public Team getCurrentTeam(){
        return currentTeam;
    }

    public void setNextPlayer() {
        currentTeam = currentTeam == Team.RED ? Team.WHITE : Team.RED;
    }
}

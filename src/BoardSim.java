import java.util.ArrayList;
import java.util.Random;

public class BoardSim {

    private enum Type {
        EMPTY, WHITE, RED, WHITE_KING, RED_KING
    }

    private Random rand = new Random();

    private Type[][] simBoard = new Type[Game.SCALE][Game.SCALE];
    private ArrayList<Coordinates> redUnits = new ArrayList<>();
    private ArrayList<Coordinates> whiteUnits = new ArrayList<>();
    private Team currentTeam;

    public BoardSim(Board oldBoard, Team team) {
        generateSimTiles(oldBoard);
        currentTeam = team;
    }

    public BoardSim(BoardSim oldBoard){
//        simBoard = oldBoard.getSimBoard().clone();
        for (int i = 0; i < Game.SCALE; i++) {
            for (int j = 0; j < Game.SCALE; j++) {
                simBoard[i][j] = oldBoard.getSimBoard()[i][j];
            }
        }
        redUnits = new ArrayList<>(oldBoard.getRedUnits());
        whiteUnits = new ArrayList<>(oldBoard.getWhiteUnits());
        currentTeam = oldBoard.getCurrentTeam();
    }

    public void generateSimTiles(Board oldBoard) {
        simBoard = new Type[Game.SCALE][Game.SCALE];
        for (int i = 0; i < Game.SCALE; i++) {
            for (int j = 0; j < Game.SCALE; j++) {
                generateSimTile(oldBoard, i, j);
            }
        }
    }

    public void generateSimTile(Board oldBoard, int i, int j) {
        Tile tile = oldBoard.getTile(new Coordinates(i, j));
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

        for (Coordinates position: teamUnits) {
            possibleTeamMoves.addAll(getUnitsPossibleMoves(position));
        }

        return prioritiseAttackMoves(possibleTeamMoves);
    }

    public ArrayList<Move> prioritiseAttackMoves(ArrayList<Move> possibleUnitMoves) {
        ArrayList<Move> attackMoves = new ArrayList<>();
        for (Move move : possibleUnitMoves) {
            if (move.getResult().getType() == MoveType.KILL) {
                attackMoves.add(move);
            }
        }

        if (!attackMoves.isEmpty()) {
            return attackMoves;
        } else {
            return possibleUnitMoves;
        }
    }

    public ArrayList<Move> getUnitsPossibleMoves(Coordinates origin) {
        ArrayList<Move> moves = new ArrayList<>();

        for (Coordinates possiblePosition : getUnitsPossiblePositions(origin)) {
            if (!isOccupiedTile(possiblePosition)) {
                MoveResult result = new MoveResult(MoveType.NORMAL);
                if (possiblePosition.isEnemyKingRow(currentTeam) && !isKing(origin)) {
                    result.createKing();
                }
                moves.add(new Move(origin, possiblePosition, result));
            } else if (isEnemyUnit(possiblePosition) && isAttackPossible(possiblePosition)) {
                MoveResult result = new MoveResult(MoveType.KILL);
                if (possiblePosition.getNextOnPath().isEnemyKingRow(currentTeam) && !isKing(origin) || isKing(possiblePosition) && !isKing(origin) && Game.CROWN_STEALING_ALLOWED) {
                    result.createKing();
                }
                moves.add(new Move(origin, possiblePosition.getNextOnPath(), result));
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
            if (!Coordinates.isOutsideBoard(position)) {
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

    public boolean isEnemyOnEdge(Coordinates enemyPos) {
        return Coordinates.isBoardEdge(enemyPos);
    }

    private boolean isKing(Coordinates position){
        Type type = getTile(position);
        return type == Type.WHITE_KING || type == Type.RED_KING;
    }

    private boolean canAttack(Move move) {
        ArrayList<Move> possibleMoves = getUnitsPossibleMoves(move.getTarget());
        if (!possibleMoves.isEmpty()) {
            return possibleMoves.get(0).getResult().getType() == MoveType.KILL;
        } else {
            return false;
        }
    }

    private void doMove(Move move){
        moveUnit(move);

        if(move.getResult().getType() == MoveType.KILL){
            killUnit(Coordinates.getKillCoords(move));

            if (canAttack(move) && !move.getResult().isKingCreated()) {
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

       if (move.getResult().isKingCreated()){
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
        if (getTile(position) == Type.RED) {
            setTile(position,Type.RED_KING);
        } else {
            setTile(position, Type.WHITE_KING);
        }
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

    public void setNextPlayer() {
        if (currentTeam == Team.RED) {
            currentTeam = Team.WHITE;
        } else {
            currentTeam = Team.RED;
        }
    }

    public double evaluateState() {
        //heuristic - absolute piece count
/*        if (team == Team.WHITE) {
            return whiteUnits.size() - redUnits.size();
        }
        return redUnits.size() - whiteUnits.size();*/

        //heuristic - absolute piece count - kings worth double
        double reds = 0;
        double whites = 0;

        for (Coordinates pos : redUnits) {
            Type type = getTile(pos);
            if (Coordinates.isBoardEdge(pos)) {//edges are safe, so encourage their use
//                reds += 0.1;
            }
            if (type == Type.RED) {
                reds++;
            } else if (type == Type.RED_KING) {//kings are better than pawns, get and protect them
                reds++;
                reds++;
            }
        }

        for (Coordinates pos : whiteUnits) {
            Type type = getTile(pos);
            if (Coordinates.isBoardEdge(pos)) {//edges are safe, so encourage their use
//                whites += 0.1;
            }
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

    public BoardSim getChild(Move move){
        BoardSim child = new BoardSim(this);
        child.doMove(move);
        child.setNextPlayer();
        return child;
    }

    public void outputSimBoard(int depth) {
        for (int x = 0; x < Game.SCALE; x++) {
            for(int i = 1; i<=depth;i++){
                System.out.print(" | ");
            }
            System.out.print("|");
            for (int y = 0; y < Game.SCALE; y++) {
                Type type = getTile(new Coordinates(y,x));
                String out;
                if (type == Type.RED){
                    out = "r";
                }else if(type == Type.RED_KING){
                    out = "R";
                }else if (type == Type.WHITE){
                    out = "w";
                }else if(type == Type.WHITE_KING){
                    out = "W";
                }else {
                    out = "_";
                }
                System.out.print(out + "|");
            }
            System.out.println("");
        }
        System.out.println("");
    }

    public Team getCurrentTeam(){
        return currentTeam;
    }
}

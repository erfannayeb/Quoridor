package game;

import java.util.Arrays;
import java.util.InputMismatchException;

public class Board{
    private final int EMPTY = 0;
    private final int PLAYER1_CELL = 1;
    private final int PLAYER2_CELL = 2;
    private final int WALL = 11;

    private final int[][] board = new int[17][17];
    Player player1;
    Player player2;
    Player turn;
    Wall wall = new Wall();

    public Board(Player player1, Player player2, int turn) {
        for(int[] ints : board)
            Arrays.fill(ints,EMPTY);
        this.player1 = player1;
        this.player2 = player2;
        if (turn == 1)
            this.turn = player1;
        else
            this.turn = player2;
    }

    public int[][] getBoard() { return board; }
    public Player getTurn() { return turn; }
    public Player getPlayer1() { return player1; }
    public Player getPlayer2() { return player2; }
    public void setCell(int y, int x, int value) {
        board[y][x] = value;
        if (value == 1)
            player1.setBead(y, x);
        else if (value == 2)
            player2.setBead(y, x);
    }

    public void move(int x, int y) throws InputMismatchException {
        if (canMove(x, y)) {
            board[turn.bead.getY()][turn.bead.getX()] = EMPTY;
            turn.bead.setX(x);
            turn.bead.setY(y);
            board[turn.bead.getY()][turn.bead.getX()] = (turn.id == 'U')? PLAYER1_CELL : PLAYER2_CELL;
        }
        else
            throw new InputMismatchException("INVALID PLACE");
    }

    public boolean canMove(int x, int y) {
        int opponent_cell = turn.getId() == 'U' ? 2 : 1;
        return (Math.abs(turn.bead.getX() - x) == 2 && turn.bead.getY() == y && board[y][(turn.bead.getX() + x) / 2] == EMPTY) ||
                (Math.abs(turn.bead.getY() - y) == 2 && turn.bead.getX() == x && board[(turn.bead.getY() + y) / 2][x] == EMPTY) ||
                (Math.abs(turn.bead.getX() - x) == 4 && turn.bead.getY() == y && board[y][(turn.bead.getX() + x) / 2] == opponent_cell && board[y][(((turn.bead.getX() + x) / 2) + turn.bead.getX()) / 2] == EMPTY && board[y][(((turn.bead.getX() + x) / 2) + x) / 2] == EMPTY) ||
                (Math.abs(turn.bead.getY() - y) == 4 && turn.bead.getX() == x && board[(turn.bead.getY() + y) / 2][x] == opponent_cell && board[(((turn.bead.getY() + y) / 2) + turn.bead.getY()) / 2][x] == EMPTY && board[(((turn.bead.getY() + y) / 2) + y) / 2][x] == EMPTY) ||
                (Math.abs(turn.bead.getX() - x) == 2 && Math.abs(turn.bead.getY() - y) == 2 && board[y][turn.bead.getX()] == opponent_cell && board[(turn.bead.getY() + y) / 2][turn.bead.getX()] == EMPTY && board[y][(turn.bead.getX() + x) / 2] == EMPTY) ||
                (Math.abs(turn.bead.getX() - x) == 2 && Math.abs(turn.bead.getY() - y) == 2 && board[turn.bead.getY()][x] == opponent_cell && board[turn.bead.getY()][(turn.bead.getX() + x) / 2] == EMPTY && board[(turn.bead.getY() + y) / 2][x] == EMPTY);

    }

    public void placeWall(int x, int y) throws InputMismatchException {
        if (wall.placeWall(board, player1, player2, turn, x, y)){
            //horizontal Wall
            if (x % 2 == 0 && y % 2 == 1) {
                board[y][x] = WALL;
                board[y][x + 1] = WALL;
                board[y][x + 2] = WALL;
            }
            //Vertical Wall
            else if (x % 2 == 1 && y % 2 == 0) {
                board[y][x] = WALL;
                board[y + 1][x] = WALL;
                board[y + 2][x] = WALL;
            }
            else
                throw new InputMismatchException("Can NOT place it here");
        }
        else
            throw new InputMismatchException("Can NOT place it here");
    }

    public boolean canPlaceWall(int x, int y) {
        return wall.canPlaceWall(board, player1, player2, x, y);
    }
    
    public void undoMove(int x, int y) {
        if (y % 2 == 0 && x % 2 == 0)
            board[y][x] = EMPTY;
        else if (y % 2 == 0 && x % 2 == 1 && x <16 && y < 15){
            board[y][x] = EMPTY;
            board[y + 1][x] = EMPTY;
            board[y + 2][x] = EMPTY;
            turn.increaseWalls();
            wall.undoPlaceWall(x, y);
        }
        else if  (y % 2 == 1 && x % 2 == 0 && x <15 && y < 16) {
            board[y][x] = EMPTY;
            board[y][x + 1] = EMPTY;
            board[y][x + 2] = EMPTY;
            turn.increaseWalls();
            wall.undoPlaceWall(x, y);
        }
    }

    public Player win() {
        if (turn.id == 'U') {
            for (int cell : board[16]) {
                if (cell == 1)
                    return player1;
            }
        } else {
            for (int cell : board[0]) {
                if (cell == 2)
                    return player2;
            }
        }
        return null;
    }

    public void turn() {
        if (this.turn == player1)
            turn = player2;
        else
            turn = player1;
    }
}

package game;

public class Wall {

    private final int EMPTY = 0;
    private BoardGraph boardGraph;

    public Wall() {
        boardGraph = new BoardGraph();
    }

    public boolean placeWall(int[][] board, Player player1, Player player2, Player turn, int x, int y) {
        int wallRemaining = turn.getId() == 'U' ? player1.getWalls() : player2.getWalls();
        //Horizontal Wall  //Vertical Wall
        if ( (wallRemaining > 0 && x % 2 == 0 && y % 2 == 1 && board[y][x + 1] == EMPTY && board[y][x + 2] == EMPTY) ||
             (wallRemaining > 0 && x % 2 == 1 && y % 2 == 0 && board[y + 1][x] == EMPTY && board[y + 2][x] == EMPTY) ) {

            if (allowedWall(player1, player2, x, y)) {
                //decrease number of player walls
                if (turn.getId() == 'U')
                    player1.decreaseWalls();
                else
                    player2.decreaseWalls();

                return true;
            }
        }
        return false;
    }
    //Checks that no bead is locked
    private boolean allowedWall(Player player1, Player player2, int x, int y) {
        return boardGraph.allowedRemoveEdge(player1, player2, x, y);
    }

    protected boolean canPlaceWall(int[][] board, Player player1, Player player2, int x, int y) {
        if ( (x % 2 == 0 && y % 2 == 1 && board[y][x + 1] == EMPTY && board[y][x + 2] == EMPTY) ||
             (x % 2 == 1 && y % 2 == 0 && board[y + 1][x] == EMPTY && board[y + 2][x] == EMPTY) )
            return boardGraph.canRemoveEdge(player1, player2, x, y);

        return false;
    }
    
    public void undoPlaceWall(int x, int y) {
        boardGraph.undoRemoveEdges(x, y);
    }
}

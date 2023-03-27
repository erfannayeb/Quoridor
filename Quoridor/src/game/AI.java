package game;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Random;

public class AI extends Player{

    private final int EMPTY = 0;

    private int min = -1000;
    private int max = 1000;
    private int best_depth;
    private int difficulty;

    public AI(String name, char id, int walls, int difficulty) { super(name, id, walls); this.difficulty = difficulty; }

    // This will return the best possible move for the player
    public int[] findBestMove(Board board)
    {
        best_depth = max;
        int bestVal = min;
        int[] bestMove = new int[2];
        bestMove[0] = -1;
        bestMove[1] = -1;
        if (difficulty == 1)
        {
            for (int i = 0; i < 1; ++i)
            {
                bestMove = moveRandomly();
                try {
                    board.move(bestMove[1], bestMove[0]);
                    board.undoMove(bestMove[1], bestMove[0]);
                    return bestMove;
                } catch (InputMismatchException ignored) {}
                try {
                    board.placeWall(bestMove[1], bestMove[0]);
                    board.undoMove(bestMove[1], bestMove[0]);
                    return bestMove;
                } catch (InputMismatchException e) {
                    i = 0;
                }
            }
        }
        if (difficulty == 2) {
            int lastX = 8;
            int lastY = id == 'U' ? 0 : 16;
            ArrayList<int[]> moves = new ArrayList<>();
            for (int i = lastY - 2; i < lastY + 2; i++)
                for (int j = lastX - 2; j < lastX + 2; j++)
                    if (i >= 0 && j >= 0 && i < 17 && j < 17)
                        if (board.canMove(j, i)) {
                            int[] move = new int[2];
                            move[0] = i;
                            move[1] = j;
                            moves.add(move);
                        }
            for (int i = lastY - 4; i < lastY + 4; i++)
                for (int j = lastX - 4; j < lastX + 4; j++)
                    if (i >= 0 && j >= 0 && i < 16 && j < 16)
                        try {
                            board.placeWall(j, i);
                            int[] move = new int[2];
                            move[0] = i;
                            move[1] = j;
                            moves.add(move);
                            board.undoMove(j, i);
                        } catch (InputMismatchException ignored) {}

            Random random = new Random();
            int index = random.nextInt(moves.size());
            return moves.get(index);
        }
        if (difficulty == 3)
        {
            // Check all cells and evaluate minimax function for all empty cells. And return the best move
            for (int i = 0; i < 17; i++) {
                for (int j = 0; j < 17; j++) {
                    int moveVal = min;

                    // Check if cell is empty
                    if (board.getBoard()[i][j] == EMPTY) {
                        if (i % 2 == 0 && j % 2 == 0) {
                            try {
                                board.move(j, i);
                                board.turn();
                                // compute evaluation function for this move.
                                moveVal = minimax(board, 0, false, min, max);
                                board.undoMove(j, i);
                            } catch (InputMismatchException ignored) {
                            }
                        } else if ((i % 2 == 0 && j % 2 == 1 && j < 16 && i < 15) ||
                                (i % 2 == 1 && j % 2 == 0 && j < 15 && i < 16)) {
                            try {
                                board.placeWall(j, i);
                                board.turn();
                                // compute evaluation function for this move.
                                moveVal = minimax(board, 0, false, min, max);
                                board.undoMove(j, i);
                            } catch (InputMismatchException ignored) {
                            }
                        }

                        // If the value of the current move is more than the best value, then update best
                        if (moveVal > bestVal) {
                            bestMove[0] = i;
                            bestMove[1] = j;
                            bestVal = moveVal;
                        }
                    }
                }
            }
        }
        return bestMove;
    }

    //It considers all the possible ways the game can go and returns the value of the board
    private int minimax(Board board, int depth, Boolean isMax, int alpha, int beta)
    {
        int score = evaluate(board);

        if (score == 0) {
            // If this maximizer's move
            if (isMax)
            {
                if (depth + 1 == best_depth || depth + 1 > 20)
                    return max;

                int best = min;

                // Traverse all cells
                for (int i = 0; i < 17; i++)
                {
                    for (int j = 0; j < 17; j++)
                    {
                        // Check if cell is empty
                        if (board.getBoard()[i][j] == EMPTY)
                        {
                            if (i % 2 == 0 && j % 2 == 0)
                            {
                                try {
                                    board.move(j, i);
                                    board.turn();
                                    // Call minimax recursively and choose the maximum value
                                    best = Math.max(best, minimax(board, depth + 1, !isMax, alpha, beta));
                                    alpha = Math.max(alpha, best);
                                    board.undoMove(j, i);
                                } catch (InputMismatchException ignored) {}
                            }
                            else if ( (i % 2 == 0 && j % 2 == 1 && j <16 && i < 15) ||
                                    (i % 2 == 1 && j % 2 == 0 && j <15 && i < 16) )
                            {
                                try {
                                    board.placeWall(j, i);
                                    board.turn();
                                    // Call minimax recursively and choose the maximum value
                                    best = Math.max(best, minimax(board, depth + 1, !isMax, alpha, beta));
                                    alpha = Math.max(alpha, best);
                                    board.undoMove(j, i);
                                } catch (InputMismatchException ignored) {}
                            }

                        }
                    }
                }
                return best;
            }
            // If this minimizer's move
            else
            {
                if (depth + 1 == best_depth || depth + 1 > 20)
                    return min;

                int best = max;

                // Traverse all cells
                for (int i = 0; i < 17; i++)
                {
                    for (int j = 0; j < 17; j++)
                    {
                        // Check if cell is empty
                        if (board.getBoard()[i][j] == EMPTY)
                        {
                            if (i % 2 == 0 && j % 2 == 0)
                            {
                                try {
                                    board.move(j, i);
                                    board.turn();
                                    // Call minimax recursively and choose the minimum value
                                    best = Math.min(best, minimax(board, depth + 1, !isMax, alpha, beta));
                                    beta = Math.min(beta, best);
                                    board.undoMove(j, i);
                                } catch (InputMismatchException ignored) {}
                            }
                            else if ( (i % 2 == 0 && j % 2 == 1 && j <16 && i < 15) ||
                                    (i % 2 == 1 && j % 2 == 0 && j <15 && i < 16) )
                            {
                                try {
                                    board.placeWall(j, i);
                                    board.turn();
                                    // Call minimax recursively and choose the minimum value
                                    best = Math.min(best, minimax(board, depth + 1, !isMax, alpha, beta));
                                    beta = Math.min(beta, best);
                                    board.undoMove(j, i);
                                } catch (InputMismatchException ignored) {}
                            }
                        }
                    }
                }
                return best;
            }
        }
        // If Maximizer has won the game return his/her evaluated score
        else if (score == 100) {
            if (depth < best_depth)
                best_depth = depth;
            return score - depth;
        }

        // If Minimizer has won the game return his/her evaluated score
        else {
            if (depth < best_depth)
                best_depth = depth;
            return score + depth;
        }

    }

    // This is the evaluation function
    private int evaluate(Board board)
    {
        // Else if none of them have won then return 0
        if (board.win() == null) {
            return 0;
        }
        // Checking for player1 or player2 victory.
        else if (board.win().getId() == this.id) {
            return 1000;
        }
        else {
            return -1000;
        }
    }

    private int[] moveRandomly() {
        int[] randMove = new int[2];
        Random random = new Random();
        int i = random.nextInt(17) - 1;
        int j = random.nextInt(17) - 1;
        randMove[0] = i;
        randMove[1] = j;
        return randMove;
    }
}
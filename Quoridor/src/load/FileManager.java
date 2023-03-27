package load;

import game.Board;
import game.Player;
import graphics.Controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.Scanner;

public class FileManager {
    public static void save(Board board) {
        Formatter savior;
        try {
            savior = new Formatter(System.getProperty("user.dir") + "/Quoridor/src/load/" +
                    new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + ".csv");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        savior.format("%s,%c,%d\n", board.getPlayer1().getName(), board.getPlayer1().getId(), board.getPlayer1().getWalls());
        savior.format("%s,%c,%d\n", board.getPlayer2().getName(), board.getPlayer2().getId(), board.getPlayer2().getWalls());
        savior.format("%d\n", (board.getTurn() == board.getPlayer1()) ? 1 : 2);
        int[][] matrix = board.getBoard();
        for (int[] row : matrix) {
            for (int cell : row)
                savior.format("%d,", cell);
            savior.format("\n");
        }

        savior.close();
    }

    public static ArrayList <String[]> load() {
        ArrayList<String[]> info = new ArrayList<>();
        FilenameFilter csv_finder = (dir, name) -> name.toLowerCase().endsWith(".csv");

        File[] save_files = new File(System.getProperty("user.dir") + "/load").listFiles(csv_finder);
        if (save_files != null)
            for (File save_file : save_files) {
                String player1name = null, player2name = null;
                try {
                    Scanner loader = new Scanner(save_file);
                    player1name = loader.nextLine().split(",")[0];
                    player2name = loader.nextLine().split(",")[0];
                    loader.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                info.add(new String[] {save_file.getName(), player1name, player2name});
            }

        return info;
    }

    public static boolean load(File game_data, Controller controller) {
        Scanner loader;
        try {
            loader = new Scanner(game_data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        String[] player1info = loader.nextLine().split(",");
        String[] player2info = loader.nextLine().split(",");
        Board board = new Board(new Player(player1info[0], player1info[1].charAt(0), Integer.parseInt(player1info[2])),
                                new Player(player2info[0], player2info[1].charAt(0), Integer.parseInt(player2info[2])),
                                Integer.parseInt(loader.nextLine()));

        for (int i = 0; i < 17; ++i) {
            String[] cells = loader.nextLine().split(",");
            for (int j = 0; j < 17; j++)
                if (!cells[j].equals("0"))
                    board.setCell(i, j, Integer.parseInt(cells[j]));
        }
        loader.close();

        controller.setBoard(board);
        return true;
    }
}

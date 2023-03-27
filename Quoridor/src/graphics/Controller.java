package graphics;

import game.AI;
import game.Board;
import game.Cup;
import game.Player;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import load.FileManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.concurrent.TimeUnit;

public class Controller {
    private Play play;
    public void setPlay(Play play) { this.play = play; }
    // menu methods
    @FXML
    protected void gotoGameModes() throws IOException { play.gotoFXML("game modes.fxml"); }
    @FXML
    protected void gotoLoad() throws IOException { play.gotoFXML("load.fxml"); initializeLoad(); }
    @FXML
    protected void Exit() { play.getPrimaryStage().close(); }

    // game modes gotos
    @FXML
    protected void gotoMenu() throws IOException { play.gotoFXML("main menu.fxml"); }
    @FXML
    protected void gotoPlayerSettings() throws IOException {
        play.gotoFXML("player settings.fxml");
        bot1difficulty = (ChoiceBox<String>) play.getScene().lookup("#bot1difficulty");
        bot2difficulty = (ChoiceBox<String>) play.getScene().lookup("#bot2difficulty");
        bot1difficulty.setItems(FXCollections.observableArrayList(new String[] {"1", "2", "3"}));
        bot2difficulty.setItems(FXCollections.observableArrayList(new String[] {"1", "2", "3"}));
    }
    @FXML
    protected void gotoCup() throws IOException {
        play.gotoFXML("tournament menu.fxml");

        name_input = (TextField)play.getScene().lookup("#name_input");
        names = (Label)play.getScene().lookup("#names");
        error = (Label)play.getScene().lookup("#error");

        name_input.setOnKeyPressed(keyEvent -> {
            ArrayList<String> names_array = new ArrayList<>(Arrays.asList(names.getText().split("\n")));
            if (keyEvent.getCode() == KeyCode.ENTER) {
                if (name_input.getText().equals(""))
                    error.setText("name cannot be empty");
                else if (names_array.contains(name_input.getText()))
                    error.setText("name cannot be duplicate");
                else {
                    error.setText("");
                    names.setText(((names.getText().equals("")) ? "" : names.getText() + "\n") + name_input.getText());
                    name_input.setText("");
                }
            }
        });
    }

    // player settings
    @FXML protected TextField player1name;
    @FXML protected TextField player2name;
    @FXML protected ToggleButton bot1;
    @FXML protected ToggleButton bot2;
    @FXML protected ChoiceBox <String> bot1difficulty;
    @FXML protected ChoiceBox <String> bot2difficulty;
    @FXML protected Label status;
    // go to game
    @FXML
    protected void gotoNewGame() throws IOException {
        if (player1name.getText().equals("") || player2name.getText().equals("")) {
            status.setText("The names can NOT be empty");
            return;
        }
        else if (player1name.getText().equals(player2name.getText())) {
            status.setText("The names should NOT match");
            return;
        }
        Player player1, player2;
        if (bot1.isSelected()) {
            player1name.setText(player1name.getText() + " (BOT)");
            int difficulty;
            if (bot1difficulty.getValue().equals("1"))
                difficulty = 1;
            else
                difficulty = 2;
            player1 = new AI(player1name.getText(), 'U', 10, difficulty);
        }
        else
            player1 = new Player(player1name.getText(), 'U', 10);

        if (bot2.isSelected()) {
            player2name.setText(player2name.getText() + " (BOT)");
            int difficulty;
            if (bot2difficulty.getValue().equals("1"))
                difficulty = 1;
            else
                difficulty = 2;
            player2 = new AI(player2name.getText(), 'D', 10, difficulty);
        }
        else
            player2 = new Player(player2name.getText(), 'D', 10);

        board = new Board(player1, player2, 1);
        gotoGame();
        cup_is_on = false;
    }
    @FXML
    protected void gotoGame() throws IOException { play.gotoFXML("game.fxml"); initializeGame(); }

    // tournament objects
    @FXML protected Label names;
    @FXML protected TextField name_input;
    @FXML protected TextField number_of_bots;
    @FXML protected Label error;
    private Cup tournament;

    @FXML
    protected void cup() {
        ArrayList <Player> players = new ArrayList<>();
        String[] names = this.names.getText().split("\n");
        for (String name : names)
            players.add(new Player(name, 'U', 10));
//        int number_of_bots = Integer.parseInt(this.number_of_bots.getText());
//        for (int i = 0; i < number_of_bots; i++) { todo: bots
//            players.add(new AI());
//        }
        tournament = new Cup(players);
        cup_is_on = true;
        play();
    }

    private boolean play() {
        if (tournament.levelFinished())
            return false;
        tournament.nextLevel();
        Player[] gamers = tournament.play();
        try { gotoNewGame(gamers[0], gamers[1]);
        } catch (IOException ioException) { ioException.printStackTrace(); }
        return true;
    }

    protected void gotoNewGame(Player player1, Player player2) throws IOException {
        board = new Board(player1, player2, 1);
        gotoGame();
    }

    // game objects
    private Board board;
    public void setBoard(Board board) { this.board = board; }
    // shall be changed objects
    @FXML protected Label player1info;
    @FXML protected Label player1walls;
    @FXML protected Label player2info;
    @FXML protected Label player2walls;
    @FXML protected AnchorPane table;
    @FXML protected AnchorPane bead1;
    @FXML protected AnchorPane bead2;
    private boolean cup_is_on = false;
    // game methods
    protected void initializeGame() {
        // creating the board
        table = (AnchorPane)(play.getScene().lookup("#table"));
        final double NARROW = 10, THICK = 40;
        // initialize cells
        int y = 0;
        for (int i = 0; i < 17; ++i) {
            int x = 0;
            for (int j = 0; j < 17; j++) {
                String id = "cell" + ((i < 10)? "0" + i : i) + ((j < 10)? "0" + j : j);
                AnchorPane cell = new AnchorPane();
                cell.setId(id);

                cell.setLayoutX(x);
                cell.setLayoutY(y);
                if (i % 2 == 0)
                    cell.setPrefHeight(THICK);
                else {
                    cell.setPrefHeight(NARROW);
                }
                if (j % 2 == 0) {
                    if (i % 2 == 0)
                        cell.setStyle("-fx-background-color: slategrey");
                    else
                        cell.setStyle("-fx-background-color: rgb(150,80,58)");
                    cell.setPrefWidth(THICK);
                    x += THICK;
                }
                else {
                    cell.setStyle("-fx-background-color: rgb(150,80,58)");
                    cell.setPrefWidth(NARROW);
                    x += NARROW;
                }
                // add functionality
                if (((j % 2 == 0 && i % 2 == 1) || (j % 2 == 1 && i % 2 == 0)) && i != 16 && j != 16) {
                    cell.setOnMouseClicked(this::placeWall);
                    cell.setOnMouseEntered(this::canPlace);
                    cell.setOnMouseExited(this::baseColor);
                }
                else if (cell.getStyle().equals("-fx-background-color: slategrey")) {
                    cell.setOnMouseClicked(this::beadMover);
                    cell.setOnMouseEntered(this::canMove);
                    cell.setOnMouseExited(this::baseColor);
                }

                table.getChildren().add(cell);
            }
            if (i % 2 == 0)
                y += THICK;
            else
                y += NARROW;
        }
        // initialize the beads
        {
            bead1 = new AnchorPane();
            bead1.setPrefSize(THICK, THICK);
            int x1 = board.getPlayer1().getBead().getX();
            int y1 = board.getPlayer1().getBead().getY();
            String id1 = "#cell" + ((y1 < 10) ? "0" + y1 : y1) + ((x1 < 10) ? "0" + x1 : x1);
            bead1.setLayoutX(play.getScene().lookup(id1).getLayoutX());
            bead1.setLayoutY(play.getScene().lookup(id1).getLayoutY());

            bead2 = new AnchorPane();
            bead2.setPrefSize(THICK, THICK);
            int x2 = board.getPlayer2().getBead().getX();
            int y2 = board.getPlayer2().getBead().getY();
            String id2 = "#cell" + ((y2 < 10) ? "0" + y2 : y2) + ((x2 < 10) ? "0" + x2 : x2);
            bead2.setLayoutX(play.getScene().lookup(id2).getLayoutX());
            bead2.setLayoutY(play.getScene().lookup(id2).getLayoutY());
            // adding style
            bead1.setStyle("-fx-background-size: 40 40;" +
                    "-fx-background-radius: 40;" +
                    "-fx-border-radius: 40;" +
                    "-fx-background-color: black");

            bead2.setStyle("-fx-background-size: 40 40;" +
                    "-fx-background-radius: 40;" +
                    "-fx-border-radius: 40;" +
                    "-fx-background-color: white");
            // add to the table
            table.getChildren().addAll(bead1, bead2);
        }
        // initialize the labels
        player1info = (Label) (play.getScene().lookup("#player1info"));
        player2info = (Label) (play.getScene().lookup("#player2info"));
        player1walls = (Label) (play.getScene().lookup("#player1walls"));
        player2walls = (Label) (play.getScene().lookup("#player2walls"));
        {
            // Align the names
            String name1 = board.getPlayer1().getName(), name2 = board.getPlayer2().getName();
            while (name1.length() < name2.length())
                name1 += " ";
            while (name2.length() < name1.length())
                name2 += " ";
            // initialize player1's label
            player1info.setText(name1);
            player1walls.setText("Remaining Walls: " + board.getPlayer1().getWalls());
            player1info.setTextFill(Color.BLACK);
            player1walls.setTextFill(Color.BLACK);
            // initialize player2's label
            player2info.setText(name2);
            player2walls.setText("Remaining Walls: " + board.getPlayer2().getWalls());
            player2info.setTextFill(Color.DARKGREY);
            player2walls.setTextFill(Color.DARKGREY);
        }
    }
    @FXML
    protected void canMove(MouseEvent event) {
        int index = Integer.parseInt(((AnchorPane)event.getSource()).getId().substring(4));
        int x = index % 100, y = index / 100;
        if (board.canMove(x, y))
            if (board.getTurn().getId() == 'U')
                ((AnchorPane) event.getSource()).setStyle("-fx-background-color: #5c5c5c");
            else
                ((AnchorPane) event.getSource()).setStyle("-fx-background-color: #bab5ab");
        else
            ((AnchorPane) event.getSource()).setStyle("-fx-background-color: rgba(238,0,0,0.54)");
    }
    @FXML
    protected void canPlace(MouseEvent event) {
        int index = Integer.parseInt(((AnchorPane)event.getSource()).getId().substring(4));
        int x = index % 100, y = index / 100;
        String color = "-fx-background-color: ";
        String id1 = "#cell";
        String id2 = "#cell";
        if (y % 2 == 1) {
            String y_value = ((y < 10) ? "0" + y : y).toString();
            id1 += y_value + ((x + 1 < 10) ? "0" + (x + 1) : x + 1);
            id2 += y_value + ((x + 2 < 10) ? "0" + (x + 2) : x + 2);
        }
        else {
            String x_value = ((x < 10) ? "0" + x : x).toString();
            id1 += ((y + 1 < 10) ? "0" + (y + 1) : y + 1) + x_value;
            id2 += ((y + 2 < 10) ? "0" + (y + 2) : y + 2) + x_value;
        }
        if (board.canPlaceWall(x, y))
            color += "cornflowerblue";
        else
            color += "orangered";

        ((AnchorPane) event.getSource()).setStyle(color);
        play.getScene().lookup(id1).setStyle(color);
        play.getScene().lookup(id2).setStyle(color);
    }
    @FXML
    protected void beadMover(MouseEvent event) {
        int index = Integer.parseInt(((AnchorPane) event.getSource()).getId().substring(4));
        int x = index % 100, y = index / 100;
        try {
            this.board.move(x, y);
            // move the on-screen bead
            AnchorPane clicked = (AnchorPane) event.getSource();
            AnchorPane bead = (board.getTurn().getId() == 'U') ? bead1 : bead2;

            bead.setLayoutX(clicked.getLayoutX());
            bead.setLayoutY(clicked.getLayoutY());

            win();
            this.board.turn();

            if (board.getTurn().getClass().getSimpleName().equals("AI"))
                click(((AI)board.getTurn()).findBestMove(this.board));
        } catch (InputMismatchException exception) {
            try { TimeUnit.MILLISECONDS.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }
    @FXML
    protected void placeWall(MouseEvent event) {
        AnchorPane wall_space = (AnchorPane) event.getSource();
        int index = Integer.parseInt(wall_space.getId().substring(4));
        int x = index % 100, y = index / 100;
        try {
            this.board.placeWall(x, y);
            // change the on-screen wall
            wall_space.setStyle("-fx-background-color: #b49a51");
            String id1 = "#cell";
            String id2 = "#cell";
            if (y % 2 == 1) {
                String y_value = ((y < 10) ? "0" + y : y).toString();
                id1 += y_value + ((x + 1 < 10) ? "0" + (x + 1) : x + 1);
                id2 += y_value + ((x + 2 < 10) ? "0" + (x + 2) : x + 2);
            }
            else {
                String x_value = ((x < 10) ? "0" + x : x).toString();
                id1 += ((y + 1 < 10) ? "0" + (y + 1) : y + 1) + x_value;
                id2 += ((y + 2 < 10) ? "0" + (y + 2) : y + 2) + x_value;
            }
            play.getScene().lookup(id1).setStyle("-fx-background-color: #b49a51");
            play.getScene().lookup(id2).setStyle("-fx-background-color: #b49a51");
            // removing listeners
            {
                wall_space.setOnMouseExited(null);
                wall_space.setOnMouseEntered(null);
                wall_space.setOnMouseClicked(null);
                play.getScene().lookup(id1).setOnMouseExited(null);
                play.getScene().lookup(id1).setOnMouseEntered(null);
                play.getScene().lookup(id1).setOnMouseClicked(null);
                play.getScene().lookup(id2).setOnMouseExited(null);
                play.getScene().lookup(id2).setOnMouseEntered(null);
                play.getScene().lookup(id2).setOnMouseClicked(null);
            }
            // change number of walls
            Player turn = this.board.getTurn();
            Label player_info = (turn.getId() == 'U') ? player1walls : player2walls;
            String info = player_info.getText().substring(0, player_info.getText().length() - 2);
            player_info.setText(info + "0" + turn.getWalls());

            this.board.turn();

            if (board.getTurn().getClass().getSimpleName().equals("AI"))
                click(((AI)board.getTurn()).findBestMove(this.board));
        } catch (InputMismatchException exception) {
            try { TimeUnit.MILLISECONDS.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }
    // clicks where the AI tells it to
    private void click(int[] coordinates) {
        String id = "#cell" + ((coordinates[0] < 10)? "0" + coordinates[0] : coordinates[0]) +
                ((coordinates[1] < 10)? "0" + coordinates[1] : coordinates[1]);
        AnchorPane cell = (AnchorPane) play.getScene().lookup(id);
        // click on the cell
        cell.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0,
                0, 0, 0, MouseButton.PRIMARY, 1, true, true, true,
                true, true, true, true, true, true, true, null));
    }
    @FXML // return to initial colors
    protected void baseColor(MouseEvent event) {
        int index = Integer.parseInt(((AnchorPane)event.getSource()).getId().substring(4));
        int x = index % 100, y = index / 100;
        if (x % 2 == 0 && y % 2 == 0)
            ((AnchorPane) event.getSource()).setStyle("-fx-background-color: slategrey");
        else if (!((AnchorPane) event.getSource()).getStyle().equals("-fx-background-color: khaki")) {
            ((AnchorPane) event.getSource()).setStyle("-fx-background-color: rgb(150,80,58)");
            String id1 = "#cell";
            String id2 = "#cell";
            if (y % 2 == 1) {
                String y_value = ((y < 10) ? "0" + y : y).toString();
                id1 += y_value + ((x + 1 < 10) ? "0" + (x + 1) : x + 1);
                id2 += y_value + ((x + 2 < 10) ? "0" + (x + 2) : x + 2);
                if (board.getBoard()[y][x + 1] != 11)
                    play.getScene().lookup(id1).setStyle("-fx-background-color: rgb(150,80,58)");
                else
                    play.getScene().lookup(id1).setStyle("-fx-background-color: #b49a51");

                if (board.getBoard()[y][x + 2] != 11)
                    play.getScene().lookup(id2).setStyle("-fx-background-color: rgb(150,80,58)");
                else
                    play.getScene().lookup(id2).setStyle("-fx-background-color: #b49a51");
            }
            else {
                String x_value = ((x < 10) ? "0" + x : x).toString();
                id1 += ((y + 1 < 10) ? "0" + (y + 1) : y + 1) + x_value;
                id2 += ((y + 2 < 10) ? "0" + (y + 2) : y + 2) + x_value;
                if (board.getBoard()[y + 1][x] != 11)
                    play.getScene().lookup(id1).setStyle("-fx-background-color: rgb(150,80,58)");
                else
                    play.getScene().lookup(id1).setStyle("-fx-background-color: #b49a51");

                if (board.getBoard()[y + 2][x] != 11)
                    play.getScene().lookup(id2).setStyle("-fx-background-color: rgb(150,80,58)");
                else
                    play.getScene().lookup(id2).setStyle("-fx-background-color: #b49a51");
            }
        }
    }
    // do the winner stuff
    private void win() {
        Player winner = board.win();
        if (winner == null)
            return;

        int winner_number;
        Label winner_info;
        Label loser_info;
        if (winner.getId() == 'U') {
            winner_info = player1info;
            loser_info = player2info;
            winner_number = 1;
        }
        else {
            winner_info = player2info;
            loser_info = player1info;
            winner_number = 2;
        }

        winner_info.setFont(new Font("Segoe UI Semibold", 20));
        winner_info.setText("You Won!!");
        loser_info.setFont(new Font("Segoe UI Semibold", 20));
        loser_info.setText("You Lost :)");

        for (int i = 0; i < 17; ++i)
            for (int j = 0; j < 17; j++) {
                String id = "#cell" + ((i < 10) ? "0" + i : i) + ((j < 10) ? "0" + j : j);
                AnchorPane cell = (AnchorPane) (play.getScene().lookup(id));
                // remove functionality
                cell.setOnMouseClicked(null);
                cell.setOnMouseEntered(null);
                cell.setOnMouseExited(null);
                if (!cup_is_on) {
                    if (i == 6) {
                        if (j == 6) {
                            cell.setStyle("-fx-background-color: mediumaquamarine");
                            Label label = new Label("try");
                            label.setTextFill(Color.BLACK);
                            label.setFont(new Font("Arial Rounded MT Bold", 12));
                            label.setAlignment(Pos.CENTER);
                            label.setPrefSize(40, 40);
                            cell.getChildren().add(label);
                            if (bead1.getLayoutX() == cell.getLayoutX() && bead1.getLayoutY() == cell.getLayoutY())
                                bead1.setVisible(false);
                            if (bead2.getLayoutX() == cell.getLayoutX() && bead2.getLayoutY() == cell.getLayoutY())
                                bead2.setVisible(false);
                        }
                        else if (j == 8) {
                            cell.setStyle("-fx-background-color: mediumaquamarine");
                            Label label = new Label("again?");
                            label.setTextFill(Color.BLACK);
                            label.setFont(new Font("Arial Rounded MT Bold", 12));
                            label.setAlignment(Pos.CENTER);
                            label.setPrefSize(40, 40);
                            cell.getChildren().add(label);
                            if (bead1.getLayoutX() == cell.getLayoutX() && bead1.getLayoutY() == cell.getLayoutY())
                                bead1.setVisible(false);
                            if (bead2.getLayoutX() == cell.getLayoutX() && bead2.getLayoutY() == cell.getLayoutY())
                                bead2.setVisible(false);
                        }
                    }
                    else if(i == 8) {
                        if (j == 6) {
                            cell.setStyle("-fx-background-color: limegreen");
                            Label label = new Label("YES");
                            label.setTextFill(Color.BLACK);
                            label.setFont(new Font("Arial Rounded MT Bold", 12));
                            label.setAlignment(Pos.CENTER);
                            label.setPrefSize(40, 40);
                            label.setOnMouseClicked(event -> {
                                try {
                                    gotoNewGame(new Player(board.getPlayer1().getName(), 'U', 10),
                                                new Player(board.getPlayer2().getName(), 'D', 10));
                                } catch (IOException ioException) { ioException.printStackTrace(); }
                            });
                            cell.getChildren().add(label);
                            if (bead1.getLayoutX() == cell.getLayoutX() && bead1.getLayoutY() == cell.getLayoutY())
                                bead1.setVisible(false);
                            if (bead2.getLayoutX() == cell.getLayoutX() && bead2.getLayoutY() == cell.getLayoutY())
                                bead2.setVisible(false);
                        }
                        else if (j == 8) {
                            cell.setStyle("-fx-background-color: red");
                            Label label = new Label("NO");
                            label.setTextFill(Color.BLACK);
                            label.setFont(new Font("Arial Rounded MT Bold", 12));
                            label.setAlignment(Pos.CENTER);
                            label.setPrefSize(40, 40);
                            label.setOnMouseClicked(event -> {
                                try { gotoGameModes(); } catch (IOException ioException) {
                                    ioException.printStackTrace();
                                }
                            });
                            cell.getChildren().add(label);
                            if (bead1.getLayoutX() == cell.getLayoutX() && bead1.getLayoutY() == cell.getLayoutY())
                                bead1.setVisible(false);
                            if (bead2.getLayoutX() == cell.getLayoutX() && bead2.getLayoutY() == cell.getLayoutY())
                                bead2.setVisible(false);
                        }
                    }
                }
            }

        if (cup_is_on)
            cupOptimizer(winner_number);
    }

    private void cupOptimizer(int winner_number) {
        tournament.won(winner_number);
        if (!play()) {
            if (tournament.finished() == null)
                tournament.nextLevel();
            // todo
//            else {
//                showWinner();
//                return;
//            }
            play();
        }
    }
    @FXML
    protected void save() { FileManager.save(this.board); }

    // shall be changed load object
    @FXML protected AnchorPane load_pane;
    @FXML protected ListView<String> load_files;
    // load methods
    private void initializeLoad() {
        // initialize load files' list
        {
            load_pane = (AnchorPane)(play.getScene().lookup("#load_pane"));

            load_files = new ListView<>();
            load_files.setPrefSize(390, 400);
            load_files.setLayoutX(65);
            load_files.setLayoutY(185);

            load_pane.getChildren().add(load_files);
        }
        // get files' names
        ArrayList<String[]> files = FileManager.load();
        // add them to the list
        for (String[] details : files)
            load_files.getItems().add(details[1] + '\t' + details[2] + '\t' + details[0]);
        // set a listener for the items
        load_files.setOnMouseClicked(event -> {
            String file_name = load_files.getSelectionModel().getSelectedItem().split("\t")[2];
            if (FileManager.load(new File(System.getProperty("user.dir") + "/load/" + file_name + ".csv"), this))
                try {
                    gotoGame();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            else
                System.out.println("Something went wrong");
        });
    }
}

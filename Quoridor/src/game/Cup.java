package game;
import java.util.*;

public class Cup {
    private ArrayList<Player> players;
    private Integer skipper;
    private int turn = 0;

    public Cup(ArrayList<Player> players) {
        this.players = players;
    }

    public void nextLevel() {
        Collections.shuffle(players);
        if (players.size() % 2 != 0) {
            int i = 0;
            while (players.get(i).skip_level)
                ++i;
            players.get(i).skip_level = true;
            skipper = i;
        }
        else
            skipper = null;

        turn = 0;
    }

    public Player[] play() {
        Player[] gamers = new Player[2];
        System.out.println(skipper);
        if (skipper != null)
            System.out.println(players.get(skipper).getName());

        if (skipper != null)
            if (turn == skipper)
                turn++;
        gamers[0] = players.get(turn++);
        gamers[0].setId('U');

        if (skipper != null)
            if (turn == skipper)
                turn++;
        gamers[1] = players.get(turn);
        gamers[1].setId('D');

        return new Player[] {gamers[0], gamers[1]};
    }

    public void won(int i) {
        if (i == 1) {
            players.remove(turn);
        }
        else if (i == 2)
            players.remove(turn - 1);
        turn++;
    }

    public boolean levelFinished() { return turn >= players.size() - 1; }

    public Player finished() { return (players.size() == 1) ? players.get(0) : null; }
}

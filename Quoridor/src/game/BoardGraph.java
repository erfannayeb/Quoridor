package game;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;

class BoardGraph {
    private Graph<String, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);

    public BoardGraph() {
        for (int i = 0; i < 9; ++i)
            for (int j = 0; j < 9; ++j) {
                String name = "V" + i + j;
                graph.addVertex(name);
            }

        for (int i = 0; i < 8; ++i)
            for (int j = 0; j < 8; ++j) {
                graph.addEdge("V" + i + j, "V" + (i + 1) + (j));
                graph.addEdge("V" + i + j, "V" + (i) + (j + 1));
            }

        for (int i = 0; i < 8; ++i)
            graph.addEdge("V" + i + "8", "V" + (i + 1) + "8");

        for (int j = 0; j < 8; ++j)
            graph.addEdge("V" + "8" + j, "V" + "8" + (j + 1));
    }

    //check if we remove edges is any path exist from bead to win side or not
    public boolean isPathExist(Player player, Graph<String, DefaultEdge> clonedGraph) {
        int row = player.getBead().getY() / 2;
        int column = player.getBead().getX() / 2;
        int targetRow = player.getId() == 'U' ? 8 : 0;
        ConnectivityInspector<String, DefaultEdge> connectivityInspector = new ConnectivityInspector<>(clonedGraph);

        for (int i = 0; i < 9; ++i)
            if (connectivityInspector.pathExists("V" + row + column, "V" + targetRow + i))
                return true;

        return false;
    }

    public boolean isPathExist(Player player1, Player player2, Graph<String, DefaultEdge> clonedGraph) {
        return isPathExist(player1, clonedGraph) && isPathExist(player2, clonedGraph);
    }

    //it get x and y to place wall and if can do it remove 2 edges which wall cut.
    public void removeEdgeByWall(int x, int y, Graph<String, DefaultEdge> graph) {
        int i, j;
        //remove edges when place horizontal wall
        if (y % 2 == 1 && x % 2 == 0) {
            i = (y - 1) / 2;
            j = x / 2;
            graph.removeEdge("V" + i + j, "V" + (i + 1) + j);
            graph.removeEdge("V" + i + (j + 1), "V" + (i + 1) + (j + 1));
        }
        //remove edges when place vertical wall
        else if (y % 2 == 0 && x % 2 == 1) {
            i = y / 2;
            j = (x - 1) / 2;
            graph.removeEdge("V" + i + j, "V" + i + (j + 1));
            graph.removeEdge("V" + (i + 1) + j, "V" + (i + 1) + (j + 1));
        }
    }

    public void undoRemoveEdges(int x, int y) {
        int i, j;
        //remove edges when place horizontal wall
        if (y % 2 == 1 && x % 2 == 0) {
            i = (y - 1) / 2;
            j = x / 2;
            graph.addEdge("V" + i + j, "V" + (i + 1) + j);
            graph.addEdge("V" + i + (j + 1), "V" + (i + 1) + (j + 1));
        }
        //remove edges when place vertical wall
        else if (y % 2 == 0 && x % 2 == 1) {
            i = y / 2;
            j = (x - 1) / 2;
            graph.addEdge("V" + i + j, "V" + i + (j + 1));
            graph.addEdge("V" + (i + 1) + j, "V" + (i + 1) + (j + 1));
        }
    }

    //check if can remove edge, it remove edge and return true.
    public boolean allowedRemoveEdge(Player player1, Player player2, int x, int y) {
        Graph<String, DefaultEdge> clonedGraph = new DefaultUndirectedGraph<>(DefaultEdge.class);
        Graphs.addGraph(clonedGraph, graph);

        removeEdgeByWall(x, y, clonedGraph);

        if (isPathExist(player1, player2, clonedGraph)) {
            removeEdgeByWall(x, y, graph);
            return true;
        }
        return false;
    }

    //check if we can remove edge or not.
    public boolean canRemoveEdge(Player player1, Player player2, int x, int y) {
        Graph<String, DefaultEdge> clonedGraph = new DefaultUndirectedGraph<>(DefaultEdge.class);
        Graphs.addGraph(clonedGraph, graph);

        removeEdgeByWall(x, y, clonedGraph);

        return isPathExist(player1, player2, clonedGraph);
    }
}

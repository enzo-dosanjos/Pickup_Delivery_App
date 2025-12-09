package domain.model;

import java.util.Arrays;

/**
 * Represents a complete graph with a specified number of vertices and edge costs.
 */
public class GrapheComplet implements Graphe {


    long[] sommets; // Array of vertex identifiers. The position in the array is used as the intersection's unique ID to link the vertices in the graph to the intersection.


    int nbSommets; // The number of vertices in the graph, which is also the size of the cost matrix.


    double[][] cout; // Cost matrix representing the costs between each pair of vertices. The cost from vertex i to vertex j is stored in cout[i][j].

    /**
     * Constructs a complete graph with the specified number of vertices.
     * Initializes the cost matrix with default values of Double.MAX_VALUE.
     *
     * @param nbSommets the number of vertices in the graph
     */
    public GrapheComplet(int nbSommets) {
        this.nbSommets = nbSommets;
        this.cout = new double[nbSommets][nbSommets];
        for (int i = 0; i < nbSommets; i++) Arrays.fill(cout[i], Double.MAX_VALUE);
    }

    /**
     * Constructs a complete graph with the specified vertices and number of vertices.
     * Initializes the cost matrix with default values of Double.MAX_VALUE.
     *
     * @param sommets an array of vertex identifiers
     * @param nbSommets the number of vertices in the graph
     */
    public GrapheComplet(long[] sommets, int nbSommets) {
        this.sommets = Arrays.copyOf(sommets, nbSommets);
        this.nbSommets = nbSommets;
        this.cout = new double[nbSommets][nbSommets];
        for (int i = 0; i < nbSommets; i++) Arrays.fill(cout[i], Double.MAX_VALUE);
    }


    public long[] getSommets() {
        return sommets;
    }

    @Override
    public int getNbSommets() {
        return nbSommets;
    }


    @Override
    public double getCout(int i, int j) {
        if (i < 0 || i >= nbSommets || j < 0 || j >= nbSommets)
            return -1;
        return cout[i][j];
    }


    public double[][] getCout() {
        return cout;
    }


    public void setCout(int i, int j, double newVal) {
        cout[i][j] = newVal;
    }

    /**
     * Checks if there is an edge between two vertices.
     * Since the graph is complete, an edge exists if the vertices are within bounds and are not the same.
     *
     * @param i the source vertex
     * @param j the destination vertex
     * @return true if there is an edge; false otherwise
     */
    @Override
    public boolean estArc(int i, int j) {
        if (i < 0 || i >= nbSommets || j < 0 || j >= nbSommets)
            return false;
        return i != j;
    }


    @Override
    public String toString() {
        return "GrapheComplet{" +
                "sommets=" + Arrays.toString(sommets) +
                ", nbSommets=" + nbSommets +
                ",\ncouts=" + Arrays.deepToString(cout) +
                '}';
    }
}
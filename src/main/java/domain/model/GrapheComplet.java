package domain.model;

import java.util.Arrays;

/**
 * Represents a complete graph with a specified number of vertices and edge costs.
 */
public class GrapheComplet implements Graphe {

    /** Array of vertex identifiers. */
    long[] sommets;

    /** The number of vertices in the graph. */
    int nbSommets;

    /** A 2D array representing the cost of edges between vertices. */
    double[][] cout;

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

    /**
     * Retrieves the array of vertex identifiers.
     *
     * @return the array of vertex identifiers
     */
    public long[] getSommets() {
        return sommets;
    }

    /**
     * Retrieves the number of vertices in the graph.
     *
     * @return the number of vertices
     */
    @Override
    public int getNbSommets() {
        return nbSommets;
    }

    /**
     * Retrieves the cost of the edge between two vertices.
     *
     * @param i the source vertex
     * @param j the destination vertex
     * @return the cost of the edge if valid; -1 if the vertices are out of bounds
     */
    @Override
    public double getCout(int i, int j) {
        if (i < 0 || i >= nbSommets || j < 0 || j >= nbSommets)
            return -1;
        return cout[i][j];
    }

    /**
     * Retrieves the entire cost matrix.
     *
     * @return the 2D array representing edge costs
     */
    public double[][] getCout() {
        return cout;
    }

    /**
     * Updates the cost of the edge between two vertices.
     *
     * @param i the source vertex
     * @param j the destination vertex
     * @param newVal the new cost value
     */
    public void setCout(int i, int j, double newVal) {
        cout[i][j] = newVal;
    }

    /**
     * Checks if there is an edge between two vertices.
     * An edge exists if the vertices are within bounds and are not the same.
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

    /**
     * Returns a string representation of the graph, including vertices and edge costs.
     *
     * @return the string representation of the graph
     */
    @Override
    public String toString() {
        return "GrapheComplet{" +
                "sommets=" + Arrays.toString(sommets) +
                ", nbSommets=" + nbSommets +
                ",\ncouts=" + Arrays.deepToString(cout) +
                '}';
    }
}
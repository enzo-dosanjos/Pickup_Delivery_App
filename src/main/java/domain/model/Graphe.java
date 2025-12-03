package domain.model;

/**
 * Represents a graph structure with methods to retrieve the number of vertices,
 * the cost of an edge, and to check if an edge exists between two vertices.
 */
public interface Graphe {

    /**
     * Retrieves the number of vertices in the graph.
     *
     * @return the number of vertices in <code>this</code> graph
     */
    public abstract int getNbSommets();

    /**
     * Retrieves the cost of the edge between two vertices.
     *
     * @param i the source vertex
     * @param j the destination vertex
     * @return the cost of the edge (i, j) if it exists; -1 otherwise
     */
    public abstract double getCout(int i, int j);

    /**
     * Checks if there is an edge between two vertices.
     *
     * @param i the source vertex
     * @param j the destination vertex
     * @return true if <code>(i, j)</code> is an edge in <code>this</code> graph; false otherwise
     */
    public abstract boolean estArc(int i, int j);
}
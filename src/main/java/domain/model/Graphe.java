package domain.model;

/**
 * Represents a graph structure with methods to retrieve the number of vertices,
 * the cost of an edge, and to check if an edge exists between two vertices.
 */
public interface Graphe {


    public abstract int getNbSommets();


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
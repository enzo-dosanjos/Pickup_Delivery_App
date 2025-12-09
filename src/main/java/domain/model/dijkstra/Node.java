package domain.model.dijkstra;

/**
 * Represents a node in the Dijkstra algorithm, containing a vertex identifier
 * and the duration or cost associated with reaching this node.
 */
public class Node implements Comparable<Node> {
    private long vertex; // The identifier of the vertex
    private double duration; // The duration or cost associated with the node

    /**
     * Constructs a new Node with the specified vertex and duration.
     *
     * @param v the vertex identifier
     * @param d the duration or cost associated with the node
     */
    public Node(long v, double d) {
        this.vertex = v;
        this.duration = d;
    }


    public long getVertex() {
        return vertex;
    }


    public void setVertex(long vertex) {
        this.vertex = vertex;
    }


    public double getDuration() {
        return duration;
    }


    public void setDuration(double duration) {
        this.duration = duration;
    }

    /**
     * Compares this node to another node based on their durations.
     *
     * @param other the other node to compare to
     * @return a negative integer, zero, or a positive integer as this node's
     *         duration is less than, equal to, or greater than the other node's duration
     */
    @Override
    public int compareTo(Node other) {
        return Double.compare(this.duration, other.duration);
    }
}
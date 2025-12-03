package domain.model.dijkstra;

/**
 * Represents information about a cell in the Dijkstra algorithm.
 * Stores the duration, predecessor, and visited status of the cell.
 */
public class CellInfo {
    private double duration; // The duration or cost associated with this cell (from the start node)
    private long predecessor; // The predecessor node ID in the path
    private boolean visited; // Indicates whether the cell has been visited

    /**
     * Constructs a new CellInfo instance.
     *
     * @param duration    the duration or cost associated with this cell (from the start node) (double in seconds)
     * @param predecessor the predecessor node ID in the path (-1 if none) (long)
     * @param visited     whether the cell has been visited (boolean)
     */
    public CellInfo(double duration, long predecessor, boolean visited) {
        this.duration = duration;
        this.predecessor = predecessor;
        this.visited = visited;
    }

    /**
     * Gets the duration or cost associated with this cell.
     *
     * @return the duration of the cell (double in seconds)
     */
    public double getDuration() {
        return duration;
    }

    /**
     * Sets the duration or cost associated with this cell.
     *
     * @param duration the new duration of the cell (double in seconds)
     */
    public void setDuration(double duration) {
        this.duration = duration;
    }

    /**
     * Gets the predecessor node ID in the path.
     *
     * @return the predecessor node ID (long)
     */
    public long getPredecessor() {
        return predecessor;
    }

    /**
     * Sets the predecessor node ID in the path.
     *
     * @param predecessor the new predecessor node ID (long)
     */
    public void setPredecessor(long predecessor) {
        this.predecessor = predecessor;
    }

    /**
     * Checks whether the cell has been visited.
     *
     * @return true if the cell has been visited, false otherwise
     */
    public boolean isVisited() {
        return visited;
    }

    /**
     * Sets the visited status of the cell.
     *
     * @param visited the new visited status of the cell (boolean)
     */
    public void setVisited(boolean visited) {
        this.visited = visited;
    }
}

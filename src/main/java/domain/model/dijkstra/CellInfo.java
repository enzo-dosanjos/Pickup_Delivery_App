package domain.model.dijkstra;

/**
 * Represents information about a cell in our DijkstraTable used for our Dijkstra algorithm.
 * It stores all the information needed to use Dijkstra's algorithm repetitively on each node
 * of a graph to find the shortest paths.
 *
 * Stores the duration (of travel from node A to B), predecessor (in the travel from A to B), and visited status of the cell.
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

    public double getDuration() {
        return duration;
    }


    public void setDuration(double duration) {
        this.duration = duration;
    }


    public long getPredecessor() {
        return predecessor;
    }


    public void setPredecessor(long predecessor) {
        this.predecessor = predecessor;
    }


    public boolean isVisited() {
        return visited;
    }


    public void setVisited(boolean visited) {
        this.visited = visited;
    }
}

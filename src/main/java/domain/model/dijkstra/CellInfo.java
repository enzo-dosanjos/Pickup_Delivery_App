package domain.model.dijkstra;

public class CellInfo {
    double distance;
    long predecessor;
    boolean visited;

    public CellInfo(double distance, long predecessor, boolean visited) {
        this.distance = distance;
        this.predecessor = predecessor;
        this.visited = visited;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
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

package domain.model.dijkstra;

public class CellInfo {
    private double duration;
    private long predecessor;
    private boolean visited;

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

package domain.model.dijkstra;

public class CellInfo {
    double distance;
    long precedent;
    boolean visited;

    public CellInfo(double distance, long precedent, boolean visited) {
        this.distance = distance;
        this.precedent = precedent;
        this.visited = visited;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public long getPrecedent() {
        return precedent;
    }

    public void setPrecedent(long precedent) {
        this.precedent = precedent;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }
}

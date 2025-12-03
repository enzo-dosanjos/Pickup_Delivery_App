package domain.model.dijkstra;

public class Node implements Comparable<Node> {
    private long vertex;
    private double duration;

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

    @Override
    public int compareTo(Node other) {
        return Double.compare(this.duration, other.duration);
    }
}
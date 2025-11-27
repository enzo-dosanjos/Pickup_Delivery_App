package domain.model.dijkstra;

public class Node implements Comparable<Node> {
    long vertex;
    double distance;

    public Node(long v, double d) {
        this.vertex = v;
        this.distance = d;
    }

    public long getVertex() {
        return vertex;
    }

    public void setVertex(long vertex) {
        this.vertex = vertex;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public int compareTo(Node other) {
        return Double.compare(this.distance, other.distance);
    }
}
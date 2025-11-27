package domain.model.dijkstra;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NodeTest {

    @Test
    void checkConstructorAndGettersSetters() {
        long vertex = 8L;
        double distance = 666.666;

        Node node = new Node(vertex, distance);
        assertEquals(vertex, node.getVertex());
        assertEquals(distance, node.getDistance());

        vertex = 12L;
        distance = 333.333;
        node.setVertex(vertex);
        node.setDistance(distance);
        assertEquals(vertex, node.getVertex());
        assertEquals(distance, node.getDistance());
    }
}

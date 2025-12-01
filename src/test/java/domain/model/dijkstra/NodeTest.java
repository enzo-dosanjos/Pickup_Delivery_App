package domain.model.dijkstra;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NodeTest {

    @Test
    void checkConstructorAndGettersSetters() {
        long vertex = 8L;
        double duration = 666.666;

        Node node = new Node(vertex, duration);
        assertEquals(vertex, node.getVertex());
        assertEquals(duration, node.getDuration());

        vertex = 12L;
        duration = 333.333;
        node.setVertex(vertex);
        node.setDuration(duration);
        assertEquals(vertex, node.getVertex());
        assertEquals(duration, node.getDuration());
    }
}

package domain.model.dijkstra;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Node} class.
 */
public class NodeTest {

    /**
     * Tests the constructor, getter, and setter methods of the {@link Node} class.
     * Verifies that the vertex and duration values can be set and retrieved correctly.
     */
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

    /**
     * Verifies that the compareTo method correctly compares nodes based on their durations.
     */
    @Test
    void compareToComparesNodesByDuration() {
        Node node1 = new Node(1L, 100.0);
        Node node2 = new Node(2L, 200.0);
        Node node3 = new Node(3L, 100.0);

        assertTrue(node1.compareTo(node2) < 0);
        assertTrue(node2.compareTo(node1) > 0);
        assertEquals(0, node1.compareTo(node3));
    }

    /**
     * Verifies that the compareTo method handles nodes with negative durations.
     */
    @Test
    void compareToHandlesNegativeDurations() {
        Node node1 = new Node(1L, -50.0);
        Node node2 = new Node(2L, 0.0);
        Node node3 = new Node(3L, -100.0);

        assertTrue(node1.compareTo(node2) < 0);
        assertTrue(node1.compareTo(node3) > 0);
        assertTrue(node3.compareTo(node2) < 0);
    }



    /**
     * Verifies that the setVertex and setDuration methods update the values correctly.
     */
    @Test
    void setMethodsUpdateValuesCorrectly() {
        Node node = new Node(1L, 50.0);
        node.setVertex(10L);
        node.setDuration(75.0);

        assertEquals(10L, node.getVertex());
        assertEquals(75.0, node.getDuration());
    }
}
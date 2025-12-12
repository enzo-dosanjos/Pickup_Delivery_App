package domain.model.dijkstra;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link CellInfo} class.
 */
public class CellInfoTest {

    /**
     * Tests the constructor, getter, and setter methods of the {@link CellInfo} class.
     * Verifies that the fields are correctly initialized and updated.
     */
    @Test
    void checkConstructorAndGettersSetters() {
        double duration = 666.666; // Initial duration value
        long predecessor = 48; // Initial predecessor value
        boolean visited = false; // Initial visited status

        // Create a new CellInfo instance and verify initial values
        CellInfo cellInfo = new CellInfo(duration, predecessor, visited);
        assertEquals(duration, cellInfo.getDuration());
        assertEquals(predecessor, cellInfo.getPredecessor());
        assertEquals(visited, cellInfo.isVisited());

        // Update the values using setters and verify the updated values
        duration = 333.333; // Updated duration value
        predecessor = 46; // Updated predecessor value
        visited = true; // Updated visited status
        cellInfo.setDuration(duration);
        cellInfo.setPredecessor(predecessor);
        cellInfo.setVisited(visited);
        assertEquals(duration, cellInfo.getDuration());
        assertEquals(predecessor, cellInfo.getPredecessor());
        assertEquals(visited, cellInfo.isVisited());
    }

    /**
     * Verifies that the CellInfo constructor handles edge cases such as negative duration,
     * no predecessor, and already visited status.
     */
    @Test
    void constructorHandlesEdgeCases() {
        double duration = -1.0; // Negative duration
        long predecessor = -1; // No predecessor
        boolean visited = true; // Already visited

        CellInfo cellInfo = new CellInfo(duration, predecessor, visited);

        assertEquals(duration, cellInfo.getDuration());
        assertEquals(predecessor, cellInfo.getPredecessor());
        assertTrue(cellInfo.isVisited());
    }

    /**
     * Verifies that the setDuration method handles edge cases such as setting a negative duration.
     */
    @Test
    void setDurationHandlesNegativeValues() {
        CellInfo cellInfo = new CellInfo(10.0, 1, false);

        cellInfo.setDuration(-5.0); // Set a negative duration
        assertEquals(-5.0, cellInfo.getDuration());
    }

    /**
     * Verifies that the setVisited method correctly updates the visited status.
     */
    @Test
    void setVisitedUpdatesStatus() {
        CellInfo cellInfo = new CellInfo(10.0, 1, false);

        cellInfo.setVisited(true); // Update visited status to true
        assertTrue(cellInfo.isVisited());

        cellInfo.setVisited(false); // Update visited status to false
        assertFalse(cellInfo.isVisited());
    }
}

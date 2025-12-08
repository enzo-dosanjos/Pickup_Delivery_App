package domain.model.dijkstra;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link DijkstraTable} class.
 */
public class DijkstraTableTest {

    /**
     * Tests the constructor and getter/setter methods of the {@link DijkstraTable} class.
     * Verifies that values can be added and retrieved correctly.
     */
    @Test
    void checkConstructorAndGettersSetters() {
        DijkstraTable dijkstraTable = new DijkstraTable();
        dijkstraTable.put(1L, 2L, 666.666, 1L, true);
        CellInfo cellInfo = new CellInfo(333.333, 2L, false);
        dijkstraTable.put(3L, 4L, cellInfo);

        assertEquals(666.666, dijkstraTable.get(1L, 2L).getDuration());
        assertEquals(cellInfo, dijkstraTable.get(3L, 4L));
    }

    /**
     * Tests the {@link DijkstraTable#contains} method.
     * Verifies that the method correctly identifies whether a key pair exists in the table.
     */
    @Test
    void checkContains() {
        DijkstraTable dijkstraTable = new DijkstraTable();
        assertFalse(dijkstraTable.contains(1L, 2L));

        CellInfo cellInfo = new CellInfo(333.333, 2L, false);
        dijkstraTable.put(3L, 4L, cellInfo);
        assertTrue(dijkstraTable.contains(3L, 4L));
    }

    /**
     * Tests the {@link DijkstraTable#remove} method.
     * Verifies that values can be removed from the table and checks the behavior when removing non-existent keys.
     */
    @Test
    void checkRemove() {
        DijkstraTable dijkstraTable = new DijkstraTable();
        dijkstraTable.put(1L, 2L, 666.666, 1L, true);

        dijkstraTable.remove(3L, 4L);
        assertNotEquals(null, dijkstraTable.get(1L, 2L));

        dijkstraTable.remove(1L, 2L);
        assertNull(dijkstraTable.get(1L, 2L));
    }

    /**
     * Verifies that the get method returns null for non-existent cells.
     */
    @Test
    void getReturnsNullForNonExistentCells() {
        DijkstraTable dijkstraTable = new DijkstraTable();
        assertNull(dijkstraTable.get(1L, 2L));
    }

    /**
     * Verifies that the put method correctly overwrites existing cells.
     */
    @Test
    void putOverwritesExistingCells() {
        DijkstraTable dijkstraTable = new DijkstraTable();
        dijkstraTable.put(1L, 2L, 100.0, 1L, false);
        dijkstraTable.put(1L, 2L, 200.0, 2L, true);

        CellInfo cellInfo = dijkstraTable.get(1L, 2L);
        assertEquals(200.0, cellInfo.getDuration());
        assertEquals(2L, cellInfo.getPredecessor());
        assertTrue(cellInfo.isVisited());
    }

    /**
     * Verifies that the remove method does not throw an exception when removing non-existent cells.
     */
    @Test
    void removeHandlesNonExistentCellsGracefully() {
        DijkstraTable dijkstraTable = new DijkstraTable();
        dijkstraTable.remove(1L, 2L);
        assertNull(dijkstraTable.get(1L, 2L));
    }

    /**
     * Verifies that the table handles multiple rows and columns correctly.
     */
    @Test
    void handlesMultipleRowsAndColumns() {
        DijkstraTable dijkstraTable = new DijkstraTable();
        dijkstraTable.put(1L, 1L, 100.0, 1L, false);
        dijkstraTable.put(1L, 2L, 200.0, 2L, true);
        dijkstraTable.put(2L, 1L, 300.0, 3L, false);

        assertEquals(100.0, dijkstraTable.get(1L, 1L).getDuration());
        assertEquals(200.0, dijkstraTable.get(1L, 2L).getDuration());
        assertEquals(300.0, dijkstraTable.get(2L, 1L).getDuration());
    }

    /**
     * Verifies that the table is empty after removing all cells.
     */
    @Test
    void tableIsEmptyAfterRemovingAllCells() {
        DijkstraTable dijkstraTable = new DijkstraTable();
        dijkstraTable.put(1L, 1L, 100.0, 1L, false);
        dijkstraTable.put(1L, 2L, 200.0, 2L, true);

        dijkstraTable.remove(1L, 1L);
        dijkstraTable.remove(1L, 2L);

        assertFalse(dijkstraTable.contains(1L, 1L));
        assertFalse(dijkstraTable.contains(1L, 2L));
    }
}
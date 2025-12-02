package domain.model.dijkstra;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DijkstraTableTest {

    @Test
    void checkConstructorAndGettersSetters() {
        DijkstraTable dijkstraTable = new DijkstraTable();
        dijkstraTable.put(1L, 2L, 666.666, 1L, true);
        CellInfo cellInfo = new CellInfo(333.333, 2L, false);
        dijkstraTable.put(3L, 4L, cellInfo);

        assertEquals(666.666, dijkstraTable.get(1L, 2L).getDuration());
        assertEquals(cellInfo, dijkstraTable.get(3L, 4L));
    }

    @Test
    void checkContains() {
        DijkstraTable dijkstraTable = new DijkstraTable();
        assertFalse(dijkstraTable.contains(1L, 2L));

        CellInfo cellInfo = new CellInfo(333.333, 2L, false);
        dijkstraTable.put(3L, 4L, cellInfo);
        assertTrue(dijkstraTable.contains(3L, 4L));
    }

    @Test
    void checkRemove() {
        DijkstraTable dijkstraTable = new DijkstraTable();
        dijkstraTable.put(1L, 2L, 666.666, 1L, true);

        dijkstraTable.remove(3L, 4L);
        assertNotEquals(null, dijkstraTable.get(1L, 2L));

        dijkstraTable.remove(1L, 2L);
        assertNull(dijkstraTable.get(1L, 2L));
    }
}

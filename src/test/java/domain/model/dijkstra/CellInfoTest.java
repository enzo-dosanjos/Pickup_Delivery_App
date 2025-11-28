package domain.model.dijkstra;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CellInfoTest {

    @Test
    void checkConstructorAndGettersSetters() {
        double duration = 666.666;
        long predecessor = 48;
        boolean visited = false;

        CellInfo cellInfo = new CellInfo(duration, predecessor, visited);
        assertEquals(duration, cellInfo.getDuration());
        assertEquals(predecessor, cellInfo.getPredecessor());
        assertEquals(visited, cellInfo.isVisited());

        duration = 333.333;
        predecessor = 46;
        visited = true;
        cellInfo.setDuration(duration);
        cellInfo.setPredecessor(predecessor);
        cellInfo.setVisited(visited);
        assertEquals(duration, cellInfo.getDuration());
        assertEquals(predecessor, cellInfo.getPredecessor());
        assertEquals(visited, cellInfo.isVisited());
    }
}

package domain.model.dijkstra;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CellInfoTest {

    @Test
    void checkConstructorAndGettersSetters() {
        double distance = 666.666;
        long predecessor = 48;
        boolean visited = false;

        CellInfo cellInfo = new CellInfo(distance, predecessor, visited);
        assertEquals(distance, cellInfo.getDistance());
        assertEquals(predecessor, cellInfo.getPredecessor());
        assertEquals(visited, cellInfo.isVisited());

        distance = 333.333;
        predecessor = 46;
        visited = true;
        cellInfo.setDistance(distance);
        cellInfo.setPredecessor(predecessor);
        cellInfo.setVisited(visited);
        assertEquals(distance, cellInfo.getDistance());
        assertEquals(predecessor, cellInfo.getPredecessor());
        assertEquals(visited, cellInfo.isVisited());
    }
}

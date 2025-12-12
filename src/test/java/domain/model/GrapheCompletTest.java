package domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link GrapheComplet} class.
 */
public class GrapheCompletTest {

    /**
     * Verifies that the getCout method returns the correct cost for valid indices.
     */
    @Test
    void getCoutWithValidIndices() {
        GrapheComplet grapheComplet = new GrapheComplet(3);
        grapheComplet.setCout(0, 1, 10.5);
        grapheComplet.setCout(1, 2, 20.0);

        assertEquals(10.5, grapheComplet.getCout(0, 1));
        assertEquals(20.0, grapheComplet.getCout(1, 2));
    }

    /**
     * Verifies that the getCout method returns -1 for invalid indices.
     */
    @Test
    void getCoutWithInvalidIndices() {
        GrapheComplet grapheComplet = new GrapheComplet(3);

        assertEquals(-1, grapheComplet.getCout(-1, 1));
        assertEquals(-1, grapheComplet.getCout(3, 0));
        assertEquals(-1, grapheComplet.getCout(1, 3));
    }

    /**
     * Verifies that the estArc method correctly identifies valid edges.
     */
    @Test
    void estArcWithValidIndices() {
        GrapheComplet grapheComplet = new GrapheComplet(3);

        assertTrue(grapheComplet.estArc(0, 1));
        assertTrue(grapheComplet.estArc(1, 2));
        assertFalse(grapheComplet.estArc(0, 0));
    }

    /**
     * Verifies that the estArc method returns false for invalid indices.
     */
    @Test
    void estArcWithInvalidIndices() {
        GrapheComplet grapheComplet = new GrapheComplet(3);

        assertFalse(grapheComplet.estArc(-1, 1));
        assertFalse(grapheComplet.estArc(3, 0));
        assertFalse(grapheComplet.estArc(1, 3));
    }

    /**
     * Verifies that the toString method returns the correct string representation of the graph.
     */
    @Test
    void toStringRepresentation() {
        GrapheComplet grapheComplet = new GrapheComplet(2);
        grapheComplet.setCout(0, 1, 15.0);

        String expected = "GrapheComplet{sommets=null, nbSommets=2,\ncouts=[[1.7976931348623157E308, 15.0], [1.7976931348623157E308, 1.7976931348623157E308]]}";
        assertEquals(expected, grapheComplet.toString());
    }

    /**
     * Verifies that the setCout method updates the cost correctly for valid indices.
     */
    @Test
    void setCoutUpdatesCostForValidIndices() {
        GrapheComplet grapheComplet = new GrapheComplet(3);
        grapheComplet.setCout(0, 1, 25.0);

        assertEquals(25.0, grapheComplet.getCout(0, 1));
    }

    /**
     * Verifies that the getSommets method returns the correct array of vertex identifiers.
     */
    @Test
    void getSommetsReturnsCorrectVertexArray() {
        long[] sommets = {1L, 2L, 3L};
        GrapheComplet grapheComplet = new GrapheComplet(sommets, 3);

        assertArrayEquals(sommets, grapheComplet.getSommets());
    }

    /**
     * Verifies that the constructor initializes the cost matrix with Double.MAX_VALUE.
     */
    @Test
    void constructorInitializesCostMatrixWithMaxValue() {
        GrapheComplet grapheComplet = new GrapheComplet(2);

        assertEquals(Double.MAX_VALUE, grapheComplet.getCout(0, 0));
        assertEquals(Double.MAX_VALUE, grapheComplet.getCout(1, 1));
    }

}
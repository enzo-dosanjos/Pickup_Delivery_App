package domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GrapheCompletTest {

    @Test
    void getCoutWithValidIndices() {
        GrapheComplet grapheComplet = new GrapheComplet(3);
        grapheComplet.setCout(0, 1, 10.5);
        grapheComplet.setCout(1, 2, 20.0);

        assertEquals(10.5, grapheComplet.getCout(0, 1));
        assertEquals(20.0, grapheComplet.getCout(1, 2));
    }

    @Test
    void getCoutWithInvalidIndices() {
        GrapheComplet grapheComplet = new GrapheComplet(3);

        assertEquals(-1, grapheComplet.getCout(-1, 1));
        assertEquals(-1, grapheComplet.getCout(3, 0));
        assertEquals(-1, grapheComplet.getCout(1, 3));
    }

    @Test
    void estArcWithValidIndices() {
        GrapheComplet grapheComplet = new GrapheComplet(3);

        assertTrue(grapheComplet.estArc(0, 1));
        assertTrue(grapheComplet.estArc(1, 2));
        assertFalse(grapheComplet.estArc(0, 0));
    }

    @Test
    void estArcWithInvalidIndices() {
        GrapheComplet grapheComplet = new GrapheComplet(3);

        assertFalse(grapheComplet.estArc(-1, 1));
        assertFalse(grapheComplet.estArc(3, 0));
        assertFalse(grapheComplet.estArc(1, 3));
    }

    @Test
    void toStringRepresentation() {
        GrapheComplet grapheComplet = new GrapheComplet(2);
        grapheComplet.setCout(0, 1, 15.0);

        String expected = "GrapheComplet{sommets=null, nbSommets=2,\ncouts=[[1.7976931348623157E308, 15.0], [1.7976931348623157E308, 1.7976931348623157E308]]}";
        assertEquals(expected, grapheComplet.toString());
    }

}

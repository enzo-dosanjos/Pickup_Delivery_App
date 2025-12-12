package domain.service;

import domain.model.Graphe;
import domain.model.GrapheComplet;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link IteratorSeq} class.
 * This class tests various scenarios for the IteratorSeq, including edge cases such as empty collections,
 * non-successor vertices, and exceptions when no elements are available.
 */
public class IteratorSeqTest {

    /**
     * Verifies that the iterator correctly iterates over successors of the current vertex.
     */
    @Test
    void iteratorIteratesOverSuccessors() {
        long[] sommets = {1L, 2L, 3L, 4L, 5L};
        Graphe graph = new GrapheComplet(sommets, 5);
        Collection<Integer> nonVus = List.of(2, 3, 4);
        int sommetCrt = 1;

        IteratorSeq iterator = new IteratorSeq(nonVus, sommetCrt, graph);

        assertTrue(iterator.hasNext());
        assertEquals(2, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(3, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(4, iterator.next());
        assertFalse(iterator.hasNext());

    }

    /**
     * Verifies that the iterator correctly handles a graph where all vertices are successors.
     */
    @Test
    void iteratorHandlesAllVerticesAsSuccessors() {
        long[] sommets = {1L, 2L, 3L};
        Graphe graph = new GrapheComplet(sommets, 3);
        Collection<Integer> nonVus = List.of(1, 2);
        int sommetCrt = 0;

        IteratorSeq iterator = new IteratorSeq(nonVus, sommetCrt, graph);

        assertTrue(iterator.hasNext());
        assertEquals(1, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(2, iterator.next());
        assertFalse(iterator.hasNext());
    }

    /**
     * Verifies that the iterator handles a graph where no vertices are successors.
     */
    @Test
    void iteratorHandlesNoVerticesAsSuccessors() {
        long[] sommets = {1L, 2L, 3L};
        Graphe graph = new GrapheComplet(sommets, 3);
        Collection<Integer> nonVus = List.of();
        int sommetCrt = 2;

        IteratorSeq iterator = new IteratorSeq(nonVus, sommetCrt, graph);

        assertFalse(iterator.hasNext());
    }

    /**
     * Verifies that the iterator handles a graph with a single vertex in the collection.
     */
    @Test
    void iteratorHandlesSingleVertexInCollection() {
        long[] sommets = {1L, 2L};
        Graphe graph = new GrapheComplet(sommets, 2);
        Collection<Integer> nonVus = List.of(1);
        int sommetCrt = 0;

        IteratorSeq iterator = new IteratorSeq(nonVus, sommetCrt, graph);

        assertTrue(iterator.hasNext());
        assertEquals(1, iterator.next());
        assertFalse(iterator.hasNext());
    }

    /**
     * Verifies that the iterator handles a graph with duplicate vertices in the collection.
     */
    @Test
    void iteratorHandlesDuplicateVerticesInCollection() {
        long[] sommets = {1L, 2L, 3L, 4L};
        Graphe graph = new GrapheComplet(sommets, 4);
        Collection<Integer> nonVus = List.of(2, 2, 3);
        int sommetCrt = 1;

        IteratorSeq iterator = new IteratorSeq(nonVus, sommetCrt, graph);

        assertTrue(iterator.hasNext());
        assertEquals(2, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(2, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(3, iterator.next());
        assertFalse(iterator.hasNext());
    }

}

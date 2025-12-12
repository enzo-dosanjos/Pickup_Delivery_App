package domain.service;

import domain.model.Graphe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * IteratorSeq is an iterator that iterates over the set of vertices in `nonVus`
 * that are successors of `sommetCrt` in the graph `g`, in the order of their
 * appearance in `nonVus`.
 */
public class IteratorSeq implements Iterator<Integer> {

	private final Iterator<Integer> iterator; // Array of candidate vertices to iterate over.


	private final List<Integer> candidats; // Number of remaining candidates to iterate over.

    /**
     * Constructs an iterator for iterating over the vertices in `nonVus` that
     * are successors of `sommetCrt` in the graph `g`.
     *
     * @param nonVus the collection of vertices to consider
     * @param sommetCrt the current vertex
     * @param g the graph containing the vertices and edges
     */
	public IteratorSeq(Collection<Integer> nonVus, int sommetCrt, Graphe g) {
        candidats = new ArrayList<>();
        for (Integer s : nonVus) {
            if (g.estArc(sommetCrt, s)) {
                candidats.add(s);
            }
        }
        // Orders by higer cost (most promising first)
        candidats.sort(
                Comparator.comparingDouble(s -> g.getCout(sommetCrt, s))
        );
        iterator = candidats.iterator();
    }

    /**
     * Checks if there are more candidates to iterate over.
     *
     * @return true if there are more candidates, false otherwise
     */
	@Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    /**
     * Returns the next candidate in the iteration.
     *
     * @return the next candidate vertex
     */
    @Override
    public Integer next() {
        return iterator.next();
    }

    /**
     * Removes the current element from the iteration. This method is not
     * implemented and does nothing.
     */
    @Override
    public void remove() {}
}

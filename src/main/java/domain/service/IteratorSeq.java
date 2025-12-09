package domain.service;

import domain.model.Graphe;

import java.util.Collection;
import java.util.Iterator;

/**
 * IteratorSeq is an iterator that iterates over the set of vertices in `nonVus`
 * that are successors of `sommetCrt` in the graph `g`, in the order of their
 * appearance in `nonVus`.
 */
public class IteratorSeq implements Iterator<Integer> {


    private Integer[] candidats; // Array of candidate vertices to iterate over.


    private int nbCandidats; // Number of remaining candidates to iterate over.

    /**
     * Constructs an iterator for iterating over the vertices in `nonVus` that
     * are successors of `sommetCrt` in the graph `g`.
     *
     * @param nonVus the collection of vertices to consider
     * @param sommetCrt the current vertex
     * @param g the graph containing the vertices and edges
     */
    public IteratorSeq(Collection<Integer> nonVus, int sommetCrt, Graphe g) {
        this.candidats = new Integer[nonVus.size()];
        Iterator<Integer> it = nonVus.iterator();
        while (it.hasNext()) {
            Integer s = it.next();
            if (g.estArc(sommetCrt, s))
                candidats[nbCandidats++] = s;
        }
    }

    /**
     * Checks if there are more candidates to iterate over.
     *
     * @return true if there are more candidates, false otherwise
     */
    @Override
    public boolean hasNext() {
        return nbCandidats > 0;
    }

    /**
     * Returns the next candidate in the iteration.
     *
     * @return the next candidate vertex
     */
    @Override
    public Integer next() {
        nbCandidats--;
        return candidats[nbCandidats];
    }

    /**
     * Removes the current element from the iteration. This method is not
     * implemented and does nothing.
     */
    @Override
    public void remove() {}
}
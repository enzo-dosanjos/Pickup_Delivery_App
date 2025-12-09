package domain.service;

import domain.model.Graphe;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Iterator;

/**
 * Implementation of the Traveling Salesman Problem (TSP) using a specific template.
 * This class provides methods to calculate bounds and iterate over possible paths.
 */
@Service
public class TSP1 extends TemplateTSP {

    /**
     * Calculates a lower bound for the cost of completing the tour from the current vertex.
     * This implementation always returns 0.0, meaning no heuristic is applied.
     *
     * @param sommetCourant the current vertex
     * @param nonVus the collection of vertices that have not been visited yet
     * @return the lower bound for the cost of completing the tour
     */
    @Override
    protected double bound(Integer sommetCourant, Collection<Integer> nonVus) {
        return 0.0;
    }

    /**
     * Provides an iterator to iterate over the vertices that have not been visited yet.
     * The iterator determines the order in which the vertices are explored.
     *
     * @param sommetCrt the current vertex
     * @param nonVus the collection of vertices that have not been visited yet
     * @param g the graph representing the TSP problem
     * @return an iterator for the unvisited vertices
     */
    @Override
    protected Iterator<Integer> iterator(Integer sommetCrt, Collection<Integer> nonVus, Graphe g) {
        return new IteratorSeq(nonVus, sommetCrt, g);
    }
}

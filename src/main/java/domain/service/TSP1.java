package domain.service;

import domain.model.Graphe;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * TSP1 implements the concrete bound and iterator strategies. 
 *
 * 
 * Bound: lower bound estimation using Prim's MST algorithm.
 * Iterator: delegates to IteratorSeq for node exploration order.
 */
@Service
public class TSP1 extends TemplateTSP {

	@Override
	protected double bound(Integer sommetCourant, Collection<Integer> nonVus) {
        if (nonVus.isEmpty()) return 0.0;
        
        double minFromCurrent = Double.POSITIVE_INFINITY;
        double minToDepot = Double.POSITIVE_INFINITY;

        // Find cheapest exit and return arcs
        for (Integer i : nonVus) {
            if (g.estArc(sommetCourant, i)) {
                minFromCurrent = Math.min(minFromCurrent, g.getCout(sommetCourant, i));
            }
            if (g.estArc(i, 0)) {
                minToDepot = Math.min(minToDepot, g.getCout(i, 0));
            }
        }

        // Approximate remaining cost with a MST over unvisited nodes
        double mstCost = computeMSTCost(nonVus);

        if (minFromCurrent == Double.POSITIVE_INFINITY) minFromCurrent = 0;
        if (minToDepot == Double.POSITIVE_INFINITY) minToDepot = 0;

        return mstCost + minFromCurrent + minToDepot;
    }
	/**
     * Computes the cost of a MST among 
     * unvisited nodes using Prim's algorithm.
     */
	private double computeMSTCost(Collection<Integer> nonVus) {
    	List<Integer> nodes = new ArrayList<>(nonVus);
        int n = nodes.size();
        if (n == 0 || n == 1) return 0.0;

        double totalCost = 0.0;
        boolean[] inMST = new boolean[n];
        double[] minEdge = new double[n];
        Arrays.fill(minEdge, Double.POSITIVE_INFINITY);


        // Start from an arbitrary node (first in list)
        minEdge[0] = 0.0;

        for (int i = 0; i < n; i++) {
            // Select next closest node to MST
            int u = -1;
            double best = Double.POSITIVE_INFINITY;
            for (int j = 0; j < n; j++) {
                if (!inMST[j] && minEdge[j] < best) {
                    best = minEdge[j];
                    u = j;
                }
            }
            if (u == -1) break; 
            inMST[u] = true;
            totalCost += best;

           
            for (int v = 0; v < n; v++) {
                if (!inMST[v] && g.estArc(nodes.get(u), nodes.get(v))) {
                    double cost = g.getCout(nodes.get(u), nodes.get(v));
                    if (cost < minEdge[v]) {
                        minEdge[v] = cost;
                    }
                }
            }
        }

        return totalCost;
    }

	@Override
	protected Iterator<Integer> iterator(Integer sommetCrt, Collection<Integer> nonVus, Graphe g) {
		return new IteratorSeq(nonVus, sommetCrt, g);
	}

}

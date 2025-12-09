package domain.service;

import domain.model.Graphe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Abstract class providing a template for solving the Sequential Ordering Problem (SOP,
 * an asymmetrical Traveling Salesman Problem (TSP) with precedence constraints)  using
 * the branch-and-bound method. Subclasses must implement the `bound` and `iterator` methods.
 */
public abstract class TemplateTSP implements TSP {


    private Integer[] meilleureSolution; // Array storing the best solution found so far.


    protected Graphe g; // The graph representing the problem.


    private double coutMeilleureSolution; // The cost of the best solution found so far.


    private int tpsLimite; // Time limit for the solution search in milliseconds.


    private long tpsDebut; // Start time of the solution search.


    private Map<Integer, Set<Integer>> precedences = new HashMap<>(); // Map storing precedence constraints for nodes.


    private double[] serviceTimes = null; // Array storing the service times for each node.

    public void setPrecedences(Map<Integer, Set<Integer>> precedences) {
        if (precedences == null) this.precedences = new HashMap<>();
        else this.precedences = precedences;
    }


    public Map<Integer, Set<Integer>> getPrecedences() {
        return this.precedences;
    }


    public void setServiceTimes(double[] serviceTimes) {
        this.serviceTimes = serviceTimes;
    }


    public double[] getServiceTimes() {
        return serviceTimes;
    }

    /**
     * Searches for the best solution within the given time limit.
     *
     * @param tpsLimite the time limit in milliseconds
     * @param g the graph representing the problem
     */
    @Override
    public void chercheSolution(int tpsLimite, Graphe g) {
        if (tpsLimite <= 0) return;
        tpsDebut = System.currentTimeMillis();
        this.tpsLimite = tpsLimite;
        this.g = g;
        meilleureSolution = new Integer[g.getNbSommets()];
        Collection<Integer> nonVus = new ArrayList<>(g.getNbSommets() - 1);
        for (int i = 1; i < g.getNbSommets(); i++) nonVus.add(i);
        Collection<Integer> vus = new ArrayList<>(g.getNbSommets());
        vus.add(0); // the first visited node is 0
        coutMeilleureSolution = Integer.MAX_VALUE;
        branchAndBound(0, nonVus, vus, 0);
    }

@Override
    public Integer getSolution(int i) {
        if (g != null && i >= 0 && i < g.getNbSommets())
            return meilleureSolution[i];
        return -1;
    }

@Override
    public double getCoutSolution() {
        if (g != null)
            return coutMeilleureSolution;
        return -1;
    }


    public double getCoutMeilleureSolution() {
        return coutMeilleureSolution;
    }

    /**
     * Abstract method to compute a lower bound on the cost of paths starting from the current node,
     * visiting all unvisited nodes exactly once, and returning to the starting node.
     *
     * @param sommetCourant the current node
     * @param nonVus the collection of unvisited nodes
     * @return a lower bound on the cost of the paths
     */
    protected abstract double bound(Integer sommetCourant, Collection<Integer> nonVus);

    /**
     * Abstract method to provide an iterator over all unvisited nodes that are successors of the current node.
     *
     * @param sommetCrt the current node
     * @param nonVus the collection of unvisited nodes
     * @param g the graph representing the problem
     * @return an iterator over the successors of the current node
     */
    protected abstract Iterator<Integer> iterator(Integer sommetCrt, Collection<Integer> nonVus, Graphe g);

    /**
     * Performs the branch-and-bound algorithm to solve the TSP.
     *
     * @param sommetCrt the last visited node
     * @param nonVus the collection of unvisited nodes
     * @param vus the collection of visited nodes, including the current node
     * @param coutVus the total cost of the path through the visited nodes
     */
    private void branchAndBound(int sommetCrt, Collection<Integer> nonVus, Collection<Integer> vus, double coutVus) {
        if (System.currentTimeMillis() - tpsDebut > tpsLimite) return;
        if (nonVus.size() == 0) { // all nodes have been visited
            if (g.estArc(sommetCrt, 0)) { // can return to the starting node (0)
                if (coutVus + g.getCout(sommetCrt, 0) < coutMeilleureSolution) { // found a better solution
                    vus.toArray(meilleureSolution);
                    coutMeilleureSolution = coutVus + g.getCout(sommetCrt, 0);
                }
            }
        } else if (coutVus + bound(sommetCrt, nonVus) < coutMeilleureSolution) {
            Iterator<Integer> it = iterator(sommetCrt, nonVus, g);
            while (it.hasNext()) {
                Integer prochainSommet = it.next();
                // Check precedence constraints
                Set<Integer> preds = precedences.getOrDefault(prochainSommet, Collections.emptySet());
                boolean ok = true;
                for (Integer pred : preds) {
                    if (!vus.contains(pred)) {
                        ok = false;
                        break;
                    }
                }
                if (!ok) continue;
                vus.add(prochainSommet);
                nonVus.remove(prochainSommet);

                double addCost = g.getCout(sommetCrt, prochainSommet);

                // Add service time if applicable
                if (serviceTimes != null && prochainSommet >= 0 && prochainSommet < serviceTimes.length) {
                    addCost += serviceTimes[prochainSommet];
                }

                branchAndBound(prochainSommet, nonVus, vus, coutVus + addCost);

                vus.remove(prochainSommet);
                nonVus.add(prochainSommet);
            }
        }
    }
}
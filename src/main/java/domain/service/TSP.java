package domain.service;

import domain.model.Graphe;

/**
 * Interface representing the Traveling Salesman Problem (TSP) solver.
 * Provides methods to find a solution for the TSP, retrieve the solution path,
 * and calculate the total cost of the solution.
 */
public interface TSP {

    /**
     * Searches for a solution to the TSP for the given graph within the specified time limit.
     * The solution must start with vertex 0.
     *
     * @param tpsLimite the time limit in milliseconds for finding the solution
     * @param g the graph representing the TSP problem
     */
    public void chercheSolution(int tpsLimite, Graphe g);

    /**
     * Retrieves the i-th vertex visited in the solution calculated by {@code chercheSolution}.
     *
     * @param i the index of the vertex in the solution path
     * @return the i-th vertex in the solution, or -1 if {@code chercheSolution} has not been called,
     *         or if i is out of bounds (i < 0 or i >= g.getNbSommets())
     */
    public Integer getSolution(int i);

    /**
     * Calculates the total cost of the arcs in the solution found by {@code chercheSolution}.
     *
     * @return the total cost of the solution, or -1 if {@code chercheSolution} has not been called
     */
    public double getCoutSolution();
}
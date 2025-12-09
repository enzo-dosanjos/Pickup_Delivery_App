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


    public Integer getSolution(int i);


    public double getCoutSolution();
}
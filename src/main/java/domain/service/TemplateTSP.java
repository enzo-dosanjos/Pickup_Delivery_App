package domain.service;

import domain.model.Graphe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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


	// Durée maximale en secondes (shift duration)
    private double maxDuration = Double.MAX_VALUE;

	// Time control
	private long lastImprovementTime;
	private long NO_IMPROVEMENT_TIMEOUT = 4000;

	// Flag to stop all recursive branches
	private boolean stopSearch = false;

	public void setNO_IMPROVEMENT_TIMEOUT(long noImp){
		this.NO_IMPROVEMENT_TIMEOUT = noImp;
	}

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
        return serviceTimes;}
	
    public void setMaxDuration(double maxDuration) {
        this.maxDuration = maxDuration;
    }

	/**
     * Entry point of the TSP solving process.
     * Performs:
     *   1. Initialization
     *   2. Nearest Neighbor heuristic as initial upper bound
     *   3. Branch & Bound search for improvements
     */
	public void chercheSolution(int tpsLimite,Graphe g ){
		if (tpsLimite <= 0) return;
		tpsDebut = System.currentTimeMillis();
		this.lastImprovementTime = tpsDebut;
		this.stopSearch = false;
		this.tpsLimite = tpsLimite;
		this.g = g;
		meilleureSolution = new Integer[g.getNbSommets()];
		Collection<Integer> nonVus = new ArrayList<Integer>(g.getNbSommets()-1);
		for (int i=1; i<g.getNbSommets(); i++) nonVus.add(i);
		Collection<Integer> vus = new ArrayList<Integer>(g.getNbSommets());
		vus.add(0); // le premier sommet visite est 0 depot

    	double heuristicCost = nearestNeighborHeuristic();

		if (heuristicCost < Double.MAX_VALUE) {
			coutMeilleureSolution = heuristicCost;
		} else{
			coutMeilleureSolution = Integer.MAX_VALUE;
			}

		branchAndBound(0, nonVus, vus, 0);
	}

	public Integer getSolution(int i){
		if (g != null && i>=0 && i<g.getNbSommets())
			return meilleureSolution[i];
		return -1;
	}

	    public double getCoutSolution(){
			if (g != null)
				return coutMeilleureSolution;
			return -1;
		}

	    public double getCoutMeilleureSolution() {
	        return coutMeilleureSolution;
	    }

	/**
	 * Methode devant etre redefinie par les sous-classes de TemplateTSP
	 * @param sommetCourant
	 * @param nonVus
	 * @return une borne inferieure du cout des chemins de <code>g</code> partant de <code>sommetCourant</code>, visitant
	 * tous les sommets de <code>nonVus</code> exactement une fois, puis retournant sur le sommet <code>0</code>.
	 */
	protected abstract double bound(Integer sommetCourant, Collection<Integer> nonVus);

	/**
	 * Methode devant etre redefinie par les sous-classes de TemplateTSP
	 * @param sommetCrt
	 * @param nonVus
	 * @param g
	 * @return un iterateur permettant d'iterer sur tous les sommets de <code>nonVus</code> qui sont successeurs de <code>sommetCourant</code>
	 */
	protected abstract Iterator<Integer> iterator(Integer sommetCrt, Collection<Integer> nonVus, Graphe g);

	/**
	 * Methode definissant le patron (template) d'une resolution par separation et evaluation (branch and bound) du TSP pour le graphe <code>g</code>.
	 * @param sommetCrt le dernier sommet visite
	 * @param nonVus la liste des sommets qui n'ont pas encore ete visites
	 * @param vus la liste des sommets deja visites (y compris sommetCrt)
	 * @param coutVus la somme des couts des arcs du chemin passant par tous les sommets de vus dans l'ordre ou ils ont ete visites
	 */
	private void branchAndBound(int sommetCrt, Collection<Integer> nonVus, Collection<Integer> vus, double coutVus){

		long currentTime = System.currentTimeMillis();

		if (stopSearch) return;
		// Stop when no improvement for some time
    	if (currentTime - lastImprovementTime > NO_IMPROVEMENT_TIMEOUT) {
        if (!stopSearch) {
                System.out.println("No improvement for " + (NO_IMPROVEMENT_TIMEOUT/1000) + "s, stopping...");
                stopSearch = true;
            }
            return;
    	}
		// Stop when exceeding global limit
		if (currentTime - tpsDebut > tpsLimite) {
        	if (!stopSearch) {
                System.out.println("Global Time limit reached");
                stopSearch = true;
            }
            return;
        }
		 // Case: all nodes visited
	    if (nonVus.size() == 0){
	    	if (g.estArc(sommetCrt,0)){ // on peut retourner au sommet de depart (0)
				double newCost = coutVus+g.getCout(sommetCrt,0);
				if (newCost < coutMeilleureSolution){
					vus.toArray(meilleureSolution);
					coutMeilleureSolution = newCost;
					lastImprovementTime = System.currentTimeMillis();
				}
	    	}
		// Explore successors only if the lower bound is promising
	    } else if (coutVus+bound(sommetCrt,nonVus) < coutMeilleureSolution){
	        Iterator<Integer> it = iterator(sommetCrt, nonVus, g);
	        while (it.hasNext()){
				if (stopSearch) return;
	        	Integer prochainSommet = it.next();
				// --- PRECEDENCE ---
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

                double nouveauCout = coutVus + addCost;

                // Pruning: doesn't explore if exceeding max duration and not better than best solution
                if (nouveauCout <= maxDuration || nouveauCout < coutMeilleureSolution) {
                    branchAndBound(prochainSommet, nonVus, vus, nouveauCout);
                }
                
                vus.remove(prochainSommet);
                nonVus.add(prochainSommet);
	        }	    
	    }
	}

	/**
     * Generates a quick feasible solution using the Nearest Neighbor heuristic.
     * Used as the initial upper bound before Branch & Bound.
     */
	protected double nearestNeighborHeuristic() {
		int n = g.getNbSommets();
		boolean[] visited = new boolean[n];
		List<Integer> route = new ArrayList<>();

		int current = 0;
		visited[0] = true;
		route.add(0);

		double totalCost = 0.0;

		//make tour
		while (route.size() < n) {
			int nearest = -1;
			double minCost = Double.MAX_VALUE;

			// search closest node
			for (int next = 1; next < n; next++) {
				if (visited[next]) continue;

				// Verify precedences
				Set<Integer> preds = precedences.getOrDefault(next, Collections.emptySet());
				boolean precedencesOk = true;
				for (Integer pred : preds) {
					if (!visited[pred]) {
						precedencesOk = false;
						break;
					}
				}
				if (!precedencesOk) continue;

				// Verify arc
				if (!g.estArc(current, next)) continue;

				double cost = g.getCout(current, next);

				// service time
				if (serviceTimes != null && next < serviceTimes.length) {
					cost += serviceTimes[next];
				}

				// Verify shift duration constraint
				if (totalCost + cost > maxDuration) continue;

				if (cost < minCost) {
					minCost = cost;
					nearest = next;
				}
			}

			// if there is no nearset
			if (nearest == -1) {
				return Double.MAX_VALUE;
			}

			visited[nearest] = true;
			route.add(nearest);
			totalCost += minCost;
			current = nearest;
		}


		if (!g.estArc(current, 0)) {
			return Double.MAX_VALUE;
		}

		double returnCost = g.getCout(current, 0);
		totalCost += returnCost;

		// verify shift duration
		if (totalCost > maxDuration) {
			System.out.println(
				"NN heuristic found solution but exceeds shift duration: " +
				String.format("%.2f", totalCost / 3600.0) + "h"
			);
		}

		// save heuristic
		for (int i = 0; i < route.size(); i++) {
			meilleureSolution[i] = route.get(i);
		}

		return totalCost;
	}
}


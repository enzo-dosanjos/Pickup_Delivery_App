package domain.service;

import domain.model.Graphe;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for TSP1 / TemplateTSP Branch & Bound implementation.
 * Uses a very small synthetic graph (4 nodes) to validate behavior.
 */
class TSPTest {

    /** Minimal fake Graphe implementation for tests */
    static class DummyGraph implements Graphe {
        private final double[][] costMatrix;

        DummyGraph(double[][] costMatrix) {
            this.costMatrix = costMatrix;
        }

        @Override
        public int getNbSommets() {
            return costMatrix.length;
        }

        @Override
        public boolean estArc(int i, int j) {
            return i != j && costMatrix[i][j] < Double.POSITIVE_INFINITY;
        }

        @Override
        public double getCout(int i, int j) {
            return costMatrix[i][j];
        }
    }

    @Test
    void testSimpleGraph() {
        // 4 nodes (0 = warehouse)
        double[][] cost = {
            {0, 2, 9, 10},
            {1, 0, 6, 4},
            {15, 7, 0, 8},
            {6, 3, 12, 0}
        };

        TSP1 tsp = new TSP1();
        DummyGraph g = new DummyGraph(cost);

        tsp.setMaxDuration(Double.MAX_VALUE);
        tsp.setNO_IMPROVEMENT_TIMEOUT(2000); // 2s no-improvement stop
        tsp.chercheSolution(5000, g);        // max 5s search

        double best = tsp.getCoutMeilleureSolution();
        assertTrue(best > 0 && best < Double.MAX_VALUE, "Solution cost should be valid");
    }

    @Test
    void testPrecedenceConstraint() {
        // 3 nodes + depot
        double[][] cost = {
            {0, 1, 5, 8},
            {1, 0, 1, 4},
            {5, 1, 0, 3},
            {8, 4, 3, 0}
        };

        TSP1 tsp = new TSP1();
        DummyGraph g = new DummyGraph(cost);

        // delivery(3) must come after pickup(1)
        Map<Integer, Set<Integer>> precedences = new HashMap<>();
        precedences.put(3, Set.of(1));
        tsp.setPrecedences(precedences);

        tsp.chercheSolution(5000, g);

        // Verify route consistency: 1 must appear before 3
        List<Integer> path = new ArrayList<>();
        for (int i = 0; i < g.getNbSommets(); i++)
            path.add(tsp.getSolution(i));

        int pickupPos = path.indexOf(1);
        int deliveryPos = path.indexOf(3);
        assertTrue(pickupPos < deliveryPos, "Pickup should come before delivery.");
    }

    @Test
    void testServiceTimesAffectCost() {
        double[][] cost = {
            {0, 1, 1},
            {1, 0, 1},
            {1, 1, 0}
        };

        TSP1 tsp = new TSP1();
        DummyGraph g = new DummyGraph(cost);

        // service time at each node in seconds
        double[] serviceTimes = {0, 10, 20};
        tsp.setServiceTimes(serviceTimes);

        tsp.chercheSolution(3000, g);
        double total = tsp.getCoutMeilleureSolution();

        assertTrue(total >= 30, "Total cost should include service times.");
    }
}
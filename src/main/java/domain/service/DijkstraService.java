/*package domain.service;

import domain.model.Graphe;
import domain.model.GrapheComplet;
import domain.model.Intersection;
import domain.model.Map;
import domain.model.RoadSegment;
import domain.model.PickupDelivery;
import domain.model.Request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class DijkstraService {
    static class Node implements Comparable<Node> {
        long vertex;
        int distance;

        Node(long v, int d) {
            this.vertex = v;
            this.distance = d;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.distance, other.distance);
        }
    }

    static class CellInfo {
        int distance;
        long precedent;
        boolean visited;

        CellInfo(int distance, long precedent, boolean visited) {
            this.distance = distance;
            this.precedent = precedent;
            this.visited = visited;
        }
    }

    static class TableUltime {
        Map<Long, Map<Long, CellInfo> table;

        TableUltime() {
            this.table = new HashMap<>();
        }

        // Add or replace a cell with individual values
        public void put(long row, long col, int distance, long precedent, boolean visited) {
            table.computeIfAbsent(row, r -> new HashMap<>())
                    .put(col, new CellInfo(distance, precedent, visited));
        }

        // Add or replace a cell with a CellInfo object
        public void put(long row, long col, CellInfo cellInfo) {
            table.computeIfAbsent(row, r -> new HashMap<>())
                    .put(col, cellInfo);
        }

        // Retrieve a cell
        public CellInfo get(long row, long col) {
            Map<Long, CellInfo> rowMap = table.get(row);
            if (rowMap == null) return null;
            return rowMap.get(col);
        }

        // Check if a cell exists
        public boolean contains(long row, long col) {
            Map<Long, CellInfo> rowMap = table.get(row);
            return rowMap != null && rowMap.containsKey(col);
        }

        // Remove a cell
        public void remove(long row, long col) {
            Map<Long, CellInfo> rowMap = table.get(row);
            if (rowMap != null) {
                rowMap.remove(col);
                if (rowMap.isEmpty()) table.remove(row);
            }
        }
    }

    public static void computeShortestPath(Map map, GrapheComplet g, long[][] predecesseurs) {
    // Calculate the shortest paths between all intersections that need to be visited using Dijkstra's algorithm
        //long[][] etatNoeuds = new long[g.getNbSommets()][g.getNbSommets()];
        //for (int i = 0; i < g.getNbSommets(); i++) { Arrays.fill(etatNoeuds[i], -1); }

        TableUltime tableUltime = new TableUltime();
        for (Long row : map.getIntersections().keySet()) {
            for (Long col : map.getIntersections().keySet()) {
                int distance;
                if (row.equals(col)) {
                    distance = 0;
                } else {
                    int distance = Integer.MAX_VALUE;
                }
                long precedent = -1;
                boolean visited = false;
                tableUltime.put(row, col, distance, precedent, visited);
            }
        }


        for (int i: g.getNbSommets()) {
            Dijkstra(map, g, i, tableUltime);
        }
    }

    private static void Dijkstra(Map map, GrapheComplet grapheComplet, int start, TableUltime tableUltime) {
        // Calculate the shortest paths using Dijkstra's algorithm
        grapheComplet.cout[start][start] = 0;
        long[] sommets = grapheComplet.getSommets();
        adjencyList = map.getAdjencyList();
        //int compteur = 1;
        PriorityQueue<Node> pq = new PriorityQueue<>();
        pq.add(new Node(sommets[start], 0));
        while (!pq.isEmpty()) {//&& compteur < grapheComplet.getNbSommets()) {
            Node currentNode = pq.poll();
            long currentVertex = currentNode.vertex;
            CellInfo<T> currentCell = tableUltime.get(sommets[start], currentVertex);
            if (currentCell.visited) continue;
            currentCell.visited = true;
            RoadSegment[] neighbors = adjencyList.get(currentVertex);
            if (neighbors != null) {
                for (RoadSegment segment : neighbors) {
                    long neighborVertex = segment.getEndId();
                    CellInfo<T> neighborCell = tableUltime.get(sommets[start], neighborVertex);
                    if (neighborCell != null && !neighborCell.visited) {
                        int newDist = currentCell.distance + segment.getLength();
                        if (newDist < neighborCell.distance) {
                            neighborCell.distance = newDist;
                            neighborCell.precedent = currentVertex;
                            pq.add(new Node(neighborVertex, newDist));
                            tableUltime.put(sommets[start], neighborVertex, neighborCell);
                        }
                    }
                }
            }
            // Test pour savoir si le sommet est dans grapheComplet
            for (int j = 0; j < grapheComplet.getNbSommets(); j++) {
                if (sommets[j] == currentVertex) {
                    grapheComplet.cout[start][j] = currentNode.distance;
                    //compteur++;
                }
            }
            // Ajout pour compléter tableUltime avec les degats colatéraux
            long tempvertex = currentCell.precedent;
            if (tempvertex == -1) continue;
            long firstPrecedentVertex = tempvertex;
            int laDistance = currentCell.distance - tableUltime.get(sommets[start], tempvertex).distance;
            while (tempvertex != sommets[start]) {
                tableUltime.put(tempvertex, currentVertex, laDistance + tableUltime.get(tempvertex, firstPrecedentVertex).distance, firstPrecedentVertex, true);
                // Test pour savoir si le sommet est dans grapheComplet
                for (int i = 0; i < grapheComplet.getNbSommets(); i++) {
                    if (sommets[i] == tempvertex) {
                        for (int j = 0; j < grapheComplet.getNbSommets(); j++) {
                            if (sommets[j] == currentVertex) {
                                grapheComplet.cout[i][j] = laDistance + tableUltime.get(tempvertex, firstPrecedentVertex).distance;
                            }
                        }
                    }
                }
                tempvertex = tableUltime(sommets[start], tempvertex).precedent;
            }
        }
    }
}

 */
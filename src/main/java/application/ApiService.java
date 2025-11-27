package application;

import domain.model.*;
import domain.service.MapService;
import domain.service.TemplateTSP;
import domain.service.TSP;
import persistence.XMLParsers;

import java.util.*;
import java.util.Map;
import java.util.stream.Collectors;

public class ApiService {

    private final MapService mapService;
    private final Map<Long, Request> requests;
    private final Map<Long, Courier> couriers;

    public ApiService() {
        this.mapService = new MapService("");
        this.requests = new HashMap<>();
        this.couriers = new HashMap<>();
    }

    public boolean loadMap(String filePath) {
        try {
            this.mapService.loadMap(filePath);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void addRequest(Request request) {
        this.requests.put(request.getId(), request);
    }

    public void addCourier(Courier courier) {
        this.couriers.put(courier.getId(), courier);
    }

    public Collection<Intersection> getAllIntersections() {
        return this.mapService.getMap().getIntersections().values();
    }
    
    public domain.model.Map getMap() {
        return this.mapService.getMap();
    }

    public Tour calculateTour(long courierId, List<Long> requestIds, long warehouseId) {
        if (!couriers.containsKey(courierId)) return null;

        List<Request> currentRequests = requestIds.stream().map(requests::get).collect(Collectors.toList());

        List<Long> points = new ArrayList<>();
        points.add(warehouseId);
        for (Request req : currentRequests) {
            points.add(req.getPickupIntersectionId());
            points.add(req.getDeliveryIntersectionId());
        }
        List<Long> uniquePoints = points.stream().distinct().collect(Collectors.toList());
        int nbPoints = uniquePoints.size();

        int[][] costs = new int[nbPoints][nbPoints];
        for (int i = 0; i < nbPoints; i++) {
            for (int j = 0; j < nbPoints; j++) {
                if (i == j) {
                    costs[i][j] = 0;
                } else {
                    Path path = dijkstra(uniquePoints.get(i), uniquePoints.get(j));
                    costs[i][j] = path != null ? (int)path.distance : Integer.MAX_VALUE;
                }
            }
        }

        Graphe g = new GrapheComplet(costs);

        Map<Long, Long> pickupToDelivery = new HashMap<>();
        for(Request req : currentRequests) {
            pickupToDelivery.put(req.getPickupIntersectionId(), req.getDeliveryIntersectionId());
        }

        TSP tsp = new TemplateTSP() {
            private final Map<Integer, Integer> deliveryToPickupIndex = new HashMap<>();
            {
                for (int i = 0; i < nbPoints; i++) {
                    long currentPointId = uniquePoints.get(i);
                    if (pickupToDelivery.containsValue(currentPointId)) { // if it is a delivery point
                        for(Map.Entry<Long, Long> entry : pickupToDelivery.entrySet()){
                            if(entry.getValue() == currentPointId){
                                long pickupPointId = entry.getKey();
                                int pickupIndex = uniquePoints.indexOf(pickupPointId);
                                deliveryToPickupIndex.put(i, pickupIndex);
                                break;
                            }
                        }
                    }
                }
            }
            
            @Override
            protected int bound(Integer sommetCourant, Collection<Integer> nonVus) {
                return 0;
            }

            @Override
            protected Iterator<Integer> iterator(Integer sommetCrt, Collection<Integer> nonVus, Graphe g) {
                List<Integer> allowed = new ArrayList<>();
                for (Integer nv : nonVus) {
                    if (deliveryToPickupIndex.containsKey(nv)) { // is a delivery point
                        int pickupIndex = deliveryToPickupIndex.get(nv);
                        if (nonVus.contains(pickupIndex)) {
                            continue; // pickup not visited yet
                        }
                    }
                    allowed.add(nv);
                }
                return allowed.iterator();
            }
        };

        tsp.chercheSolution(10000, g); 
        
        List<TourStop> stops = new ArrayList<>();
        long totalDistance = tsp.getCoutSolution();
        long totalDuration = 0; // Needs real calculation. Placeholder for now.

        for (int i = 0; i < nbPoints; i++) {
            Integer M_intersec_index = tsp.getSolution(i);
            if (M_intersec_index == null) continue;

            long intersectionId = uniquePoints.get(M_intersec_index);
            if (intersectionId == warehouseId) continue; // Skip warehouse in the stops list

            StopType stopType = null;
            long requestId = -1;
            long duration = 0;

            for (Request req : currentRequests) {
                if (req.getPickupIntersectionId() == intersectionId) {
                    stopType = StopType.PICKUP;
                    requestId = req.getId();
                    duration = req.getPickupDuration();
                    break;
                } else if (req.getDeliveryIntersectionId() == intersectionId) {
                    stopType = StopType.DELIVERY;
                    requestId = req.getId();
                    duration = req.getDeliveryDuration();
                    break;
                }
            }

            // Simple duration calculation, assuming travel time is not yet calculated
            long arrivalTime = totalDuration;
            totalDuration += duration;
            long departureTime = totalDuration;

            if (stopType != null) {
                stops.add(new TourStop(stopType, requestId, intersectionId, arrivalTime, departureTime));
            }
        }
        
        return new Tour(courierId, stops, totalDistance, totalDuration);
    }
    
    private static class Path {
        double distance;
        List<Long> intersections;
        Path(double d, List<Long> i) { distance = d; intersections = i; }
    }

    private Path dijkstra(long startId, long endId) {
        Map<Long, Double> distances = new HashMap<>();
        Map<Long, Long> predecessors = new HashMap<>();
        PriorityQueue<Long> pq = new PriorityQueue<>(Comparator.comparing(distances::get));
        
        for (Long intersectionId : mapService.getMap().getIntersections().keySet()) {
            distances.put(intersectionId, Double.MAX_VALUE);
        }
        distances.put(startId, 0.0);
        pq.add(startId);

        while (!pq.isEmpty()) {
            long currentId = pq.poll();

            if(currentId == endId) break;

            RoadSegment[] segments = mapService.getMap().getAdjencyList().get(currentId);
            if(segments == null) continue;

            for (RoadSegment segment : segments) {
                long neighborId = segment.getEndId();
                double newDist = distances.get(currentId) + segment.getLength();
                if (newDist < distances.get(neighborId)) {
                    distances.put(neighborId, newDist);
                    predecessors.put(neighborId, currentId);
                    pq.remove(neighborId);
                    pq.add(neighborId);
                }
            }
        }
        
        List<Long> pathIntersections = new ArrayList<>();
        Long step = endId;
        if (predecessors.get(step) == null && step != startId) return null;
        while(step != null) {
            pathIntersections.add(0, step);
            step = predecessors.get(step);
        }
        if (!pathIntersections.isEmpty() && pathIntersections.get(0) == startId) {
            return new Path(distances.get(endId), pathIntersections);
        }
        return null;
    }
}
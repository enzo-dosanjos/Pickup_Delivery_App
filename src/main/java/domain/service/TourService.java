package domain.service;

import domain.model.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TourService {

    long courierId;

    public void assignRequestsToCouriers() {
        // Implementation will follow
    }

    public Tour convertGraphToTour(PickupDelivery pickupDelivery, LocalDateTime startTime, long courierId, Integer[] solution, Long[] vertices, double[][] costs) {
        long intersectionId;
        Integer previousTourStop = null;

        Request request;

        StopType stopType;
        TourStop tourStop;
        LocalDateTime arrivalTime, departureTime;
        Duration duration;

        Tour tour = new Tour(courierId, startTime);
        double distance = 0.0;
        double minutes = 0.0;
        boolean first = true;
        Duration commuteDuration = Duration.ZERO;

        for(Integer i : solution)
        {
            duration = Duration.ZERO;
            intersectionId = vertices[i];
            var result = pickupDelivery.findRequestByIntersectionId(intersectionId);
            request = result.getKey();
            stopType = result.getValue();
            arrivalTime =  tour.getStartTime().plus(tour.getTotalDuration());

            if (stopType == StopType.PICKUP)
            {
                duration = request.getPickupDuration();
            }

            else if(stopType == StopType.DELIVERY)
            {
                duration = request.getDeliveryDuration();
            }

            if(first)
            {
                first = false;
            }
            else
            {
                minutes = costs[previousTourStop][i];
                long wholeMinutes = (long) minutes;
                long seconds = Math.round((minutes - wholeMinutes) * 60);
                commuteDuration = Duration.ofMinutes(wholeMinutes).plusSeconds(seconds);
                arrivalTime = arrivalTime.plus(commuteDuration);
            }
            departureTime = arrivalTime.plus(duration);
            tourStop = new TourStop(stopType, request.getId(), intersectionId, arrivalTime, departureTime);
            tour.addStop(tourStop);
            tour.updateTotalDuration(duration.plus(commuteDuration));
            previousTourStop = i;

        }
        return tour;
    }
}

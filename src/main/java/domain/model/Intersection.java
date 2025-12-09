package domain.model;

/**
 * Represents an intersection with a unique identifier, latitude, and longitude.
 */
public class Intersection {

    private final long id; // Unique identifier for the intersection, used to link it to the graph's vertices.


    private final double lat; // The latitude of the intersection.


    private final double lng;  // The longitude of the intersection.

    /**
     * Constructs an Intersection with the specified id, latitude, and longitude.
     *
     * @param id the unique identifier of the intersection
     * @param lat the latitude of the intersection
     * @param lng the longitude of the intersection
     */
    public Intersection(long id, double lat, double lng) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
    }


    public long getId() {
        return id;
    }


    public double getLat() {
        return lat;
    }


    public double getLng() {
        return lng;
    }


    public String toString() {
        return "Intersection{id=" + id + ", lat=" + lat + ", lng=" + lng + "}";
    }
}
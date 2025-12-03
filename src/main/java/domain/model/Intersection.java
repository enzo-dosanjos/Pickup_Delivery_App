package domain.model;

/**
 * Represents an intersection with a unique identifier, latitude, and longitude.
 */
public class Intersection {
    /** The unique identifier of the intersection. */
    private final long id;

    /** The latitude of the intersection. */
    private final double lat;

    /** The longitude of the intersection. */
    private final double lng;

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

    /**
     * Retrieves the unique identifier of the intersection.
     *
     * @return the id of the intersection
     */
    public long getId() {
        return id;
    }

    /**
     * Retrieves the latitude of the intersection.
     *
     * @return the latitude of the intersection
     */
    public double getLat() {
        return lat;
    }

    /**
     * Retrieves the longitude of the intersection.
     *
     * @return the longitude of the intersection
     */
    public double getLng() {
        return lng;
    }

    /**
     * Returns a string representation of the intersection.
     *
     * @return a string in the format "Intersection{id=ID, lat=LAT, lng=LNG}"
     */
    public String toString() {
        return "Intersection{id=" + id + ", lat=" + lat + ", lng=" + lng + "}";
    }
}
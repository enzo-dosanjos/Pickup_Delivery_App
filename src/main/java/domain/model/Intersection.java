package domain.model;

public class Intersection {
    private final long id;
    private final double lat;
    private final double lng;

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

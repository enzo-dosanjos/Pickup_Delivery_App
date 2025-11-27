package domain.model;

public class Courier {
    private long id;
    private String name;
    private int numGivenRequests;

    public Courier(long id, String name) {
        this.id = id;
        this.name = name;
        this.numGivenRequests = 0;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getNumGivenRequests() {
        return numGivenRequests;
    }

    public void setNumGivenRequests(int numGivenRequests) {
        this.numGivenRequests = numGivenRequests;
    }
}

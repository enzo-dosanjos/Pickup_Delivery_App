package domain.model;

public enum AvailabilityStatus {
    AVAILABLE(0),
    BUSY(1);

    final int type;

    AvailabilityStatus(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

}

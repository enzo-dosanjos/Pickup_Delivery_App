package domain.model;

public enum StopType {
    PICKUP(1),
    DELIVERY(2),
    WAREHOUSE(0);

    final int type;

    StopType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

}

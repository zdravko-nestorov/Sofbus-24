package bg.znestorov.sofbus24.apidb.entity;

public enum VehicleType {
    BUS(1), TROLLEY(2), TRAM(3), BTT(4), METRO1(5), METRO2(6);

    private int order;

    VehicleType(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}

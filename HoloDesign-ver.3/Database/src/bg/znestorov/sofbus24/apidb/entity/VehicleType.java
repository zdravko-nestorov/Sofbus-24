package bg.znestorov.sofbus24.apidb.entity;

import org.apache.commons.lang3.StringUtils;

public enum VehicleType {
    BUS(1), TROLLEY(2), TRAM(3), BTT(4), METRO1(5), METRO2(6);

    private int order;

    VehicleType(int order) {
        this.order = order;
    }

    public static VehicleType parseVehicleType(String vehicleType) {
        if (StringUtils.isBlank(vehicleType)) {
          return BUS;
        }
        if ("SUPPLY".equals(vehicleType)) {
          return BUS;
        }
        return VehicleType.valueOf(vehicleType);
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}

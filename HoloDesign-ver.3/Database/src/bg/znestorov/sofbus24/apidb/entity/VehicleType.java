package bg.znestorov.sofbus24.apidb.entity;

import bg.znestorov.sofbus24.apidb.utils.Utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum VehicleType {
  BUS(1, 5),
  TROLLEY(4),
  TRAM(2),
  METRO1(3),
  METRO2(3),
  METRO3(3),
  METRO4(3);

  private final Set<Integer> skgtVehicleType;

  VehicleType(Integer... skgtVehicleType) {
    this.skgtVehicleType = new HashSet<>(Arrays.asList(skgtVehicleType));
  }

  public static VehicleType getVehicleType(int skgtVehicleType, String skgtVehicleExtId) {
    for (VehicleType vehicleType : VehicleType.values()) {
      if (skgtVehicleType == 3) {
        // Metro vehicle types
        if (vehicleType.getSkgtVehicleType().contains(skgtVehicleType) &&
            vehicleType.name().contains(Utils.getOnlyDigits(skgtVehicleExtId))) {
          return vehicleType;
        }
      } else {
        // Standard vehicle types
        if (vehicleType.getSkgtVehicleType().contains(skgtVehicleType)) {
          return vehicleType;
        }
      }
    }
    return null;
  }

  public Set<Integer> getSkgtVehicleType() {
    return skgtVehicleType;
  }
}

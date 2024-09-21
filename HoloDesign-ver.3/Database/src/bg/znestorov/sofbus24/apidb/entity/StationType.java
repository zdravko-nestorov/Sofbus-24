package bg.znestorov.sofbus24.apidb.entity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum StationType {
  BTT(1, 2, 4, 5),
  METRO(3);

  private final Set<Integer> skgtVehicleType;

  StationType(Integer... skgtVehicleType) {
    this.skgtVehicleType = new HashSet<>(Arrays.asList(skgtVehicleType));
  }

  public static StationType getStationType(int skgtVehicleType) {
    for (StationType stationType : StationType.values()) {
      if (stationType.getSkgtVehicleType().contains(skgtVehicleType)) {
        return stationType;
      }
    }
    return null;
  }

  public Set<Integer> getSkgtVehicleType() {
    return skgtVehicleType;
  }
}

package bg.znestorov.sofbus24.apidb.entity;

import bg.znestorov.sofbus24.apidb.utils.Utils;
import com.google.gson.annotations.SerializedName;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Vehicle implements Comparable<Vehicle> {

  @SerializedName("line_id")
  private int lineId;
  private String name;
  @SerializedName("ext_id")
  private String extId;
  private int type;

  private String direction;
  private Map<VehicleRoute, List<Station>> routes;

  // -------------------------- //
  // SOFBUS 24 DATABASE FIELDS  //
  // -------------------------- //
  public String getSofbusNumber() {
    return name;
  }

  public VehicleType getSofbusType() {
    return VehicleType.getVehicleType(type, extId);
  }

  public String getSofbusDirection() {
    return direction;
  }

  // -------------------------- //
  // SOFBUS 24 COMMON FIELDS    //
  // -------------------------- //
  public String getSofbusLabel() {
    return getSofbusType() + " #" + getSofbusNumber();
  }

  public int getSofbusNameLeadingDigits() {
    try {
      return Integer.parseInt(name.split("(?=\\D)")[0]);
    } catch (Exception e) {
      return Integer.MAX_VALUE;
    }
  }

  public String getSofbusNameChars() {
    try {
      return name.replaceAll("[0-9]", "");
    } catch (Exception e) {
      return null;
    }
  }

  public int getSofbusNameDigits() {
    try {
      return Integer.parseInt(name.replaceAll("[^0-9]", ""));
    } catch (Exception e) {
      return Integer.MAX_VALUE;
    }
  }

  // -------------------------- //
  // SGKT FIELDS                //
  // -------------------------- //
  public int getLineId() {
    return lineId;
  }

  public void setLineId(int lineId) {
    this.lineId = lineId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getExtId() {
    return extId;
  }

  public void setExtId(String extId) {
    this.extId = extId;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public String getDirection() {
    return direction;
  }

  public void setDirection(String direction) {
    this.direction = direction;
  }

  public Map<VehicleRoute, List<Station>> getRoutes() {
    return routes;
  }

  public void setRoutes(Map<VehicleRoute, List<Station>> routes) {
    this.routes = routes;
  }

  @Override
  public int compareTo(Vehicle vehicle) {
    return
        // FIRST compare by the vehicle type
        Comparator.comparing(Vehicle::getSofbusType)
            // SECOND compare by the vehicle name leading digits (leading digits)
            .thenComparingInt(Vehicle::getSofbusNameLeadingDigits)
            // THIRD compare by the vehicle name chars (non-digits)
            .thenComparing(Vehicle::getSofbusNameChars)
            // FOURTH compare by the vehicle name digits (all digits)
            .thenComparingInt(Vehicle::getSofbusNameDigits)
            // LAST compare the vehicle hashes
            .compare(this, vehicle);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Vehicle vehicle = (Vehicle) o;
    return Objects.equals(lineId, vehicle.lineId)
        && Objects.equals(extId, vehicle.extId)
        && Objects.equals(type, vehicle.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lineId, extId, type);
  }

  @Override
  public String toString() {
    return "Vehicle{" +
        "lineId=" + lineId +
        ", name='" + name + '\'' +
        ", extId='" + extId + '\'' +
        ", type=" + type +
        ", direction='" + direction + '\'' +
        ", routes=" + routes +
        '}';
  }
}

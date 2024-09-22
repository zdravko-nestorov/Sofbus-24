package bg.znestorov.sofbus24.apidb.entity;

import com.google.gson.annotations.SerializedName;

import java.util.Comparator;
import java.util.Objects;

public class VehicleRoute implements Comparable<VehicleRoute> {

  private int sofbusRouteId;

  @SerializedName("id")
  private int routeId;
  @SerializedName("line_id")
  private int lineId;
  private String name;
  @SerializedName("route_ref")
  private int routeRef;

  // -------------------------- //
  // SOFBUS 24 DATABASE FIELDS  //
  // -------------------------- //
  public int getSofbusRouteId() {
    return sofbusRouteId;
  }

  public void setSofbusRouteId(int sofbusRouteId) {
    this.sofbusRouteId = sofbusRouteId;
  }

  // -------------------------- //
  // SGKT DATABASE FIELDS       //
  // -------------------------- //
  public int getRouteId() {
    return routeId;
  }

  public void setRouteId(int routeId) {
    this.routeId = routeId;
  }

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

  public int getRouteRef() {
    return routeRef;
  }

  public void setRouteRef(int routeRef) {
    this.routeRef = routeRef;
  }

  @Override
  public int compareTo(VehicleRoute vehicleRoute) {
    return
        // FIRST compare by the route id
        Comparator.comparing(VehicleRoute::getRouteId)
            // SECOND compare by the line id
            .thenComparing(VehicleRoute::getLineId)
            // LAST compare the route hashes
            .compare(this, vehicleRoute);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VehicleRoute station = (VehicleRoute) o;
    return Objects.equals(routeId, station.routeId) && Objects.equals(lineId, station.lineId) &&
        Objects.equals(name, station.name) && Objects.equals(routeRef, station.routeRef);
  }

  @Override
  public int hashCode() {
    return Objects.hash(routeId, lineId, name, routeRef);
  }

  @Override
  public String toString() {
    return "Route{" + "sofbusId=" + sofbusRouteId + ", routeId='" + routeId + '\'' + ", lineId=" + lineId + ", name='" +
        name + '\'' + ", routeRef='" + routeRef + '\'' + '}';
  }
}

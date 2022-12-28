package bg.znestorov.sofbus24.apidb.entity;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import bg.znestorov.sofbus24.apidb.utils.Utils;

public class Vehicle implements Comparable<Vehicle> {

    private VehicleType type;
    private String id;
    private String name;
    private String direction;
    private Map<Integer, List<Station>> routes;

    public Vehicle() {
    }

    public Vehicle(VehicleType type, String id, String name, String direction, Map<Integer, List<Station>> routes) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.direction = direction;
        this.routes = routes;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getNameLeadingDigits() {
        try {
            return Integer.parseInt(name.split("(?=\\D)")[0]);
        } catch (Exception e) {
            return Integer.MAX_VALUE;
        }
    }

    public int getNameDigits() {
        try {
            return Integer.parseInt(name.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return Integer.MAX_VALUE;
        }
    }

    public String getNameChars() {
        try {
            return name.replaceAll("[0-9]", "");
        } catch (Exception e) {
            return null;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirection() {
        return direction.toUpperCase();
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void setDirection() {
        this.direction = Utils.formDirection(this.routes);
    }

    public Map<Integer, List<Station>> getRoutes() {
        return routes;
    }

    public void setRoutes(Map<Integer, List<Station>> routes) {
        this.routes = routes;
    }

    public String getLabel() {
        return this.type + " #" + this.name;
    }

    @Override
    public int compareTo(Vehicle vehicle) {
        return Comparator.comparing(Vehicle::getType)
                // FIRST compare by the vehicle name leading digits (leading digits)
                .thenComparingInt(Vehicle::getNameLeadingDigits)
                // SECOND compare by the vehicle name chars (non-digits)
                .thenComparing(Vehicle::getNameChars)
                // THIRD compare by the vehicle name digits (all digits)
                .thenComparingInt(Vehicle::getNameDigits)
                // LAST compare the vehicle hashes
                .compare(this, vehicle);
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "type=" + type +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", direction='" + direction + '\'' +
                ", routes=" + routes +
                '}';
    }
}

package bg.znestorov.sofbus24.apidb.vehicles;

import static bg.znestorov.sofbus24.apidb.utils.Constants.PT_PROPERTIES_LINES;
import static bg.znestorov.sofbus24.apidb.utils.Constants.PT_ROUTES_SEGMENTS;
import static bg.znestorov.sofbus24.apidb.utils.Constants.PT_ROUTES_SEGMENTS_STOP;
import static bg.znestorov.sofbus24.apidb.utils.Constants.PT_ROUTES_SEGMENTS_STOP_CODE;
import static bg.znestorov.sofbus24.apidb.utils.Utils.readPublicTransportUrl;
import static bg.znestorov.sofbus24.apidb.utils.Utils.readScheduleUrl;

import bg.znestorov.sofbus24.apidb.entity.Station;
import bg.znestorov.sofbus24.apidb.entity.Vehicle;
import bg.znestorov.sofbus24.apidb.entity.VehicleRoute;
import bg.znestorov.sofbus24.apidb.logger.DBLogger;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.IntStream;

public class RetrieveVehiclesMain {

  public static Set<Vehicle> getVehicles(Map<String, Station> stationMap) {

    // Retrieve the Vehicles JSON from "Sofia Traffic API"
    String vehiclesGson = readPublicTransportUrl(PT_PROPERTIES_LINES);

    // Convert the GSON Vehicle objects to a set of Vehicles
    Type listType = new TypeToken<LinkedHashSet<Vehicle>>() {
    }.getType();
    Set<Vehicle> vehicleSet = new Gson().fromJson(vehiclesGson, listType);
    if (CollectionUtils.isEmpty(vehicleSet)) {
      DBLogger.log(Level.WARNING, "Problem to parse the Vehicles GSON...");
      return vehicleSet;
    }

    // Add the Vehicle routes to the Vehicle set
    addVehicleRoutes(vehicleSet, stationMap);

    // Sort the set of vehicles
    List<Vehicle> vehiclesList = new ArrayList<>(vehicleSet);
    Collections.sort(vehiclesList);

    // Transform a list of Vehicles to a LinkedHashSet
    return new LinkedHashSet<>(vehiclesList);
  }

  private static void addVehicleRoutes(Set<Vehicle> vehicleSet, Map<String, Station> stationMap) {
    // Iterate the Vehicle set
    vehicleSet.forEach(vehicle -> {

      // Retrieve the Routes JSON from "Sofia Traffic API"
      String vehicleRoutesGson = readScheduleUrl(vehicle);
      if (StringUtils.isBlank(vehicleRoutesGson)) {
        return;
      }

      // Convert the GSON Vehicle routes to a set of JsonObjects (JSON: routes)
      Type listTypeJsonObject = new TypeToken<LinkedList<JsonObject>>() {
      }.getType();
      List<JsonObject> vehicleRoutesList = new Gson().fromJson(vehicleRoutesGson, listTypeJsonObject);

      // Create an empty vehicle routes map
      Map<VehicleRoute, List<Station>> vehicleRoutesMap = new LinkedHashMap<>();

      // Iterate the vehicle routes (JSON: routes)
      IntStream.range(0, vehicleRoutesList.size()).forEach(i -> {

        // Retrieve the next route from the set
        JsonObject vehicleRouteJsonElement = vehicleRoutesList.get(i);
        Type listTypeRoute = new TypeToken<VehicleRoute>() {
        }.getType();

        VehicleRoute vehicleRoute = new Gson().fromJson(vehicleRouteJsonElement, listTypeRoute);
        vehicleRoute.setSofbusRouteId(i + 1);
        vehicleRoutesMap.put(vehicleRoute, new ArrayList<>());

        // Retrieve the vehicle route segments (JSON: routes.segments)
        JsonArray vehicleRouteSegmentsJsonArray = vehicleRouteJsonElement.getAsJsonArray(PT_ROUTES_SEGMENTS);

        // Iterate the vehicle route segments (JSON: routes.segments)
        vehicleRouteSegmentsJsonArray.forEach(vehicleRouteSegmentsJsonElement -> {

          // Retrieve the next route segment from the array
          String stationCode =
              vehicleRouteSegmentsJsonElement.getAsJsonObject().getAsJsonObject(PT_ROUTES_SEGMENTS_STOP)
                  .getAsJsonPrimitive(PT_ROUTES_SEGMENTS_STOP_CODE).getAsString();

          // Add the station to the vehicle route map
          Station station = getStation(stationMap, stationCode);
          if (station != null) {
            vehicleRoutesMap.get(vehicleRoute).add(station);
          }
        });
      });

      // Set the Vehicle ROUTES & DIRECTION
      vehicle.setRoutes(vehicleRoutesMap);
      vehicle.setDirection(getDirection(vehicle));
    });
  }

  private static Station getStation(Map<String, Station> stationMap, String stationCode) {
    Station station = stationMap.get(stationCode);
    if (station != null) {
      return station;
    }

    DBLogger.log(Level.WARNING, "Couldn't find station number #" + stationCode);
    return null;
  }

  private static String getDirection(Vehicle vehicle) {
    if (vehicle.getType() == 3) {
      List<Station> stations = vehicle.getRoutes().entrySet().iterator().next().getValue();
      return String.format("%s - %s", stations.get(0).getName(), stations.get(stations.size() - 1).getName());
    } else {
      return vehicle.getRoutes().entrySet().iterator().next().getKey().getName();
    }
  }
}

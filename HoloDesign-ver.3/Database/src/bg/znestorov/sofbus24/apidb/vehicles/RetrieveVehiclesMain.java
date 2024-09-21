package bg.znestorov.sofbus24.apidb.vehicles;

import static bg.znestorov.sofbus24.apidb.utils.Constants.PT_PROPERTIES_LINES;
import static bg.znestorov.sofbus24.apidb.utils.Constants.PT_ROUTES_SEGMENTS;
import static bg.znestorov.sofbus24.apidb.utils.Constants.PT_ROUTES_SEGMENTS_STOP;
import static bg.znestorov.sofbus24.apidb.utils.Constants.PT_ROUTES_SEGMENTS_STOP_CODE;
import static bg.znestorov.sofbus24.apidb.utils.Utils.readPublicTransportUrl;
import static bg.znestorov.sofbus24.apidb.utils.Utils.readScheduleUrl;

import bg.znestorov.sofbus24.apidb.entity.Station;
import bg.znestorov.sofbus24.apidb.entity.Vehicle;
import bg.znestorov.sofbus24.apidb.logger.DBLogger;
import bg.znestorov.sofbus24.apidb.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
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

      // Convert the GSON Vehicle routes to a set of JsonObjects
      Type listType = new TypeToken<LinkedList<JsonObject>>() {
      }.getType();
      List<JsonObject> vehicleRoutesList = new Gson().fromJson(vehicleRoutesGson, listType);
      String[][] vehicleRoutesArr = new String[vehicleRoutesList.size()][];

      // Iterate the vehicle routes (routes)
      IntStream.range(0, vehicleRoutesList.size()).forEach(i -> {

        // Retrieve the next route from the set
        JsonObject vehicleRouteJsonElement = vehicleRoutesList.get(i);
        JsonArray vehicleRouteSegmentsJsonArray = vehicleRouteJsonElement.getAsJsonArray(PT_ROUTES_SEGMENTS);
        vehicleRoutesArr[i] = new String[vehicleRouteSegmentsJsonArray.size()];

        // Iterate the vehicle route segments (routes.segments)
        IntStream.range(0, vehicleRouteSegmentsJsonArray.size()).forEach(j -> {

          // Retrieve the next route segment from the array
          String stationCode = vehicleRouteSegmentsJsonArray.get(j)
              .getAsJsonObject()
              .getAsJsonObject(PT_ROUTES_SEGMENTS_STOP)
              .getAsJsonPrimitive(PT_ROUTES_SEGMENTS_STOP_CODE)
              .getAsString();
          vehicleRoutesArr[i][j] = stationCode;
        });
      });

      // Set the Vehicle ROUTES & DIRECTION
      Map<Integer, List<Station>> vehicleRoutesMap = getVehicleRoutes(stationMap, vehicleRoutesArr);
      vehicle.setRoutes(vehicleRoutesMap);
      vehicle.setDirection(Utils.formDirection(vehicleRoutesMap));
    });
  }

  private static Map<Integer, List<Station>> getVehicleRoutes(Map<String, Station> stationMap,
      String[][] vehicleArrRoutes) {
    Map<Integer, List<Station>> vehicleMapRoutes = new LinkedHashMap<>();

    // Create the routes only in case the ROUTES multi-dimensional array is not
    // empty (passed as a constant)
    if (ArrayUtils.isNotEmpty(vehicleArrRoutes)) {
      for (int i = 0; i < vehicleArrRoutes.length; i++) {
        vehicleMapRoutes.put((i + 1), getVehicleRoute(stationMap, vehicleArrRoutes[i]));
      }
    }

    return vehicleMapRoutes;
  }

  private static List<Station> getVehicleRoute(Map<String, Station> stationMap, String[] vehicleRoute) {
    List<Station> vehicleStations = new ArrayList<>();

    // Create the route (fill it with Station objects) only in case the ROUTES
    // multi-dimensional array is not empty (passed as a constant)
    if (ArrayUtils.isNotEmpty(vehicleRoute)) {
      for (String stationCode : vehicleRoute) {

        // Only in case the station is found, add it to the vehicle route
        Station station = stationMap.get(stationCode);
        if (station != null) {
          vehicleStations.add(station);
        } else {
          DBLogger.log(Level.WARNING, "Couldn't find station number #" + stationCode);
        }
      }
    }

    return vehicleStations;
  }
}

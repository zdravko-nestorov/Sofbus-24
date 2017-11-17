package bg.znestorov.sofbus24.apidb.vehicles;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.IntStream;

import bg.znestorov.sofbus24.apidb.entity.Station;
import bg.znestorov.sofbus24.apidb.entity.Vehicle;
import bg.znestorov.sofbus24.apidb.entity.VehicleType;
import bg.znestorov.sofbus24.apidb.logger.DBLogger;
import bg.znestorov.sofbus24.apidb.utils.Utils;

import static bg.znestorov.sofbus24.apidb.utils.Constants.*;

public class RetrieveVehiclesMain {

    public static Set<Vehicle> getVehicles(Map<String, Station> stationMap) {

        Set<Vehicle> vehicleSet = new LinkedHashSet<>();

        // Retrieve the Vehicles JSON from "Sofia Traffic API v1"
        String vehicleGson = Utils.readUrl(URL_VEHICLES_CODES);
        if (StringUtils.isEmpty(vehicleGson)) {
            DBLogger.log(Level.WARNING, "Problem to reach the Vehicles URL: " + URL_VEHICLES_CODES);
            return vehicleSet;
        }

        // Convert the GSON Vehicle objects to a nested Java structure
        JsonArray vehicleJsonArray;
        try {
            vehicleJsonArray = new JsonParser().parse(vehicleGson).getAsJsonArray();
        } catch (Exception e) {
            DBLogger.log(Level.WARNING, "Problem to parse the Vehicles GSON...");
            return vehicleSet;
        }

        // Transform a Set of Stations to a Map of stations with the CODE as a key
        vehicleSet = parseVehiclesList(vehicleJsonArray, stationMap);
        vehicleSet.addAll(getMetroVehicles(stationMap));

        return vehicleSet;
    }

    @SuppressWarnings("unchecked")
    private static Set<Vehicle> parseVehiclesList(JsonArray vehicleJsonArray,
                                                  Map<String, Station> stationMap) {

        Set<Vehicle> vehiclesSet = new LinkedHashSet<>();

        vehicleJsonArray.forEach(vehicleTypeJsonElement -> {

            // Transform the JsonElement to a JsonObject, so can easily interact with its values
            JsonObject vehicleTypeJsonObject = vehicleTypeJsonElement.getAsJsonObject();

            // Retrieve the Vehicle TYPE
            VehicleType vehicleType = VehicleType.valueOf(
                    vehicleTypeJsonObject.get(VEHICLE_TYPE).getAsString().toUpperCase());

            // Iterate Vehicle information for this TYPE
            vehicleTypeJsonObject.getAsJsonArray(VEHICLE_LINES).forEach(vehicleLinesJsonElement -> {

                // Transform the JsonElement to a JsonObject, so can easily interact with its values
                JsonObject vehicleLinesJsonObject = vehicleLinesJsonElement.getAsJsonObject();

                // Set the Vehicle ID, NAME and TYPE
                Vehicle vehicle = new Vehicle();
                vehicle.setId(vehicleLinesJsonObject.get(VEHICLE_ID).getAsString());
                vehicle.setName(vehicleLinesJsonObject.get(VEHICLE_NAME).getAsString());
                vehicle.setType(vehicleType);

                JsonArray vehicleStationJsonArray = vehicleLinesJsonObject.getAsJsonArray(VEHICLE_ROUTES);
                String[][] vehicleRoutes = new String[vehicleStationJsonArray.size()][];

                // Iterate Vehicle routes for this NUMBER and TYPE
                IntStream.range(0, vehicleStationJsonArray.size()).forEach(i -> {

                    // Transform the JsonElement to a JsonObject
                    JsonArray vehicleStationCodesJsonArray = vehicleStationJsonArray
                            .get(i).getAsJsonObject()               // Get the current route (1, 2, ...)
                            .get(VEHICLE_CODE).getAsJsonArray();    // Get the list of Station codes

                    // Iterate Vehicle routes station codes for this NUMBER and TYPE
                    vehicleRoutes[i] = new String[vehicleStationCodesJsonArray.size()];
                    IntStream.range(0, vehicleStationCodesJsonArray.size()).forEach(j ->
                            vehicleRoutes[i][j] = vehicleStationCodesJsonArray.get(j).getAsString()
                    );
                });

                // Set the Vehicle DIRECTION and ROUTES
                vehicle.setRoutes(getVehicleRoutes(stationMap, vehicleRoutes));
                vehicle.setDirection();

                // Add the vehicle to the Vehicle list
                vehiclesSet.add(vehicle);
            });
        });

        return vehiclesSet;
    }

    private static Set<Vehicle> getMetroVehicles(Map<String, Station> stationMap) {

        Set<Vehicle> vehiclesSet = new LinkedHashSet<>();
        vehiclesSet.add(new Vehicle(VehicleType.METRO1, VEHICLE_METRO1_ID, VEHICLE_METRO1_NAME,
                VEHICLE_METRO1_DIRECTION, getVehicleRoutes(stationMap, VEHICLE_METRO1_ROUTES)));
        vehiclesSet.add(new Vehicle(VehicleType.METRO2, VEHICLE_METRO2_ID, VEHICLE_METRO2_NAME,
                VEHICLE_METRO2_DIRECTION, getVehicleRoutes(stationMap, VEHICLE_METRO2_ROUTES)));

        return vehiclesSet;
    }

    private static Map<Integer, List<Station>> getVehicleRoutes(Map<String, Station> stationMap,
                                                                String[][] vehicleRoutes) {

        Map<Integer, List<Station>> vehicleMetro1Routes = new LinkedHashMap<>();

        // Create the routes only in case the ROUTES multi-dimensional array is not
        // empty (passed as a constant)
        if (ArrayUtils.isNotEmpty(vehicleRoutes)) {
            for (int i = 0; i < vehicleRoutes.length; i++) {
                vehicleMetro1Routes.put((i + 1), getVehicleRoute(stationMap, vehicleRoutes[i]));
            }
        }

        return vehicleMetro1Routes;
    }

    private static List<Station> getVehicleRoute(Map<String, Station> stationMap,
                                                 String[] vehicleRoute) {

        List<Station> vehicleMetroStations = new ArrayList<>();

        // Create the route (fill it with Station objects) only in case the ROUTES
        // multi-dimensional array is not empty (passed as a constant)
        if (ArrayUtils.isNotEmpty(vehicleRoute)) {
            for (String stationCode : vehicleRoute) {
                vehicleMetroStations.add(stationMap.get(stationCode));
            }
        }

        return vehicleMetroStations;
    }

}
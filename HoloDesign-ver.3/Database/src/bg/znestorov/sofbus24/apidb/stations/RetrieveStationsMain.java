package bg.znestorov.sofbus24.apidb.stations;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;

import bg.znestorov.sofbus24.apidb.entity.Station;
import bg.znestorov.sofbus24.apidb.logger.DBLogger;
import bg.znestorov.sofbus24.apidb.utils.Utils;

import static bg.znestorov.sofbus24.apidb.utils.Constants.STATION_SKGT_ID_DEFAULT;
import static bg.znestorov.sofbus24.apidb.utils.Constants.URL_STATIONS_CODES;
import static bg.znestorov.sofbus24.apidb.utils.Constants.URL_STATIONS_SKGT_IDS;

public class RetrieveStationsMain {

    public static Map<String, Station> getStations() {

        Map<String, Station> stationMap = new LinkedHashMap<>();

        // Retrieve the Stations JSON from "Sofia Traffic API v1"
        String stationsGson = Utils.readUrl(URL_STATIONS_CODES);
        if (StringUtils.isEmpty(stationsGson)) {
            DBLogger.log(Level.WARNING, "Problem to reach the Stations URL: " + URL_STATIONS_CODES);
            return stationMap;
        }

        // Convert the GSON Station objects to a set of Stations
        Type listType = new TypeToken<LinkedHashSet<Station>>() {
        }.getType();
        Set<Station> stationSet = new Gson().fromJson(stationsGson, listType);
        if (CollectionUtils.isEmpty(stationSet)) {
            DBLogger.log(Level.WARNING, "Problem to parse the Stations GSON...");
            return stationMap;
        }

        // Retrieve the unique station SKGT IDs
        for (Station station : stationSet) {
            enrichStationSkgtId(station);
        }

        // Order the stations by CODE
        List<Station> stationList = new ArrayList<>(stationSet);
        stationList.sort(Comparator.comparing(Station::getCode));

        // Transform a Set of Stations to a LinkedHashMap of stations with the CODE as a key
        return stationList.stream().collect(Collectors.toMap(Station::getCode, Function.identity(),
                (u, v) -> {
                    throw new IllegalStateException(String.format("Duplicate key %s", u));
                }, LinkedHashMap::new));
    }

    private static void enrichStationSkgtId(Station station) {
        // Search for the current station via the new SKGT API
        String searchResultUrl = String.format(URL_STATIONS_SKGT_IDS, station.getCode());
        String searchResultGson = Utils.readUrl(searchResultUrl);
        if (StringUtils.isEmpty(searchResultGson)) {
            DBLogger.log(Level.WARNING, "Problem to reach the search URL: " + searchResultUrl);
            station.setSkgtId(STATION_SKGT_ID_DEFAULT);
            return;
        }

        // Check if the search result is empty
        JsonArray searchResultGsonArr = new Gson().fromJson(searchResultGson, JsonArray.class);
        if (searchResultGsonArr == null || searchResultGsonArr.size() <= 0) {
            DBLogger.log(Level.WARNING, "Empty result from search URL: " + searchResultUrl);
            station.setSkgtId(STATION_SKGT_ID_DEFAULT);
            return;
        }

        // Iterate all the search results and check if the current station is a stop with the same code
        for (int i = 0; i < searchResultGsonArr.size(); i++) {
            JsonObject stationGsonObj = searchResultGsonArr.get(i).getAsJsonObject();

            // Check if the current result is a stop and if the code is the same
            boolean isStop = stationGsonObj.get("is_stop").getAsBoolean();
            String code = stationGsonObj.get("code").getAsString();
            String skgtId = stationGsonObj.get("id").getAsString();

            // If the current station is a stop with the same code, set the SKGT ID
            if (isStop && Utils.equalsStringNumbers(station.getCode(), code)) {
                station.setSkgtId(skgtId);
                break;
            }
        }

        // If the SKGT ID is still empty, set the default value
        if (station.getSkgtId() == null) {
            DBLogger.log(Level.WARNING, "Non-matching result from search URL: " + searchResultUrl);
            station.setSkgtId(STATION_SKGT_ID_DEFAULT);
        }
    }
}

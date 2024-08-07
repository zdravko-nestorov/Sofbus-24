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

        // Retrieve the unique station SKGT IDs (deprecated code required because of the SKGT changes)
        for (Station station : stationSet) {
            station.setSkgtId(station.getId());
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
}

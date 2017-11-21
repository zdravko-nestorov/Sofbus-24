package bg.znestorov.sofbus24.apidb.stations;

import com.google.gson.Gson;
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

import static bg.znestorov.sofbus24.apidb.utils.Constants.URL_STATIONS_CODES;

public class RetrieveStationsMain {

    public static Map<String, Station> getStations() {

        Map<String, Station> stationMap = new LinkedHashMap<>();

        // Retrieve the Stations JSON from "Sofia Traffic API v1"
        String stationGson = Utils.readUrl(URL_STATIONS_CODES);
        if (StringUtils.isEmpty(stationGson)) {
            DBLogger.log(Level.WARNING, "Problem to reach the Stations URL: " + URL_STATIONS_CODES);
            return stationMap;
        }

        // Convert the GSON Station objects to a set of Stations
        Type listType = new TypeToken<LinkedHashSet<Station>>() {
        }.getType();
        Set<Station> stationSet = new Gson().fromJson(stationGson, listType);
        if (CollectionUtils.isEmpty(stationSet)) {
            DBLogger.log(Level.WARNING, "Problem to parse the Stations GSON...");
            return stationMap;
        }

        // Make all Station names in Camel Case format
        stationSet.forEach(station -> {
            if (!station.isMetro()) {
                station.setPublicName(Utils.toCamelCase(station.getPublicName()));
                station.setPublicNameEN(Utils.toCamelCase(station.getPublicNameEN()));
            }
        });

        List<Station> stationList = new ArrayList<>(stationSet);
        stationList.sort(Comparator.comparing(Station::getCode));

        // Transform a Set of Stations to a LinkedHashMap of stations with the CODE as a key
        return stationList.stream().collect(Collectors.toMap(Station::getCode, Function.identity(),
                (u, v) -> {
                    throw new IllegalStateException(String.format("Duplicate key %s", u));
                }, LinkedHashMap::new));
    }

}
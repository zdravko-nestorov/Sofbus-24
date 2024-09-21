package bg.znestorov.sofbus24.apidb.stations;

import static bg.znestorov.sofbus24.apidb.utils.Constants.PT_PROPERTIES_STOPS;

import bg.znestorov.sofbus24.apidb.entity.Station;
import bg.znestorov.sofbus24.apidb.logger.DBLogger;
import bg.znestorov.sofbus24.apidb.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class RetrieveStationsMain {

  public static Map<String, Station> getStations() {

    Map<String, Station> stationMap = new LinkedHashMap<>();

    // Retrieve the Stations JSON from "Sofia Traffic API"
    String stationsGson = Utils.readPublicTransportUrl(PT_PROPERTIES_STOPS);

    // Convert the GSON Station objects to a set of Stations
    Type listType = new TypeToken<LinkedHashSet<Station>>() {
    }.getType();
    Set<Station> stationSet = new Gson().fromJson(stationsGson, listType);
    if (CollectionUtils.isEmpty(stationSet)) {
      DBLogger.log(Level.WARNING, "Problem to parse the Stations GSON...");
      return stationMap;
    }

    // Order the stations by CODE
    List<Station> stationList = new ArrayList<>(stationSet);
    Collections.sort(stationList);

    // Transform a Set of Stations to a LinkedHashMap of stations with the CODE as a key
    return stationList.stream().collect(Collectors.toMap(Station::getCode, Function.identity(),
        (u, v) -> {
          throw new IllegalStateException(String.format("Duplicate key %s", u));
        }, LinkedHashMap::new));
  }
}

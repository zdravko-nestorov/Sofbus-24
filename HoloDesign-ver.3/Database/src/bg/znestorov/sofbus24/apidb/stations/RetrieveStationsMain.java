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
        addNewMetroStations(stationSet);
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

    private static void addNewMetroStations(Set<Station> stationSet) {
        Station station3329 = new Station();
        station3329.setId("3329");
        station3329.setCode("3329");
        station3329.setLon("23.270897590023196");
        station3329.setLat("42.682571555100495");
        station3329.setPublicName("Ã≈“–Œ—“¿Õ÷»ﬂ Œ¬◊¿  ”œ≈À");
        station3329.setPublicNameEN("OVCHA KUPEL METRO STATION");
        station3329.setMetro(true);
        stationSet.add(station3329);

        Station station3330 = new Station();
        station3330.setId("3330");
        station3330.setCode("3330");
        station3330.setLon("23.270897590023196");
        station3330.setLat("42.682571555100495");
        station3330.setPublicName("Ã≈“–Œ—“¿Õ÷»ﬂ Œ¬◊¿  ”œ≈À");
        station3330.setPublicNameEN("OVCHA KUPEL METRO STATION");
        station3330.setMetro(true);
        stationSet.add(station3330);

        Station station3331 = new Station();
        station3331.setId("3331");
        station3331.setCode("3331");
        station3331.setLon("23.255750439782666");
        station3331.setLat("42.683835440878035");
        station3331.setPublicName("Ã≈“–Œ—“¿Õ÷»ﬂ Ã»«»ﬂ/Õ¡”");
        station3331.setPublicNameEN("MIZIA/NBU METRO STATION");
        station3331.setMetro(true);
        stationSet.add(station3331);

        Station station3332 = new Station();
        station3332.setId("3332");
        station3332.setCode("3332");
        station3332.setLon("23.255750439782666");
        station3332.setLat("42.683835440878035");
        station3332.setPublicName("Ã≈“–Œ—“¿Õ÷»ﬂ Ã»«»ﬂ/Õ¡”");
        station3332.setPublicNameEN("MIZIA/NBU METRO STATION");
        station3332.setMetro(true);
        stationSet.add(station3332);

        Station station3333 = new Station();
        station3333.setId("3333");
        station3333.setCode("3333");
        station3333.setLon("23.248137599970402");
        station3333.setLat("42.68451964575825");
        station3333.setPublicName("Ã≈“–Œ—“¿Õ÷»ﬂ Œ¬◊¿  ”œ≈À II");
        station3333.setPublicNameEN("OVCHA KUPEL II METRO STATION");
        station3333.setMetro(true);
        stationSet.add(station3333);

        Station station3334 = new Station();
        station3334.setId("3334");
        station3334.setCode("3334");
        station3334.setLon("23.248137599970402");
        station3334.setLat("42.68451964575825");
        station3334.setPublicName("Ã≈“–Œ—“¿Õ÷»ﬂ Œ¬◊¿  ”œ≈À II");
        station3334.setPublicNameEN("OVCHA KUPEL II METRO STATION");
        station3334.setMetro(true);
        stationSet.add(station3334);

        Station station3335 = new Station();
        station3335.setId("3335");
        station3335.setCode("3335");
        station3335.setLon("23.24114459130291");
        station3335.setLat("42.682938639529596");
        station3335.setPublicName("Ã≈“–Œ—“¿Õ÷»ﬂ √Œ–Õ¿ ¡¿Õﬂ");
        station3335.setPublicNameEN("GORNA BANYA METRO STATION");
        station3335.setMetro(true);
        stationSet.add(station3335);

        Station station3336 = new Station();
        station3336.setId("3336");
        station3336.setCode("3336");
        station3336.setLon("23.24114459130291");
        station3336.setLat("42.682938639529596");
        station3336.setPublicName("Ã≈“–Œ—“¿Õ÷»ﬂ √Œ–Õ¿ ¡¿Õﬂ");
        station3336.setPublicNameEN("GORNA BANYA METRO STATION");
        station3336.setMetro(true);
        stationSet.add(station3336);
    }
}
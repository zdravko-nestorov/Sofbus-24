package bg.znestorov.sofbus24.apidb.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.IntStream;

import bg.znestorov.sofbus24.apidb.entity.Station;

import static bg.znestorov.sofbus24.apidb.logger.DBLogger.logDuration;
import static bg.znestorov.sofbus24.apidb.logger.DBLogger.logSevere;
import static bg.znestorov.sofbus24.apidb.utils.Constants.*;
import static bg.znestorov.sofbus24.apidb.utils.UtilsDuration.getTime;

public class Utils {

    public static boolean copyEmptyDatabase() {
        try {
            FileUtils.deleteQuietly(DB_CURRENT_FULL_FILE);
            FileUtils.deleteQuietly(DB_CURRENT_JOURNAL_FULL_FILE);
            FileUtils.copyFile(DB_ORIGINAL_EMPTY_FILE, DB_CURRENT_FULL_FILE);
            return true;
        } catch (IOException e) {
            logSevere("Copying of the DB was not successful - " + e.getClass().getName() + ": " + e.getMessage());
            return false;
        }
    }

    public static void backupDatabase() {
        try {
            FileUtils.deleteQuietly(DB_BACKUP_FULL_FILE);
            FileUtils.copyFile(DB_CURRENT_FULL_FILE, DB_BACKUP_FULL_FILE);

            FileUtils.deleteQuietly(DB_INFORMATION_BACKUP_FILE);
            FileUtils.copyFile(DB_INFORMATION_FILE, DB_INFORMATION_BACKUP_FILE);
        } catch (IOException e) {
            logSevere("Copying of the DB was not successful - " + e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public static String readUrl(String urlString) {

        Long startTime = getTime();

        // Create a new scanner to download the URL content
        try (Scanner scanner = new Scanner(new URL(urlString).openStream(), "UTF-8")) {

            // The regular expression "\\A" matches the beginning of input. This tells Scanner
            // to tokenize the entire stream, from beginning to (illogical) next beginning
            return scanner.useDelimiter("\\A").next();
        } catch (Exception e) {
            return null;
        } finally {
            logDuration("The content for URL - " + urlString + ", is retrieved for ", startTime);
        }
    }

    public static String toCamelCase(String input) {
        return WordUtils.capitalizeFully(input, ' ').replace('³', '²');
    }

    public static String formDirection(Map<Integer, List<Station>> routes) {

        String direction = null;

        // For the direction name using the first and last station of the first route
        if (!MapUtils.isEmpty(routes) && !CollectionUtils.isEmpty(routes.get(1))) {
            List<Station> firstRoute = routes.get(1);
            direction = firstRoute.get(0).getPublicName() + " - "
                    + firstRoute.get(firstRoute.size() - 1).getPublicName();
        }

        return direction;
    }

    public static Set<String> transformMultiDimArrIntoSet(String[][] multiDimArr) {

        Set<String> set = new LinkedHashSet<>();

        // Check if we have any routes available
        if (ArrayUtils.isNotEmpty(multiDimArr)) {
            IntStream.range(0, multiDimArr.length).forEach(i -> {

                // Check if we have any stations for the current route
                if (ArrayUtils.isNotEmpty(multiDimArr[i])) {
                    IntStream.range(0, multiDimArr[i].length).forEach(j ->
                            set.add(multiDimArr[i][j]));
                }
            });
        }

        return set;
    }

}
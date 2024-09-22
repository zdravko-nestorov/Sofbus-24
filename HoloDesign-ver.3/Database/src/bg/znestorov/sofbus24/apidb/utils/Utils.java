package bg.znestorov.sofbus24.apidb.utils;

import static bg.znestorov.sofbus24.apidb.logger.DBLogger.logDuration;
import static bg.znestorov.sofbus24.apidb.logger.DBLogger.logSevere;
import static bg.znestorov.sofbus24.apidb.utils.Constants.DB_BACKUP_FULL_FILE;
import static bg.znestorov.sofbus24.apidb.utils.Constants.DB_CONFIG_FULL_FILE;
import static bg.znestorov.sofbus24.apidb.utils.Constants.DB_CURRENT_FULL_FILE;
import static bg.znestorov.sofbus24.apidb.utils.Constants.DB_CURRENT_JOURNAL_FULL_FILE;
import static bg.znestorov.sofbus24.apidb.utils.Constants.DB_INFORMATION_BACKUP_FILE;
import static bg.znestorov.sofbus24.apidb.utils.Constants.DB_INFORMATION_FILE;
import static bg.znestorov.sofbus24.apidb.utils.Constants.DB_ORIGINAL_EMPTY_FILE;
import static bg.znestorov.sofbus24.apidb.utils.Constants.PT_PROPERTIES;
import static bg.znestorov.sofbus24.apidb.utils.Constants.PT_ROUTES;
import static bg.znestorov.sofbus24.apidb.utils.Constants.URL_PUBLIC_TRANSPORT_API;
import static bg.znestorov.sofbus24.apidb.utils.Constants.URL_SCHEDULE_API;
import static bg.znestorov.sofbus24.apidb.utils.Constants.URL_SCHEDULE_API_SOFIA_TRAFFIC_SESSION;
import static bg.znestorov.sofbus24.apidb.utils.Constants.URL_SCHEDULE_API_XSRF_TOKEN;
import static bg.znestorov.sofbus24.apidb.utils.UtilsDuration.getTime;

import bg.znestorov.sofbus24.apidb.entity.Station;
import bg.znestorov.sofbus24.apidb.entity.Vehicle;
import bg.znestorov.sofbus24.apidb.entity.VehicleRoute;
import bg.znestorov.sofbus24.apidb.logger.DBLogger;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

      FileUtils.deleteQuietly(DB_CONFIG_FULL_FILE);
      FileUtils.copyFile(DB_CURRENT_FULL_FILE, DB_CONFIG_FULL_FILE);

      FileUtils.deleteQuietly(DB_INFORMATION_BACKUP_FILE);
      FileUtils.copyFile(DB_INFORMATION_FILE, DB_INFORMATION_BACKUP_FILE);

    } catch (IOException e) {
      logSevere("Copying of the DB was not successful - " + e.getClass().getName() + ": " + e.getMessage());
    }
  }

  public static String readPublicTransportUrl(String propertyKey) {

    long startTime = getTime();
    String url = URL_PUBLIC_TRANSPORT_API;

    // Create a new scanner to download the URL content
    try (Scanner scanner = new Scanner(openPublicTransportUrlConnection(url).getInputStream(), "UTF-8")) {
      // Format the URL content
      String content = formatUrlContent(scanner);

      // Extract the public transport data from the content
      Pattern pattern = Pattern.compile(".*<div id=\"app\" data-page=\"(.*)\"></div>.*");
      Matcher matcher = pattern.matcher(content);
      content = matcher.matches() ? matcher.group(1) : null;

      // Return the property value
      return new Gson().fromJson(content, JsonObject.class).get(PT_PROPERTIES).getAsJsonObject().get(propertyKey)
          .getAsJsonArray().toString();

    } catch (Exception e) {
      DBLogger.log(Level.WARNING, "Problem to reach the URL: " + url + "/" + propertyKey);
      return null;

    } finally {
      logDuration("The content for URL: " + url + "/" + propertyKey + ", is retrieved for ", startTime);
    }
  }

  public static HttpURLConnection openPublicTransportUrlConnection(String url) throws Exception {
    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
    connection.setRequestMethod("GET");

    return connection;
  }

  public static String readScheduleUrl(Vehicle vehicle) {

    long startTime = getTime();
    String url = URL_SCHEDULE_API;

    // Create a new scanner to download the URL content
    try (Scanner scanner = new Scanner(openScheduleUrlConnection(url, vehicle).getInputStream(), "UTF-8")) {
      // Format the URL content
      String content = formatUrlContent(scanner);

      // Return the property value
      return new Gson().fromJson(content, JsonObject.class).get(PT_ROUTES).getAsJsonArray().toString();

    } catch (Exception e) {
      DBLogger.log(Level.WARNING, "Problem to reach the URL: " + url + "/" + vehicle.getExtId());
      return null;

    } finally {
      logDuration("The content for URL: " + url + "/" + vehicle.getExtId() + ", is retrieved for ", startTime);
    }
  }

  public static HttpURLConnection openScheduleUrlConnection(String url, Vehicle vehicle) throws Exception {
    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
    connection.setRequestMethod("POST");
    connection.setRequestProperty("content-type", "application/json");

    String cookie = String.format("XSRF-TOKEN=%s; sofia_traffic_session=%s", URL_SCHEDULE_API_XSRF_TOKEN,
        URL_SCHEDULE_API_SOFIA_TRAFFIC_SESSION);
    connection.setRequestProperty("cookie", cookie);

    String xsrfToken =
        String.format("%s", URLDecoder.decode(URL_SCHEDULE_API_XSRF_TOKEN, StandardCharsets.UTF_8.name()));
    connection.setRequestProperty("x-xsrf-token", xsrfToken);

    // Writing the data to the output stream
    connection.setDoOutput(true);
    String requestBody = String.format("{\"ext_id\":\"%s\"}", vehicle.getExtId());
    try (OutputStream os = connection.getOutputStream()) {
      byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
      os.write(input, 0, input.length);
    }

    return connection;
  }

  public static String formatUrlContent(Scanner scanner) {
    // The regular expression "\\A" matches the beginning of input. This tells Scanner
    // to tokenize the entire stream, from beginning to (illogical) next beginning
    String content = scanner.useDelimiter("\\A").next();

    // Remove the new lines and carriage returns
    content = content.replace("\n", "").replace("\r", "");

    // Unescape the HTML content
    return StringEscapeUtils.unescapeHtml4(content);
  }

  public static String formDirection(Map<VehicleRoute, List<Station>> routes) {
    if (MapUtils.isEmpty(routes)) {
      return "---";
    }

    Iterator<Map.Entry<VehicleRoute, List<Station>>> vehicleRoutesIterator = routes.entrySet().iterator();
    if (!vehicleRoutesIterator.hasNext()) {
      return "---";
    }

    return vehicleRoutesIterator.next().getKey().getName();
  }

  public static String getOnlyDigits(String value) {
    if (value != null && !value.isEmpty()) {
      value = value.replaceAll("\\D+", "");
    } else {
      value = "";
    }

    return value;
  }

  public static String getOnlyChars(String value) {
    if (value != null && !value.isEmpty()) {
      value = value.replaceAll("\\d+", "");
    } else {
      value = "";
    }

    return value;
  }
}

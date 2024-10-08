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
import static bg.znestorov.sofbus24.apidb.utils.Constants.PT_COOKIES_SOFIA_TRAFFIC_SESSION;
import static bg.znestorov.sofbus24.apidb.utils.Constants.PT_COOKIES_XSRF_TOKEN;
import static bg.znestorov.sofbus24.apidb.utils.Constants.PT_PROPERTIES;
import static bg.znestorov.sofbus24.apidb.utils.Constants.PT_ROUTES;
import static bg.znestorov.sofbus24.apidb.utils.Constants.URL_PUBLIC_TRANSPORT_API;
import static bg.znestorov.sofbus24.apidb.utils.Constants.URL_SCHEDULE_API;
import static bg.znestorov.sofbus24.apidb.utils.UtilsDuration.getTime;

import bg.znestorov.sofbus24.apidb.entity.Vehicle;
import bg.znestorov.sofbus24.apidb.logger.DBLogger;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

  public static Map<String, String> SKGT_COOKIES = new HashMap<>();

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

  public static void initPublicTransportUrlCookies() {
    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(URL_PUBLIC_TRANSPORT_API).openConnection();
      List<String> cookies = connection.getHeaderFields().get("Set-Cookie");
      SKGT_COOKIES = initPublicTransportUrlCookies(cookies);

    } catch (Exception ignored) {
      SKGT_COOKIES = new HashMap<>();
    }
  }

  public static Map<String, String> initPublicTransportUrlCookies(List<String> cookies) {
    Map<String, String> cookiesMap = new java.util.HashMap<>();

    if (cookies == null) {
      return cookiesMap;
    }

    // Initialize the cookies
    for (String cookie : cookies) {
      if (StringUtils.isEmpty(cookie)) {
        continue;
      }

      // Null-safe split operation (get the first part of the cookie)
      String[] cookieSegments = cookie.split(";");
      if (ArrayUtils.isEmpty(cookieSegments) || StringUtils.isEmpty(cookieSegments[0])) {
        continue;
      }

      // Null-safe split operation (get the cookie name and value)
      String[] cookieParts = cookieSegments[0].split("=");
      if (ArrayUtils.getLength(cookieParts) != 2 || StringUtils.isEmpty(cookieParts[0])) {
        continue;
      }

      // Get the cookie name and value
      String cookieName = cookieParts[0];
      String cookieValue = cookieParts[1];

      // Add the desired cookies to the map
      switch (cookieName) {
        case PT_COOKIES_XSRF_TOKEN:
          cookiesMap.put(PT_COOKIES_XSRF_TOKEN, cookieValue);
          break;
        case PT_COOKIES_SOFIA_TRAFFIC_SESSION:
          cookiesMap.put(PT_COOKIES_SOFIA_TRAFFIC_SESSION, cookieValue);
          break;
      }
    }

    return cookiesMap;
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
    String xsrfToken = SKGT_COOKIES.get(PT_COOKIES_XSRF_TOKEN);
    String sofiaTrafficSession = SKGT_COOKIES.get(PT_COOKIES_SOFIA_TRAFFIC_SESSION);

    // Open the connection
    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
    connection.setRequestMethod("POST");
    connection.setRequestProperty("content-type", "application/json");

    // Set the cookies
    String cookie = String.format("XSRF-TOKEN=%s; sofia_traffic_session=%s", xsrfToken, sofiaTrafficSession);
    connection.setRequestProperty("cookie", cookie);

    // Set the x-xsrf-token
    xsrfToken = String.format("%s", URLDecoder.decode(xsrfToken, StandardCharsets.UTF_8.name()));
    connection.setRequestProperty("x-xsrf-token", xsrfToken);

    // Writing the data to the output stream
    connection.setDoOutput(true);
    String requestBody = vehicle.getType() == 3
        ? String.format("{\"ext_id\":\"%s\",\"type\":3}", vehicle.getExtId())
        : String.format("{\"ext_id\":\"%s\"}", vehicle.getExtId());

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

  public static String getOnlyDigits(String value) {
    if (value != null && !value.isEmpty()) {
      value = value.replaceAll("\\D+", "");
    } else {
      value = "";
    }

    return value;
  }
}

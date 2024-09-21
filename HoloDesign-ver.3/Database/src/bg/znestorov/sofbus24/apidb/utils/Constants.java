package bg.znestorov.sofbus24.apidb.utils;

import static bg.znestorov.sofbus24.apidb.utils.UtilsDuration.getDate;

import java.io.File;

public class Constants {

  /**
   * APPLICATION CONSTANTS
   */
  public static final String LINE_SEPARATOR = System.lineSeparator();

  /**
   * LOGGER CONSTANTS
   */
  public static final String SOFBUS_LOGGER = "STATIONS AND VEHICLES DATABASE INFORMATION";
  public static final String DB_LOG_FILE = "log/MyLogFile.log";
  // Metro stations DB information
  public static final String DB_INFORMATION_TITLE = "Database Information:";
  public static final String DB_INFORMATION_STATIONS =
      " * Stations: %s (BUS) + %s (NIGHT BUS) + %s (TROLLEYBUS) + %s (TRAM) + %s (METRO) = %s";
  public static final String DB_INFORMATION_VEHICLES =
      " * Vehicles: %s (BUS) + %s (NIGHT BUS) + %s (TROLLEYBUS) + %s (TRAM) + %s (METRO) = %s";
  public static final String DB_INFORMATION_STATIONS_VEHICLES = " * Stations/Vehicles - %s";
  public static final File DB_METRO_INFO_FILE = new File("../MetroSchedule/properties/metro_coordinates.properties");
  /**
   * PUBLIC TRANSPORTATION API CONSTANTS
   */
  public static final String URL_PUBLIC_TRANSPORT_API = "https://www.sofiatraffic.bg/bg/public-transport";
  // JSON codes (properties)
  public static final String PT_PROPERTIES = "props";
  public static final String PT_PROPERTIES_STOPS = "stops";
  public static final String PT_PROPERTIES_LINES = "lines";
  /**
   * SCHEDULE API CONSTANTS
   */
  public static final String URL_SCHEDULE_API = "https://www.sofiatraffic.bg/bg/trip/getSchedule";
  public static final String URL_SCHEDULE_API_XSRF_TOKEN =
      "eyJpdiI6IlBZRGJaSHplZnhJd05tWllmTEd2Qnc9PSIsInZhbHVlIjoiZGllTy9VRzZjZGxPQ0JERitvNWllOGFNc0JVQkduQjBxbVZtd0tMeDFFakZpcTlwN3VyZitIK3ZyT0NZVlR2OHkvejZmUFNBK0xCTG1abnV3QkYzM3NTSGdaT1FKbmJOc2o0bDQ1ZW5PRXQ5TFdkUDVELys5TGpxVUpKUkVxWFciLCJtYWMiOiJiYmI2ZWE1NjEyMmE3YmE0MWQxYmQxZDNmZjcyOGE4MDNhMWY0NjQyNzgxYTdhMzNhY2U2ZWUwNDhmZDc4YWI0IiwidGFnIjoiIn0%3D";
  public static final String URL_SCHEDULE_API_SOFIA_TRAFFIC_SESSION =
      "eyJpdiI6Ik5yM1VHV0VGb3lpMnE1a3VpZklaSGc9PSIsInZhbHVlIjoiU0NKbHNGTTF3KzFXMmkzeHVscnRIeG1JTko3SDcwN0U3Q0xiK0JhQW1tejlBYzErd3QrRlU0bUVENmh6dW9QQmY0eXNBTkhoT1Zsb2w1RStheTd5UUNmaUZ3NmZJRGtYVmx2Z1RMS0ludElPdmZEY3BLNERUWHQ5Y0dmYWs4dGkiLCJtYWMiOiI0NzdhZDFlNTJkZGEyZTRhMWYwNGRmZjIwMmJiOTUyNGY3NmFiYjJmYTA4YTVmYzZlOTdhMWY4NjE0YTcxYWZmIiwidGFnIjoiIn0%3D";
  // JSON codes (routes)
  public static final String PT_ROUTES = "routes";
  public static final String PT_ROUTES_SEGMENTS = "segments";
  public static final String PT_ROUTES_SEGMENTS_STOP = "stop";
  public static final String PT_ROUTES_SEGMENTS_STOP_CODE = "code";
  static final File DB_CONFIG_FULL_FILE = new File("../ConfigData/Databases/sofbus24.db");
  /**
   * DATABASE CONSTANTS
   */
  private static final String DB_PATH = "database/api/";
  static final File DB_ORIGINAL_EMPTY_FILE = new File(DB_PATH + "sofbus24-empty.db");
  static final File DB_CURRENT_FULL_FILE = new File(DB_PATH + "sofbus24.db");
  static final File DB_CURRENT_JOURNAL_FULL_FILE = new File(DB_PATH + "sofbus24.db-journal");
  private static final String DB_BACKUP_PATH = DB_PATH + "backups/";
  static final File DB_BACKUP_FULL_FILE = new File(DB_BACKUP_PATH + "sofbus24-" + getDate() + ".db");
  // Full DB information
  private static final String DB_INFO_CURRENT_PATH = "info/api/";
  public static final File DB_INFORMATION_FILE = new File(DB_INFO_CURRENT_PATH + "Information.txt");
  private static final String DB_INFO_BACKUP_PATH = DB_INFO_CURRENT_PATH + "backups/";
  public static final File DB_INFORMATION_BACKUP_FILE =
      new File(DB_INFO_BACKUP_PATH + "Information-" + getDate() + ".txt");
}

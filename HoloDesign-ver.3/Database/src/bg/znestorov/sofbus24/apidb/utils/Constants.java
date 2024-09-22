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
      "eyJpdiI6Inp1YjV3RUxxL0dvV2JYeENJaEtLckE9PSIsInZhbHVlIjoiUTFXVTMyK3JHVEx1Tkk3ZVFWL2pJM2ZxZFBOYXJ4WTlkeGgvVEVWckxLY3pkNEE0ZEhHeTAxblpJNlNScC9rSjJBYWY3c2dXUVVicjdobHFjSXlXTnBxQk1EVExCL3gxQXRlWGF4MXgwM2pVcmlGQTVWZi8vVjBlMzFsejk1bkIiLCJtYWMiOiI2ZjQxYzAzNTg3ZWI3YmExMzRjMGQ0ZDVkODE3NmMyZWQyOWVmNGJiZTA0ZGMwMGU2MDA5NTUwM2IwNjJkNzdkIiwidGFnIjoiIn0%3D";
  public static final String URL_SCHEDULE_API_SOFIA_TRAFFIC_SESSION =
      "eyJpdiI6IkJNMXR2b1F2OXg0KzZHekxyanlUN3c9PSIsInZhbHVlIjoib3cyYnpDU3ZTUE5rUWNUbGY2cHZoZm5oODdhTE5yOHFaazhzOHZjNER4S3hEaFZFeWRSa2txKzVLcUVBdVIrWnVjdTNZOUdzNy9uRGpHc01xVWJCdC9yQTBQNUZmcGlJN3dySFY1WFhjZGZnY0l6cjVHcFgxWUFlb2pBQjJsZkYiLCJtYWMiOiJiNzIyYjY4MjcxODU3ZmJkNjQ0YWI4M2ViODRjNTlmMWE0ZDQwM2U4NDcwNDZiMTdiNmVkOWVkYjcxMzdmZjY4IiwidGFnIjoiIn0%3D";
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

package bg.znestorov.sofbus24.apidb.entity;

import static bg.znestorov.sofbus24.apidb.logger.DBLogger.logSevere;
import static bg.znestorov.sofbus24.apidb.utils.Constants.DB_INFORMATION_FILE;
import static bg.znestorov.sofbus24.apidb.utils.Constants.DB_INFORMATION_STATIONS;
import static bg.znestorov.sofbus24.apidb.utils.Constants.DB_INFORMATION_STATIONS_VEHICLES;
import static bg.znestorov.sofbus24.apidb.utils.Constants.DB_INFORMATION_TITLE;
import static bg.znestorov.sofbus24.apidb.utils.Constants.DB_INFORMATION_VEHICLES;
import static bg.znestorov.sofbus24.apidb.utils.Constants.LINE_SEPARATOR;
import static bg.znestorov.sofbus24.apidb.utils.UtilsDuration.getFullDate;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DatabaseInfo {

  private static final DatabaseInfo DATABASE_INFO = new DatabaseInfo();
  private StringBuilder info;

  private DatabaseInfo() {
    info = new StringBuilder(getFullDate());
    info.append(LINE_SEPARATOR)
        .append(LINE_SEPARATOR)
        .append(DB_INFORMATION_TITLE)
        .append(LINE_SEPARATOR);
  }

  public static DatabaseInfo getInstance() {
    return DATABASE_INFO;
  }

  public void appendStationsInfo(Object... values) {
    info.append(formatInformation(DB_INFORMATION_STATIONS, values));
  }

  public void appendVehiclesInfo(Object... values) {
    info.append(formatInformation(DB_INFORMATION_VEHICLES, values));
  }

  public void appendVehiclesStationsInfo(Object... values) {
    info.append(formatInformation(DB_INFORMATION_STATIONS_VEHICLES, values));
  }

  private String formatInformation(String info, Object... values) {
    return String.format(info, values) + LINE_SEPARATOR;
  }

  public StringBuilder getInfo() {
    return info;
  }

  public void writeInformation() {
    try {
      FileUtils.deleteQuietly(DB_INFORMATION_FILE);
      Files.write(Paths.get(DB_INFORMATION_FILE.getPath()), info.toString().getBytes());
    } catch (IOException e) {
      logSevere("There is a problem writing the DB information - " + e.getClass().getName() + ": " + e.getMessage());
    }
  }

  @Override
  public String toString() {
    return "DatabaseInfo{" +
        "info=" + info +
        '}';
  }

}

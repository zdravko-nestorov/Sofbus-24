package bg.znestorov.sofbus24.apidb.entity;

import static bg.znestorov.sofbus24.apidb.logger.DBLogger.logSevere;
import static bg.znestorov.sofbus24.apidb.utils.Constants.DB_METRO_INFO_FILE;
import static bg.znestorov.sofbus24.apidb.utils.Constants.LINE_SEPARATOR;
import static bg.znestorov.sofbus24.apidb.utils.UtilsDuration.getFullDate;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MetroStationsInfo {

  private static final MetroStationsInfo METRO_STATIONS_INFO = new MetroStationsInfo();
  private StringBuilder info;

  private MetroStationsInfo() {
    info = new StringBuilder()
        .append("# ")
        .append(getFullDate())
        .append(LINE_SEPARATOR)
        .append(LINE_SEPARATOR);
  }

  public static MetroStationsInfo getInstance() {
    return METRO_STATIONS_INFO;
  }

  public void appendMetroStationsInfo(String metroStationsProps) {
    info.append(metroStationsProps);
  }

  public StringBuilder getInfo() {
    return info;
  }

  public void writeInformation() {
    try {
      FileUtils.deleteQuietly(DB_METRO_INFO_FILE);
      Files.write(Paths.get(DB_METRO_INFO_FILE.getPath()), info.toString().getBytes());
    } catch (IOException e) {
      logSevere("There is a problem writing the Metro Stations information - " + e.getClass().getName() + ": " +
          e.getMessage());
    }
  }

  @Override
  public String toString() {
    return "DatabaseInfo{" +
        "info=" + info +
        '}';
  }

}

package bg.znestorov.sofbus24.apidb.main;

import static bg.znestorov.sofbus24.apidb.logger.DBLogger.logDuration;
import static bg.znestorov.sofbus24.apidb.logger.DBLogger.logInfo;
import static bg.znestorov.sofbus24.apidb.utils.UtilsDuration.getTime;

import bg.znestorov.sofbus24.apidb.databases.SQLiteJDBC;
import bg.znestorov.sofbus24.apidb.entity.DatabaseInfo;
import bg.znestorov.sofbus24.apidb.entity.MetroStationsInfo;
import bg.znestorov.sofbus24.apidb.entity.Station;
import bg.znestorov.sofbus24.apidb.entity.Vehicle;
import bg.znestorov.sofbus24.apidb.stations.RetrieveStationsMain;
import bg.znestorov.sofbus24.apidb.utils.Utils;
import bg.znestorov.sofbus24.apidb.vehicles.RetrieveVehiclesMain;

import java.util.Map;
import java.util.Set;

public class RetrieveDatabaseInfoMain {

  public static void main(String[] args) {

    long globalStart = getTime();

    long startTime = getTime();
    logInfo("*** COPY EMPTY DATABASE ***");
    if (!Utils.copyEmptyDatabase()) {
      return;
    }
    logDuration("Copying of the empty database took ", startTime);

    startTime = getTime();
    logInfo("-----------------------\n");
    logInfo("*** RETRIEVE SKGT COOKIES ***");
    Utils.initPublicTransportUrlCookies();
    logDuration("The SKGT COOKIES are retrieved and parsed for ", startTime);

    startTime = getTime();
    logInfo("-----------------------\n");
    logInfo("*** RETRIEVE STATIONS ***");
    Map<String, Station> stationMap = RetrieveStationsMain.getStations();
    logDuration("The STATIONS are retrieved and parsed for ", startTime);

    startTime = getTime();
    logInfo("-----------------------\n");
    logInfo("*** RETRIEVE VEHICLES ***");
    Set<Vehicle> vehicleSet = RetrieveVehiclesMain.getVehicles(stationMap);
    logDuration("The VEHICLES are retrieved and parsed for ", startTime);

    startTime = getTime();
    logInfo("-----------------------\n");
    logInfo("*** INSERT INTO DATABASE ***");
    SQLiteJDBC sqLiteJDBC = new SQLiteJDBC(stationMap, vehicleSet);
    sqLiteJDBC.initDatabase();
    logDuration("The DATABASE is populated with data for ", startTime);

    startTime = getTime();
    logInfo("-----------------------\n");
    logInfo("*** WRITE DATABASE INFORMATION TO A FILE ***");
    DatabaseInfo.getInstance().writeInformation();
    logDuration("Write the database information took ", startTime);

    startTime = getTime();
    logInfo("-----------------------\n");
    logInfo("*** WRITE METRO STATIONS COORDINATES TO A FILE (METRO SCHEDULE PROJECT) ***");
    MetroStationsInfo.getInstance().writeInformation();
    logDuration("Write the metro stations coordinates took ", startTime);

    startTime = getTime();
    logInfo("-----------------------\n");
    logInfo("*** BACKUP FULL DATABASE ***");
    Utils.backupDatabase();
    logDuration("Backup of the full database took ", startTime);

    logInfo("-----------------------\n");
    logDuration("Database retrieval took ", globalStart);
  }

}

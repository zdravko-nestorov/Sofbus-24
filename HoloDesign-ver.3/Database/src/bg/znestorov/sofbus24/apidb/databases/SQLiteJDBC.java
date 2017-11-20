package bg.znestorov.sofbus24.apidb.databases;

import java.sql.*;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import bg.znestorov.sofbus24.apidb.entity.DatabaseInfo;
import bg.znestorov.sofbus24.apidb.entity.Station;
import bg.znestorov.sofbus24.apidb.entity.Vehicle;
import bg.znestorov.sofbus24.apidb.entity.VehicleType;
import bg.znestorov.sofbus24.db.utils.Utils;

import static bg.znestorov.sofbus24.apidb.logger.DBLogger.logDuration;
import static bg.znestorov.sofbus24.apidb.logger.DBLogger.logInfo;
import static bg.znestorov.sofbus24.apidb.logger.DBLogger.logSevere;
import static bg.znestorov.sofbus24.apidb.logger.DBLogger.logWarning;

public class SQLiteJDBC {

    private final Set<Station> stationSet;
    private final Set<Vehicle> vehicleSet;

    private Connection connection = null;
    private Statement stmt = null;

    public SQLiteJDBC(Map<String, Station> stationMap, Set<Vehicle> vehicleSet) {
        this.stationSet = new LinkedHashSet<>(stationMap.values());
        this.vehicleSet = vehicleSet;
    }

    public void initDatabase() {

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:database/api/sofbus24.db");
            connection.setAutoCommit(false);
            logInfo("Opened sofbus24.db database successfully!\n");

            stmt = connection.createStatement();

            long startTime = Utils.getTime();
            initStationsTable();
            logDuration("The 'SOF_STAT' table is initialized for ", startTime);

            startTime = Utils.getTime();
            initVehiclesTable();
            logDuration("The 'SOF_VEHI' table is initialized for ", startTime);

            startTime = Utils.getTime();
            initVehicleStationsTable();
            logDuration("The 'SOF_VEST' table is initialized for ", startTime);

        } catch (Exception e) {
            logSevere(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            try {
                stmt.close();
                connection.commit();
                connection.close();
            } catch (SQLException e) {
                logSevere(e.getClass().getName() + ": " + e.getMessage());
            }
        }
    }

    private void initStationsTable() {

        int totalStations = stationSet.size();
        AtomicInteger bttStations = new AtomicInteger(0);
        AtomicInteger metroStations = new AtomicInteger(0);
        AtomicInteger insertedStations = new AtomicInteger(0);

        stationSet.forEach(station -> {

            String stationNumber = station.getCode();
            String stationName = station.getPublicName();
            String stationLat = station.getLat();
            String stationLon = station.getLon();
            VehicleType stationType = station.getType();

            try {
                String sql = "INSERT INTO SOF_STAT (STAT_NUMBER, STAT_NAME, STAT_LATITUDE, STAT_LONGITUDE, STAT_TYPE)\n"
                        + "VALUES ('%s', '%s', '%s', '%s', '%s');";
                sql = String.format(sql, stationNumber, stationName, stationLat, stationLon, stationType);

                stmt.executeUpdate(sql);
                insertedStations.incrementAndGet();

                // Count the number of different type of stations
                if (!station.isMetro()) {
                    bttStations.incrementAndGet();
                } else {
                    metroStations.incrementAndGet();
                }

            } catch (Exception e) {
                logWarning("There was a problem inserting station - " + station.getLabel());
            }
        });

        DatabaseInfo.getInstance().appendStationsInfo(bttStations, metroStations, insertedStations);
        logInfo("Total stations (from SKGT) = " + totalStations
                + ", BTT stations (in DB) = " + bttStations
                + ", METRO stations (in DB) = " + metroStations
                + ", Inserted stations (in DB) = " + insertedStations);
    }

    private void initVehiclesTable() {

        int totalVehicles = vehicleSet.size();
        AtomicInteger insertedVehicles = new AtomicInteger(0);

        vehicleSet.forEach(vehicle -> {

            String vehicleNumber = vehicle.getName();
            VehicleType vehicleType = vehicle.getType();
            String vehicleDirection = vehicle.getDirection();

            try {
                String sql = "INSERT INTO SOF_VEHI (VEHI_NUMBER, VEHI_TYPE, VEHI_DIRECTION)\n"
                        + "VALUES ('%s', '%s', '%s');";
                sql = String.format(sql, vehicleNumber, vehicleType, vehicleDirection);

                stmt.executeUpdate(sql);
                insertedVehicles.incrementAndGet();
            } catch (Exception e) {
                logWarning("There was a problem inserting vehicle - " + vehicle.getLabel());
            }
        });

        DatabaseInfo.getInstance().appendVehiclesInfo(insertedVehicles);
        logInfo("Total vehicles (from SKGT) = " + totalVehicles
                + ", Inserted vehicles (in DB) = " + insertedVehicles);
    }

    private void initVehicleStationsTable() {

        int totalVehicleStations = getTotalVehicleStations();
        AtomicInteger insertedVehicleStations = new AtomicInteger(0);

        vehicleSet.forEach(vehicle -> {

            int vehicleId = getVehicleId(vehicle);
            vehicle.getRoutes().forEach((vehicleDirection, stations) -> stations.forEach(station -> {

                try {
                    String sql = "INSERT INTO SOF_VEST (FK_VEST_VEHI_ID, FK_VEST_STAT_ID, VEST_DIRECTION) "
                            + "VALUES (%s, %s, %s);";
                    sql = String.format(sql, vehicleId, getStationId(station), vehicleDirection);

                    stmt.executeUpdate(sql);
                    insertedVehicleStations.incrementAndGet();
                } catch (Exception e) {
                    logWarning("There was a problem inserting VehicleStation - "
                            + vehicle.getLabel() + ", " + station.getLabel());
                }
            }));
        });

        DatabaseInfo.getInstance().appendVehiclesStationsInfo(insertedVehicleStations);
        logInfo("Total VehiclesStations (from SKGT) = " + totalVehicleStations
                + ", Inserted VehiclesStations (in DB) = " + insertedVehicleStations);
    }

    private int getTotalVehicleStations() {

        AtomicInteger totalVehicleStations = new AtomicInteger(0);
        vehicleSet.forEach(vehicle -> vehicle.getRoutes().values().forEach(stations ->
                totalVehicleStations.addAndGet(stations.size())
        ));

        return totalVehicleStations.intValue();
    }

    private int getStationId(Station station) {

        int stationId = 0;

        try {
            String sql = "SELECT PK_STAT_ID FROM SOF_STAT WHERE STAT_NUMBER = '%s';";
            sql = String.format(sql, station.getCode());
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                stationId = rs.getInt("PK_STAT_ID");
            }
        } catch (SQLException sqle) {
            logWarning("There was a problem finding station - " + station.getLabel());
        }

        return stationId;
    }

    private int getVehicleId(Vehicle vehicle) {

        int vehicleId = 0;

        try {
            String sql = "SELECT PK_VEHI_ID FROM SOF_VEHI WHERE VEHI_NUMBER = '%s' AND VEHI_TYPE = '%s';";
            sql = String.format(sql, vehicle.getName(), vehicle.getType());
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                vehicleId = rs.getInt("PK_VEHI_ID");
            }
        } catch (SQLException sqle) {
            logWarning("There was a problem finding vehicle - " + vehicle.getLabel());
        }

        return vehicleId;
    }

}
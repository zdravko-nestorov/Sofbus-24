package bg.znestorov.sofbus24.apidb.utils;

import java.io.File;

import static bg.znestorov.sofbus24.apidb.utils.UtilsDuration.getDate;

public class Constants {

    /**
     * APPLICATION CONSTANTS
     */
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * LOGGER CONSTANTS
     */
    public static final String SOFBUS_LOGGER = "STATIONS AND VEHICLES DATABASE INFORMATION";
    public static final String DB_LOG_FILE = "log/MyLogFile.log";

    /**
     * DATABASE CONSTANTS
     */
    private static final String DB_PATH = "database/api/";
    private static final String DB_BACKUP_PATH = DB_PATH + "backups/";
    static final File DB_ORIGINAL_EMPTY_FILE = new File(DB_PATH + "sofbus24-empty.db");
    static final File DB_CURRENT_FULL_FILE = new File(DB_PATH + "sofbus24.db");
    static final File DB_CURRENT_JOURNAL_FULL_FILE = new File(DB_PATH + "sofbus24.db-journal");
    static final File DB_BACKUP_FULL_FILE = new File(DB_BACKUP_PATH + "sofbus24-" + getDate() + ".db");
    static final File DB_CONFIG_FULL_FILE = new File("../ConfigData/Databases/sofbus24.db");

    // Full DB information
    private static final String DB_INFO_CURRENT_PATH = "info/api/";
    private static final String DB_INFO_BACKUP_PATH = DB_INFO_CURRENT_PATH + "backups/";
    public static final File DB_INFORMATION_FILE = new File(DB_INFO_CURRENT_PATH + "Information.txt");
    public static final File DB_INFORMATION_BACKUP_FILE = new File(DB_INFO_BACKUP_PATH + "Information-" + getDate() + ".txt");

    // Metro stations DB information
    public static final String DB_INFORMATION_TITLE = "Database Information:";
    public static final String DB_INFORMATION_STATIONS = " * Stations - %s (BTT) + %s (METRO) = %s";
    public static final String DB_INFORMATION_VEHICLES = " * Vehicles - %s";
    public static final String DB_INFORMATION_STATIONS_VEHICLES = " * Stations/Vehicles - %s";

    public static final File DB_METRO_INFO_FILE = new File("../MetroSchedule/properties/metro_coordinates.properties");

    /**
     * STATIONS AND VEHICLES CODES CONSTANTS
     */
    public static final String URL_STATIONS_CODES = "https://routes.sofiatraffic.bg/resources/stops.json";
    public static final String URL_VEHICLES_CODES = "https://routes.sofiatraffic.bg/resources/routes.json";
    public static final String ELECTROBUS_CODES = "е.*?|9|309";

    /**
     * METRO JSON, STATIONS AND VEHICLES CONSTANTS
     */
    // JSON codes (used to retrieve the information)
    public static final String VEHICLE_TYPE = "type";
    public static final String VEHICLE_LINES = "lines";
    public static final String VEHICLE_ID = "id";
    public static final String VEHICLE_NAME = "name";
    public static final String VEHICLE_ROUTES = "routes";
    public static final String VEHICLE_CODE = "codes";

    // Metro #1 (Code: 1033)
    public static final String VEHICLE_METRO1_ID = "METRO_1";
    public static final String VEHICLE_METRO1_NAME = "1033";
    public static final String VEHICLE_METRO1_DIRECTION = "л.дфеиля аюсвеп - л.наекъ - л.лкюдняр-1";
    private static final String[] VEHICLE_METRO1_ROUTE1 = {"2975", "2977", "2979", "2981", "2983", "2985", "2987", "2989", "2991", "2993", "2995", "2997", "2999"};
    private static final String[] VEHICLE_METRO1_ROUTE2 = {"3001", "3003", "3005", "3007", "3009", "3011", "3013", "3015", "3017", "3019", "3021", "3023", "3025", "3027", "3029", "3031", "3033", "3035", "3037", "3039", "3041", "3043"};
    private static final String[] VEHICLE_METRO1_ROUTE3 = {"3309", "3311", "3315", "3317", "3319", "3321", "3323", "3327", "3329", "3331", "3333", "3335"};
    public static final String[][] VEHICLE_METRO1_ROUTES = {VEHICLE_METRO1_ROUTE1, VEHICLE_METRO1_ROUTE2, VEHICLE_METRO1_ROUTE3};

    // Metro #2 (Code: 1034)
    public static final String VEHICLE_METRO2_ID = "METRO_2";
    public static final String VEHICLE_METRO2_NAME = "1034";
    public static final String VEHICLE_METRO2_DIRECTION = "л.лкюдняр-1 - л.наекъ - л.дфеиля аюсвеп";
    private static final String[] VEHICLE_METRO2_ROUTE1 = {"3336", "3334", "3332", "3330", "3328", "3324", "3322", "3320", "3318", "3316", "3312", "3310"};
    private static final String[] VEHICLE_METRO2_ROUTE2 = {"3044", "3042", "3040", "3038", "3036", "3034", "3032", "3030", "3028", "3026", "3024", "3022", "3020", "3018", "3016", "3014", "3012", "3010", "3008", "3006", "3004", "3002"};
    private static final String[] VEHICLE_METRO2_ROUTE3 = {"3000", "2998", "2996", "2994", "2992", "2990", "2988", "2986", "2984", "2982", "2980", "2978", "2976"};
    public static final String[][] VEHICLE_METRO2_ROUTES = {VEHICLE_METRO2_ROUTE1, VEHICLE_METRO2_ROUTE2, VEHICLE_METRO2_ROUTE3};
}

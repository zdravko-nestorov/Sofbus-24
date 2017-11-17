package bg.znestorov.sofbus24.db.utils;

public class Constants {

    /**
     * MAIN CONSTANTS
     */
    public static final String DB_LOG_FILE = "log/MyLogFile.log";
    public static final String METRO_STATIONS_FILE = "data/html/metro_stations.txt";
    public static final String SKGT_STATIONS_FILE = "data/html/skgt_stations.xml";

    /**
     * VEHICLES NUMBERS CONSTANTS
     */
    public static final String DB_VEHICLES_NUMBERS_URL = "http://schedules.sofiatraffic.bg/";
    public static final String DB_VEHICLES_URL = "http://m.sofiatraffic.bg/schedules?tt=%s&ln=%s&s=Търсене";
    public static final String DB_VEHICLES_USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.107 Safari/537.36";

    public static final String DB_VEHICLES_TYPES_REGEX = "<div class=\"lines_section\">([^^]*?)<\\/div>";
    public static final String DB_VEHICLES_NUMBERS_REGEX = "<li><a href=\"(.*?)\">(.*?)<\\/a><\\/li>";

    public static final String DB_VEHICLE_DIRECTION_REGEX = "<div class=\"info\">(.*?)<\\/div>";

    public static final String DB_STATION_DIRECTION_REGEX = "<td><input type=\"submit\" value=\"Провери\" name=\"ch\" class=\"btn\"/></td>";
    public static final String DB_STATION_REGEX = "<option id=\".*?\" value=\"(.*?)\">(.*?)<\\/option>";

    public static final String DB_VEHICLE_STATION_REGEX = "<input type=\"hidden\" value=\"([0-9]+)\" name=\"%s\"/>";
}

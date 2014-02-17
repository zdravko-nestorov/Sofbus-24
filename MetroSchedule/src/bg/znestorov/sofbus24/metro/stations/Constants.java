package bg.znestorov.sofbus24.metro.stations;

public class Constants {

	public static final String METRO_SCHEDULE_URL = "http://schedules.sofiatraffic.bg/metro/1";
	public static final String METRO_USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.107 Safari/537.36";

	public static final String METRO_REGEX_PARTS = "<div class=\"schedule_direction_sign_wrapper\">";
	public static final String METRO_REGEX_DIRECTIONS = ".*?<li>[^^]*?<a href=\"/metro/1#direction/([0-9]{4}).*?id=\"schedule_direction_.*?class=\"schedule_view_direction[^^]*?<span>(.*?)</span>";
	public static final String METRO_REGEX_STATION_PARTS = "<div class=\"schedule_view_direction\">";
	public static final String METRO_REGEX_STATIONS = ".*?<a class=\"stop_link\" id=\"schedule_[0-9]*_direction_[0-9]*_sign_[0-9]*_stop\" href=\".*?\">([0-9]*)</a>[^^]*?<a class=\"stop_change\" id=\"schedule_[0-9]*_direction_[0-9]*_sign_[0-9]*\" href=\".*?\">(.*?)</a>[^^]*?</li>";

	public static final String METRO_SCHEDULE_FILE = "schedule/MetroSchedule.xml";

	public static final String STATION_SCHEDULE_FILE_LOCATION = "https://sofia-stations.googlecode.com/svn/MetroSchedule/schedule/Station%s.xml";

}

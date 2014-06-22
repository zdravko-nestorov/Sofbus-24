package bg.znestorov.sobusf24.db.information;

import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bg.znestorov.sobusf24.db.utils.Constants;
import bg.znestorov.sobusf24.db.utils.Utils;
import bg.znestorov.sofbus24.db.entity.Station;
import bg.znestorov.sofbus24.db.entity.Vehicle;
import bg.znestorov.sofbus24.db.entity.VehicleType;

public class HtmlResult {

	public static Vehicle getVehicle(Logger logger, String htmlResponse,
			String type, String number) {

		logger.info("[Vehicle] Start parsing the information...");

		Pattern directionPattern = Pattern
				.compile(Constants.DB_VEHICLE_DIRECTION_REGEX);
		Matcher directionMatcher = directionPattern.matcher(htmlResponse);

		if (directionMatcher.find()) {
			String direction = directionMatcher.group(1);
			direction = Utils.formatDirectionName(direction);

			return new Vehicle(type, number, direction);
		}

		return new Vehicle(type, number, "");
	}

	public static ArrayList<Station> getStations(Logger logger,
			String htmlResponse) {

		logger.info("[Station] Start parsing the information...");

		ArrayList<Station> stationsList = new ArrayList<Station>();
		Pattern stationPattern = Pattern.compile(Constants.DB_STATION_REGEX);
		Matcher stationMatcher = stationPattern.matcher(htmlResponse);

		while (stationMatcher.find()) {
			String name = stationMatcher.group(1);
			String number = Utils.getValueAfterLast(name, "(");
			number = Utils.getValueBefore(number, ")");
			number = Utils.removeSpaces(number);
			name = Utils.getValueBeforeLast(name, "(");
			name = name.trim();

			if (number.matches("-?\\d+(\\.\\d+)?")) {
				stationsList.add(new Station(VehicleType.BTT, number, name));
			}
		}

		return stationsList;
	}
}

package bg.znestorov.sobusf24.db.information;

import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bg.znestorov.sobusf24.db.utils.Constants;
import bg.znestorov.sobusf24.db.utils.Utils;
import bg.znestorov.sofbus24.db.entity.Station;
import bg.znestorov.sofbus24.db.entity.Vehicle;
import bg.znestorov.sofbus24.db.entity.VehicleStation;
import bg.znestorov.sofbus24.db.entity.VehicleType;

public class HtmlResult {

	public static Vehicle getVehicle(Logger logger, String htmlResponse, String type, String number) {

		logger.info("[Vehicle] Start parsing the information...");

		Pattern directionPattern = Pattern.compile(Constants.DB_VEHICLE_DIRECTION_REGEX);
		Matcher directionMatcher = directionPattern.matcher(htmlResponse);

		String direction = "";
		if (directionMatcher.find()) {
			direction = directionMatcher.group(1);
			direction = Utils.formatDirectionName(direction);
		}

		direction = proceedSpecialTypeOfDirection(number, direction);

		return new Vehicle(type, number, direction);
	}

	private static String proceedSpecialTypeOfDirection(String number, String direction) {

		if ("66".equals(number)) {
			direction = "Автостанция Хладилника - хотел Морени";
		}

		if ("103".equals(number)) {
			direction = "Автостанция Овча купел - в.з. Бонсови поляни";
		}

		if ("8-ТМ".equals(number)) {
			direction = "ж.к. Западен парк - ул. Димитър Петков";
		}

		if ("10-ТМ".equals(number)) {
			direction = "Автостанция Хладилника - НДК";
		}

		if ("11-А".equals(number)) {
			direction = "Площад Сточна гара - ж.к. Дружба 1";
		}

		return direction;
	}

	public static ArrayList<Station> getStations(Logger logger, String htmlResponse) {

		logger.info("[Station] Start parsing the information...");

		ArrayList<Station> stationsList = new ArrayList<Station>();
		String[] htmlResponseParts = htmlResponse.split(Constants.DB_STATION_DIRECTION_REGEX);

		if (htmlResponseParts.length == 3) {

			for (int i = 0; i < 3; i++) {
				Pattern stationPattern = Pattern.compile(Constants.DB_STATION_REGEX);
				Matcher stationMatcher = stationPattern.matcher(htmlResponseParts[i]);

				while (stationMatcher.find()) {
					String stop = stationMatcher.group(1);
					String name = stationMatcher.group(2);
					String number = Utils.getValueAfterLast(name, "(");
					number = Utils.getValueBefore(number, ")");
					number = Utils.removeSpaces(number);
					name = Utils.getValueBeforeLast(name, "(");
					name = name.trim();
					name = name.replaceAll("&quot;", "\"");
					stop = stop.trim();

					if (number.matches("-?\\d+(\\.\\d+)?")) {
						stationsList.add(new Station(VehicleType.BTT, number, name, stop, i + 1));
					}
				}
			}
		}

		return stationsList;
	}

	public static ArrayList<VehicleStation> getVehicleStations(Logger logger, String htmlResponse, Vehicle vehicle, ArrayList<Station> stationsList) {

		logger.info("[VehicleStation] Start parsing the information...");

		ArrayList<VehicleStation> vehicleStationsList = new ArrayList<VehicleStation>();
		if ("10-ТМ".equals(vehicle.getNumber()) || "66".equals(vehicle.getNumber()) || "103".equals(vehicle.getNumber()) || "11-А".equals(vehicle.getNumber())) {
			vehicleStationsList.addAll(getSpecialVehicleStations(logger, htmlResponse, vehicle, stationsList));
		} else {
			String vt = getVehicleStationHiddenParam(htmlResponse, "vt");
			String lid = getVehicleStationHiddenParam(htmlResponse, "lid");
			String rid = getVehicleStationHiddenParam(htmlResponse, "rid");

			for (int i = 0; i < stationsList.size(); i++) {
				Station station = stationsList.get(i);
				vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), station.getNumber(), vt, lid, rid, station.getStop(),
						station.getDirection()));
			}
		}

		return vehicleStationsList;
	}

	public static ArrayList<VehicleStation> getSpecialVehicleStations(Logger logger, String htmlResponse, Vehicle vehicle, ArrayList<Station> stationsList) {

		logger.info("[VehicleStation (Special Cases)] Start parsing the information...");

		ArrayList<VehicleStation> vehicleStationsList = new ArrayList<VehicleStation>();

		if (vehicle.getType() == VehicleType.BUS && "10-ТМ".equals(vehicle.getNumber())) {
			vehicleStationsList.addAll(getSpecialVehicleStations10TM(vehicle));
		}

		if (vehicle.getType() == VehicleType.BUS && "66".equals(vehicle.getNumber())) {
			vehicleStationsList.addAll(getSpecialVehicleStations66(vehicle));
		}

		if (vehicle.getType() == VehicleType.BUS && "103".equals(vehicle.getNumber())) {
			vehicleStationsList.addAll(getSpecialVehicleStations103(vehicle));
		}

		if (vehicle.getType() == VehicleType.TROLLEY && "11-А".equals(vehicle.getNumber())) {
			vehicleStationsList.addAll(getSpecialVehicleStations11A(vehicle));
		}

		return vehicleStationsList;
	}

	private static ArrayList<VehicleStation> getSpecialVehicleStations10TM(Vehicle vehicle) {

		ArrayList<VehicleStation> vehicleStationsList = new ArrayList<VehicleStation>();
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0064", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0909", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "2654", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0342", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "2039", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0923", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "2330", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0397", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0397", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "1322", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0922", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "2038", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0343", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "2655", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0912", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0064", 2));

		return vehicleStationsList;
	}

	private static ArrayList<VehicleStation> getSpecialVehicleStations66(Vehicle vehicle) {

		ArrayList<VehicleStation> vehicleStationsList = new ArrayList<VehicleStation>();
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0064", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0834", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "2697", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "2704", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "2704", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "2696", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0837", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0064", 2));

		return vehicleStationsList;
	}

	private static ArrayList<VehicleStation> getSpecialVehicleStations103(Vehicle vehicle) {

		ArrayList<VehicleStation> vehicleStationsList = new ArrayList<VehicleStation>();
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "2705", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0095", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0900", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0041", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0773", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0347", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "2049", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0766", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0480", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "2707", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "2709", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "2710", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "2712", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "2714", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "2716", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "2718", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "2720", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "2720", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "2719", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "2717", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "2715", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "2713", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "2711", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0829", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "2708", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "2706", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0493", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0765", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "2050", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0346", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0776", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0042", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "2541", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0094", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "2705", 2));

		return vehicleStationsList;
	}

	private static ArrayList<VehicleStation> getSpecialVehicleStations11A(Vehicle vehicle) {

		ArrayList<VehicleStation> vehicleStationsList = new ArrayList<VehicleStation>();
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "1319", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "1964", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "1242", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "1698", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "1287", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "1394", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0074", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "1820", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "2326", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "1696", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "1257", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "1260", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "2615", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "1152", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0370", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "1986", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0256", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0578", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0235", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0142", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0144", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0146", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0516", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0605", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "2424", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0517", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0147", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0143", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0141", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0236", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0577", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0257", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0255", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "1983", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0371", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "1151", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "2616", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "1695", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "2327", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "1819", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "0073", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "1395", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "1288", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "1699", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "1241", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "1965", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(), vehicle.getNumber(), "1313", 2));

		return vehicleStationsList;
	}

	private static String getVehicleStationHiddenParam(String htmlResponse, String hiddenParamName) {

		String hiddenParam = "";

		Pattern vehicleStationPattern = Pattern.compile(String.format(Constants.DB_VEHICLE_STATION_REGEX, hiddenParamName));
		Matcher vehicleStationMatcher = vehicleStationPattern.matcher(htmlResponse);

		if (vehicleStationMatcher.find()) {
			hiddenParam = vehicleStationMatcher.group(1);
			hiddenParam.trim();
		}

		return hiddenParam;
	}
}

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

	public static Vehicle getVehicle(Logger logger, String htmlResponse,
			String type, String number) {

		logger.info("[Vehicle] Start parsing the information...");

		Pattern directionPattern = Pattern
				.compile(Constants.DB_VEHICLE_DIRECTION_REGEX);
		Matcher directionMatcher = directionPattern.matcher(htmlResponse);

		String direction = "";
		if (directionMatcher.find()) {
			direction = directionMatcher.group(1);
			direction = Utils.formatDirectionName(direction);
		}

		direction = proceedSpecialTypeOfDirection(number, direction);

		return new Vehicle(type, number, direction);
	}

	private static String proceedSpecialTypeOfDirection(String number,
			String direction) {

		/*
		 * SPECIAL CASES Public Transport
		 */
		if ("1-ТБ".equals(number)) {
			direction = "ж.к. Левски Г - ж.к. Хаджи Димитър";
		}

		if ("4-ТМ".equals(number)) {
			direction = "Автостанция Орландовци - Централна гара";
		}

		if ("5-ТМ".equals(number)) {
			direction = "Автостанция Княжево - ж.к. Бъкстон";
		}

		if ("6Т1".equals(number)) {
			direction = "Бул. Витоша - хотел Хемус";
		}

		if ("6Т2".equals(number)) {
			direction = "ПК Спартак - НДК";
		}

		if ("10-ТМ".equals(number)) {
			direction = "НДК (хотел Хилтън) - Зоопарка";
		}

		if ("44-Б".equals(number)) {
			direction = "Автостанция Банкя - Метростанция Сливница";
		}

		if ("11-А".equals(number)) {
			direction = "Площад Сточна гара - ж.к. Дружба 1";
		}

		/*
		 * WEEKEND Public Transport
		 */
		if ("66".equals(number)) {
			direction = "Хотел Морени - ж.к. Гоце Делчев";
		}

		if ("103".equals(number)) {
			direction = "Автостанция Овча купел - в.з. Бонсови поляни";
		}

		if ("505".equals(number)) {
			direction = "Площад Орлов мост - парк музей Врана";
		}

		return direction;
	}

	public static ArrayList<Station> getStations(Logger logger,
			String htmlResponse) {

		logger.info("[Station] Start parsing the information...");

		ArrayList<Station> stationsList = new ArrayList<Station>();
		String[] htmlResponseParts = htmlResponse
				.split(Constants.DB_STATION_DIRECTION_REGEX);

		if (htmlResponseParts.length == 3) {

			for (int i = 0; i < 3; i++) {
				Pattern stationPattern = Pattern
						.compile(Constants.DB_STATION_REGEX);
				Matcher stationMatcher = stationPattern
						.matcher(htmlResponseParts[i]);

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
						stationsList.add(new Station(VehicleType.BTT, number,
								name, stop, i + 1));
					}
				}
			}
		}

		return stationsList;
	}

	public static ArrayList<VehicleStation> getVehicleStations(Logger logger,
			String htmlResponse, Vehicle vehicle,
			ArrayList<Station> stationsList) {

		logger.info("[VehicleStation] Start parsing the information...");

		ArrayList<VehicleStation> vehicleStationsList = new ArrayList<VehicleStation>();
		if ("1-ТБ".equals(vehicle.getNumber())
				|| "4-ТМ".equals(vehicle.getNumber())
				|| "5-ТМ".equals(vehicle.getNumber())
				|| "6Т1".equals(vehicle.getNumber())
				|| "6Т2".equals(vehicle.getNumber())
				|| "10-ТМ".equals(vehicle.getNumber())
				|| "44-Б".equals(vehicle.getNumber())
				|| "11-А".equals(vehicle.getNumber())
				|| "66".equals(vehicle.getNumber())
				|| "103".equals(vehicle.getNumber())
				|| "505".equals(vehicle.getNumber())) {
			vehicleStationsList.addAll(getSpecialVehicleStations(logger,
					htmlResponse, vehicle, stationsList));
		} else {
			String vt = getVehicleStationHiddenParam(htmlResponse, "vt");
			String lid = getVehicleStationHiddenParam(htmlResponse, "lid");
			String rid = getVehicleStationHiddenParam(htmlResponse, "rid");

			for (int i = 0; i < stationsList.size(); i++) {
				Station station = stationsList.get(i);

				vehicleStationsList.add(new VehicleStation(vehicle.getType(),
						vehicle.getNumber(), station.getNumber(), vt, lid, rid,
						station.getStop(), station.getDirection()));

				// Add NDK-Tunnel and NDK-Grafitti stations to the list of
				// vehicles
				VehicleType vehicleType = vehicle.getType();
				String vehicleNumber = vehicle.getNumber();
				String stationNumber = station.getNumber();

				if (vehicleType == VehicleType.TROLLEY
						&& ("1".equals(vehicleNumber)
								|| "2".equals(vehicleNumber)
								|| "5".equals(vehicleNumber)
								|| "7".equals(vehicleNumber)
								|| "8".equals(vehicleNumber)
								|| "9".equals(vehicleNumber))
						&& "0363".equals(stationNumber)) {

					vehicleStationsList
							.add(new VehicleStation(vehicle.getType(),
									vehicle.getNumber(), "1139", vt, lid, rid,
									station.getStop(), station.getDirection()));

				} else if (vehicleType == VehicleType.TRAM
						&& ("0364".equals(stationNumber)
								|| "0400".equals(stationNumber))
						&& "6".equals(vehicleNumber)) {

					if ("0364".equals(stationNumber)) {
						vehicleStationsList.add(new VehicleStation(
								vehicle.getType(), vehicle.getNumber(), "1137",
								vt, lid, rid, station.getStop(),
								station.getDirection()));
					} else {
						vehicleStationsList.add(new VehicleStation(
								vehicle.getType(), vehicle.getNumber(), "1138",
								vt, lid, rid, station.getStop(),
								station.getDirection()));
					}
				}

			}
		}

		return vehicleStationsList;
	}

	public static ArrayList<VehicleStation> getSpecialVehicleStations(
			Logger logger, String htmlResponse, Vehicle vehicle,
			ArrayList<Station> stationsList) {

		logger.info(
				"[VehicleStation (Special Cases)] Start parsing the information...");

		ArrayList<VehicleStation> vehicleStationsList = new ArrayList<VehicleStation>();

		if (vehicle.getType() == VehicleType.BUS
				&& "1-ТБ".equals(vehicle.getNumber())) {
			vehicleStationsList.addAll(getSpecialVehicleStations1TB(vehicle));
		}

		if (vehicle.getType() == VehicleType.BUS
				&& "4-ТМ".equals(vehicle.getNumber())) {
			vehicleStationsList.addAll(getSpecialVehicleStations4TM(vehicle));
		}

		if (vehicle.getType() == VehicleType.BUS
				&& "5-ТМ".equals(vehicle.getNumber())) {
			vehicleStationsList.addAll(getSpecialVehicleStations5TM(vehicle));
		}

		if (vehicle.getType() == VehicleType.BUS
				&& "6Т1".equals(vehicle.getNumber())) {
			vehicleStationsList.addAll(getSpecialVehicleStations6T1(vehicle));
		}

		if (vehicle.getType() == VehicleType.BUS
				&& "6Т2".equals(vehicle.getNumber())) {
			vehicleStationsList.addAll(getSpecialVehicleStations6T2(vehicle));
		}

		if (vehicle.getType() == VehicleType.BUS
				&& "10-ТМ".equals(vehicle.getNumber())) {
			vehicleStationsList.addAll(getSpecialVehicleStations10TM(vehicle));
		}

		if (vehicle.getType() == VehicleType.BUS
				&& "44-Б".equals(vehicle.getNumber())) {
			vehicleStationsList.addAll(getSpecialVehicleStations44B(vehicle));
		}

		if (vehicle.getType() == VehicleType.BUS
				&& "66".equals(vehicle.getNumber())) {
			vehicleStationsList.addAll(getSpecialVehicleStations66(vehicle));
		}

		if (vehicle.getType() == VehicleType.BUS
				&& "103".equals(vehicle.getNumber())) {
			vehicleStationsList.addAll(getSpecialVehicleStations103(vehicle));
		}

		if (vehicle.getType() == VehicleType.BUS
				&& "505".equals(vehicle.getNumber())) {
			vehicleStationsList.addAll(getSpecialVehicleStations505(vehicle));
		}

		if (vehicle.getType() == VehicleType.TROLLEY
				&& "11-А".equals(vehicle.getNumber())) {
			vehicleStationsList.addAll(getSpecialVehicleStations11A(vehicle));
		}

		return vehicleStationsList;
	}

	private static ArrayList<VehicleStation> getSpecialVehicleStations1TB(
			Vehicle vehicle) {

		ArrayList<VehicleStation> vehicleStationsList = new ArrayList<VehicleStation>();
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1256", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0324", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2105", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2171", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2111", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0283", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1295", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1296", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0366", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0366", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0355", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "6300", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1300", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0284", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2114", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2170", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2102", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0339", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2596", 2));

		return vehicleStationsList;
	}

	private static ArrayList<VehicleStation> getSpecialVehicleStations4TM(
			Vehicle vehicle) {

		ArrayList<VehicleStation> vehicleStationsList = new ArrayList<VehicleStation>();
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0062", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "6281", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2332", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2311", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1318", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1311", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1275", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2002", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1327", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1327", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2003", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1278", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1321", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1312", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2309", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2333", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "6283", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0062", 2));

		return vehicleStationsList;
	}

	private static ArrayList<VehicleStation> getSpecialVehicleStations5TM(
			Vehicle vehicle) {

		ArrayList<VehicleStation> vehicleStationsList = new ArrayList<VehicleStation>();
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0853", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "6129", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "6125", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0391", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0891", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0876", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0289", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0289", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0875", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0888", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "6126", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "6127", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "6128", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0853", 2));

		return vehicleStationsList;
	}

	private static ArrayList<VehicleStation> getSpecialVehicleStations6T1(
			Vehicle vehicle) {

		ArrayList<VehicleStation> vehicleStationsList = new ArrayList<VehicleStation>();
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "6230", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "6231", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "6232", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2380", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1345", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2330", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2330", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0395", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "6230", 2));

		return vehicleStationsList;
	}

	private static ArrayList<VehicleStation> getSpecialVehicleStations6T2(
			Vehicle vehicle) {

		ArrayList<VehicleStation> vehicleStationsList = new ArrayList<VehicleStation>();
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1344", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "6227", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "6228", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "6229", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0397", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0397", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2328", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1344", 2));

		return vehicleStationsList;
	}

	private static ArrayList<VehicleStation> getSpecialVehicleStations10TM(
			Vehicle vehicle) {

		ArrayList<VehicleStation> vehicleStationsList = new ArrayList<VehicleStation>();
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2764", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0977", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2314", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2654", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0342", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2039", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0923", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2330", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0397", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0397", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1322", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0922", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2038", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0343", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2655", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0912", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2313", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0976", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0749", 2));

		return vehicleStationsList;
	}

	private static ArrayList<VehicleStation> getSpecialVehicleStations44B(
			Vehicle vehicle) {

		ArrayList<VehicleStation> vehicleStationsList = new ArrayList<VehicleStation>();
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0050", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1618", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0107", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0454", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0996", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1024", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0790", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2307", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1430", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2520", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1685", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0771", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0895", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1751", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1140", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1061", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1063", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1141", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1752", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0896", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0772", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1686", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2521", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1431", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2308", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0789", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1027", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0997", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0453", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0106", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1617", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1403", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0503", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0502", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0051", 2));

		return vehicleStationsList;
	}

	private static ArrayList<VehicleStation> getSpecialVehicleStations11A(
			Vehicle vehicle) {

		ArrayList<VehicleStation> vehicleStationsList = new ArrayList<VehicleStation>();
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1319", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1964", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1242", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1698", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1287", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1394", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0074", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1820", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2326", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1696", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1257", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1260", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2615", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1152", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0370", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1986", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0256", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0578", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0235", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0142", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0144", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0146", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0516", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0605", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2424", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0517", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0147", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0143", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0141", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0236", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0577", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0257", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0255", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1983", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0371", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1151", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2616", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1695", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2327", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1819", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0073", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1395", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1288", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1699", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1241", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1965", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1313", 2));

		return vehicleStationsList;
	}

	private static ArrayList<VehicleStation> getSpecialVehicleStations66(
			Vehicle vehicle) {

		ArrayList<VehicleStation> vehicleStationsList = new ArrayList<VehicleStation>();
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2704", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2696", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0837", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1456", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0599", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0600", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2597", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0834", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2697", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2704", 2));

		return vehicleStationsList;
	}

	private static ArrayList<VehicleStation> getSpecialVehicleStations103(
			Vehicle vehicle) {

		ArrayList<VehicleStation> vehicleStationsList = new ArrayList<VehicleStation>();
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2705", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0095", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0900", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0041", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0773", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0347", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2049", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0766", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0480", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2707", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2709", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2710", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2712", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2714", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2716", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2718", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2720", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2720", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2719", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2717", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2715", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2713", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2711", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0829", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2708", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2706", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0493", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0765", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2050", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0346", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0776", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0042", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2541", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "0094", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2705", 2));

		return vehicleStationsList;
	}

	private static ArrayList<VehicleStation> getSpecialVehicleStations505(
			Vehicle vehicle) {

		ArrayList<VehicleStation> vehicleStationsList = new ArrayList<VehicleStation>();
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1287", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2326", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1194", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1017", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "6546", 1));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "6546", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1016", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1196", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "2327", 2));
		vehicleStationsList.add(new VehicleStation(vehicle.getType(),
				vehicle.getNumber(), "1289", 2));

		return vehicleStationsList;
	}

	private static String getVehicleStationHiddenParam(String htmlResponse,
			String hiddenParamName) {

		String hiddenParam = "";

		Pattern vehicleStationPattern = Pattern.compile(String
				.format(Constants.DB_VEHICLE_STATION_REGEX, hiddenParamName));
		Matcher vehicleStationMatcher = vehicleStationPattern
				.matcher(htmlResponse);

		if (vehicleStationMatcher.find()) {
			hiddenParam = vehicleStationMatcher.group(1);
			hiddenParam.trim();
		}

		return hiddenParam;
	}
}

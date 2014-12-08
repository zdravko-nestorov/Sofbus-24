package bg.znestorov.sobusf24.db.information;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import bg.znestorov.sofbus24.db.entity.Station;
import bg.znestorov.sofbus24.db.entity.Vehicle;
import bg.znestorov.sofbus24.db.entity.VehicleStation;

public class InformationMain {

	public static HashMap<String, Object> getInformation(Logger logger, String type, String number) {
		String htmlResponse = HtmlRequest.retrieveVehicles(logger, type, number);
		if (htmlResponse == null || "".equals(htmlResponse)) {
			logger.info("Problem with the HTTP GET request to the SUMC site for vehicle[Type=" + type + ", Number=" + number + "]");
			return null;
		}

		Vehicle vehicle = HtmlResult.getVehicle(logger, htmlResponse, type, number);
		ArrayList<Station> stationsList = HtmlResult.getStations(logger, htmlResponse);
		ArrayList<VehicleStation> vehicleStationsList = HtmlResult.getVehicleStations(logger, htmlResponse, vehicle, stationsList);

		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("vehicle", vehicle);
		resultMap.put("stations", stationsList);
		resultMap.put("vehice_stations", vehicleStationsList);

		return resultMap;
	}
}

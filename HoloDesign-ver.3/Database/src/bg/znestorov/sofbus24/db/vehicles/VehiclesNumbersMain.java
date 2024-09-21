package bg.znestorov.sofbus24.db.vehicles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class VehiclesNumbersMain {

  public static HashMap<Integer, ArrayList<String>> getVehiclesNumbers(
      Logger logger) {
    String htmlResponse = HtmlRequestVehiclesNumbers
        .retrieveVehiclesNumbers(logger);
    if (htmlResponse == null || "".equals(htmlResponse)) {
      logger.info("Problem with the HTTP GET request to the SUMC site!");
      return null;
    }

    HashMap<Integer, ArrayList<String>> vehiclesMap = HtmlResultVehiclesNumbers
        .getVehiclesNumbers(logger, htmlResponse);
    if (vehiclesMap.isEmpty()) {
      logger.info("Problem with the HTTP result - no vehicles are found!");
      return null;
    }

    return vehiclesMap;
  }

}

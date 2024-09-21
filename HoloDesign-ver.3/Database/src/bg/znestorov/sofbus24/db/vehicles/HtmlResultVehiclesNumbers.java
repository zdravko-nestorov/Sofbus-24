package bg.znestorov.sofbus24.db.vehicles;

import bg.znestorov.sofbus24.db.utils.Constants;
import bg.znestorov.sofbus24.db.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlResultVehiclesNumbers {

  public static HashMap<Integer, ArrayList<String>> getVehiclesNumbers(
      Logger logger, String htmlResponse) {

    logger.info("Start parsing the information...");

    HashMap<Integer, ArrayList<String>> vehiclesMap = new HashMap<Integer, ArrayList<String>>();
    ArrayList<String> autobusList = new ArrayList<String>();
    ArrayList<String> trolleybusList = new ArrayList<String>();
    ArrayList<String> tramwayList = new ArrayList<String>();

    Pattern typesPattern = Pattern
        .compile(Constants.DB_VEHICLES_TYPES_REGEX);
    Matcher typesMatcher = typesPattern.matcher(htmlResponse);

    while (typesMatcher.find()) {
      String htmlResponsePart = typesMatcher.group(1);

      Pattern stationPattern = Pattern
          .compile(Constants.DB_VEHICLES_NUMBERS_REGEX);
      Matcher stationMatcher = stationPattern.matcher(htmlResponsePart);

      while (stationMatcher.find()) {
        String vehicleType = stationMatcher.group(1).trim()
            .toUpperCase();
        String vehicleNumber = stationMatcher.group(2).trim();

        if ("21-22".equals(vehicleNumber)) {
          autobusList.add("21");
          autobusList.add("22");
        } else {
          if (vehicleType.contains("AUTOBUS")) {
            autobusList.add(vehicleNumber);
          } else if (vehicleType.contains("TROLLEYBUS")) {
            trolleybusList.add(vehicleNumber);
          } else {
            tramwayList.add(vehicleNumber);
          }
        }
      }
    }

    Comparator<String> vehiclesNumberComparator = new Comparator<String>() {
      @Override
      public int compare(String value1, String value2) {
        long value1Long = Long.parseLong(Utils.getValueBefore(value1,
            "-").replaceAll("\\D+", ""));
        long value2Long = Long.parseLong(Utils.getValueBefore(value2,
            "-").replaceAll("\\D+", ""));

        return compare(value1Long, value2Long);
      }

      private int compare(long a, long b) {
        return a < b ? -1 : a > b ? 1 : 0;
      }
    };
    Collections.sort(autobusList, vehiclesNumberComparator);
    Collections.sort(trolleybusList, vehiclesNumberComparator);
    Collections.sort(tramwayList, vehiclesNumberComparator);

    vehiclesMap.put(0, tramwayList);
    vehiclesMap.put(1, autobusList);
    vehiclesMap.put(2, trolleybusList);

    return vehiclesMap;
  }

}

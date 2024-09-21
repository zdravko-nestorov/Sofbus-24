package bg.znestorov.sofbus24.db.coordinates;

import bg.znestorov.sofbus24.db.entity.Station;
import bg.znestorov.sofbus24.db.utils.Constants;
import bg.znestorov.sofbus24.db.utils.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

public class StationCoordinates {

  private static final String STATION_XPATH =
      "/stations/station[@code='%s' and @lat!='' and @lon!='']/@*[name()='lat' or name()='lon']";
  private Logger logger;

  public StationCoordinates(Logger logger) {
    this.logger = logger;
  }

  public Station getStationFromXml(Station station) {

    BufferedReader inputBufferedReader = null;

    try {
      String formattedStationNumber = Utils
          .removeLeadingZeros(station.getNumber());

      // Get the Document from the "skt_stations.xml" file
      DocumentBuilderFactory factory = DocumentBuilderFactory
          .newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(Constants.SKGT_STATIONS_FILE);

      // Create the XPATH instance for the current station
      XPathFactory xPathfactory = XPathFactory.newInstance();
      XPath xpath = xPathfactory.newXPath();
      XPathExpression expr = xpath.compile(
          String.format(STATION_XPATH, formattedStationNumber));

      // Evaluate the XPATH for this station
      NodeList stationsNodeList = (NodeList) expr.evaluate(doc,
          XPathConstants.NODESET);

      // Only in case the XPATH expression evaluates 2 results, proceed
      // ahead (update the stations coordinates)
      if (stationsNodeList.getLength() == 2) {
        String latitude = stationsNodeList.item(0).getNodeValue();
        String longitude = stationsNodeList.item(1).getNodeValue();

        station.setLatitude(latitude);
        station.setLongitude(longitude);

        return station;
      }

    } catch (Exception e) {
      logger.severe(
          "Problem with reading the file with SKGT stations...");
    } finally {
      if (inputBufferedReader != null) {
        try {
          inputBufferedReader.close();
        } catch (IOException e) {
          logger.info(
              "Problem with closing the file with SKGT stations...");
        }
      }
    }

    return null;
  }

}

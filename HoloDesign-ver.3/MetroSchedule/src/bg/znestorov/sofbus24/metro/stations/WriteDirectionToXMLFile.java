package bg.znestorov.sofbus24.metro.stations;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import bg.znestorov.sobusf24.metro.utils.Constants;

/**
 * Write the MetroDirectionTransfer object to a XML file
 */
public class WriteDirectionToXMLFile {

	public static void saveToXMLFile(Logger logger,
			MetroDirectionTransfer mdt) {

		logger.info("Saving the METRO directions and stations to a XML file");

		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// Root elements
			Document doc = docBuilder.newDocument();
			Element metroSchedule = doc.createElement("MetroSchedule");
			doc.appendChild(metroSchedule);

			// Create Directions Count element
			Element directionCount = doc.createElement("DirectionsCount");
			directionCount.appendChild(
					doc.createTextNode(mdt.getDirectionsListSize() + ""));
			metroSchedule.appendChild(directionCount);

			logger.info("Creating the XML structure...");

			// Create each Direction element
			for (MetroDirection metroDirection : mdt.getDirectionsList()) {
				Element direction = doc.createElement("Direction");
				direction.setAttribute("id", metroDirection.getId());
				metroSchedule.appendChild(direction);

				Element name = doc.createElement("Name");
				name.appendChild(doc.createTextNode(metroDirection.getName()));
				direction.appendChild(name);

				Element stationsCount = doc.createElement("StationsCount");
				stationsCount.appendChild(doc.createTextNode(
						metroDirection.getStations().size() + ""));
				direction.appendChild(stationsCount);

				// Create each Station element
				Iterator<Entry<String, String>> iterator = metroDirection
						.getStations().entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry<String, String> mapEntry = (Map.Entry<String, String>) iterator
							.next();

					Element station = doc.createElement("Station");
					station.setAttribute("number", mapEntry.getKey());
					direction.appendChild(station);

					Element stationName = doc.createElement("Name");
					stationName.appendChild(
							doc.createTextNode(mapEntry.getValue()));
					station.appendChild(stationName);

					Element stationScheduleUrl = doc
							.createElement("StationScheduleURL");
					stationScheduleUrl.appendChild(doc.createTextNode(String
							.format(Constants.STATION_SCHEDULE_FILE_LOCATION,
									mapEntry.getKey())));
					station.appendChild(stationScheduleUrl);
				}
			}

			logger.info("Saving the XML structure to a file...");

			// Write the content into XML file
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(
					new File(Constants.METRO_SCHEDULE_FILE));
			transformer.transform(source, result);
		} catch (ParserConfigurationException pce) {
			logger.warning("ParserConfigurationException: " + pce.toString());
		} catch (TransformerException tfe) {
			logger.warning("TransformerException: " + tfe.toString());
		}
	}
}
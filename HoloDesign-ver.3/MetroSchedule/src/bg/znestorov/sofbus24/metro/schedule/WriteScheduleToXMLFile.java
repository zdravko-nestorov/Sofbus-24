package bg.znestorov.sofbus24.metro.schedule;

import java.io.File;
import java.util.Properties;
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
import bg.znestorov.sobusf24.metro.utils.Utils;

/**
 * Write the MetroDirectionTransfer object to a XML file
 * 
 * @author zanio
 * 
 */
public class WriteScheduleToXMLFile {

	public static void saveToXMLFile(Logger logger, MetroStation ms, Properties coordinatesProp) {

		logger.info("Saving the METRO station to an XML file");

		if (!"XXXX".equals(ms.getNumber())) {
			try {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

				logger.info("Creating the XML structure...");

				// Root elements
				Document doc = docBuilder.newDocument();
				Element station = doc.createElement("Station");
				doc.appendChild(station);

				// Create Status element
				Element status = doc.createElement("Status");
				status.setAttribute("state", "OK");
				station.appendChild(status);

				// Create Number element
				Element number = doc.createElement("Number");
				number.appendChild(doc.createTextNode(ms.getNumber()));
				station.appendChild(number);

				// Create Name element
				Element name = doc.createElement("Name");
				name.appendChild(doc.createTextNode(ms.getName()));
				station.appendChild(name);

				// Create Direction element
				Element direction = doc.createElement("Direction");
				direction.appendChild(doc.createTextNode(formatDirectionName(ms.getDirection())));
				station.appendChild(direction);

				// Create Coordinates element
				Element coordinates = doc.createElement("Coordinates");
				station.appendChild(coordinates);
				String[] coordinatesArray = coordinatesProp.getProperty(ms.getNumber()).split(",");
				Element latitude = doc.createElement("Latitude");
				latitude.appendChild(doc.createTextNode(coordinatesArray[0]));
				coordinates.appendChild(latitude);
				Element longitude = doc.createElement("Longitude");
				longitude.appendChild(doc.createTextNode(coordinatesArray[1]));
				coordinates.appendChild(longitude);

				// Create WEEKDAY schedule
				Element scheduleWeekday = doc.createElement("Schedule");
				scheduleWeekday.setAttribute("day", "Weekday");
				station.appendChild(scheduleWeekday);

				// Create each hour in different element
				for (int i = 4; i <= 24; i++) {
					Element time = doc.createElement("Time");
					time.setAttribute("hour", i + "");
					time.appendChild(doc.createTextNode(Utils.listToString(ms.getWeekdaySchedule().get(i))));
					scheduleWeekday.appendChild(time);
				}

				// Create HOLIDAY schedule
				Element scheduleHoliday = doc.createElement("Schedule");
				scheduleHoliday.setAttribute("day", "Holiday");
				station.appendChild(scheduleHoliday);

				// Create each hour in different element
				for (int i = 4; i <= 24; i++) {
					Element time = doc.createElement("Time");
					time.setAttribute("hour", i + "");
					time.appendChild(doc.createTextNode(Utils.listToString(ms.getHolidaySchedule().get(i))));
					scheduleHoliday.appendChild(time);
				}

				logger.info("Saving the XML structure to a file...");

				// Write the content into XML file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);

				String file = String.format(Constants.METRO_STATION_FILE, ms.getNumber());
				StreamResult result = new StreamResult(new File(file));

				transformer.transform(source, result);
			} catch (ParserConfigurationException pce) {
				logger.warning("ParserConfigurationException: " + pce.toString());
			} catch (TransformerException tfe) {
				logger.warning("TransformerException: " + tfe.toString());
			}
		}
	}

	private static String formatDirectionName(String directionName) {

		if (Utils.isEmpty(directionName)) {
			return directionName;
		}

		if ("м.Витоша-м.Обеля-м.Летище София".equals(directionName) || "м.Витоша-м.Обеля-м.Бизнес Парк".equals(directionName)) {
			directionName = "м.Витоша-м.Обеля-м.Младост 1";
		}

		directionName = directionName.replaceAll("-", " - ");

		return directionName;
	}

}
package bg.znestorov.sofbus24.metro.schedule;

import java.io.File;
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

	public static void saveToXMLFile(Logger logger, MetroStation ms) {

		logger.info("Saving the METRO station to an XML file");

		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			logger.info("Creating the XML structure...");

			// Root elements
			Document doc = docBuilder.newDocument();
			Element station = doc.createElement("Station");
			doc.appendChild(station);

			// Create Number element
			Element number = doc.createElement("Number");
			number.appendChild(doc.createTextNode(ms.getNumber()));
			station.appendChild(number);

			// Create Name element
			Element name = doc.createElement("Name");
			name.appendChild(doc.createTextNode(ms.getName()));
			station.appendChild(name);

			// Create WEEKDAY schedule
			Element scheduleWeekday = doc.createElement("Schedule");
			scheduleWeekday.setAttribute("day", "Weekday");
			station.appendChild(scheduleWeekday);

			// Create each hour in different element
			for (int i = 4; i <= 24; i++) {
				Element time = doc.createElement("Time");
				time.setAttribute("hour", i + "");
				time.appendChild(doc.createTextNode(Utils.listToString(ms
						.getWeekdaySchedule().get(i))));
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
				time.appendChild(doc.createTextNode(Utils.listToString(ms
						.getHolidaySchedule().get(i))));
				scheduleHoliday.appendChild(time);
			}

			logger.info("Saving the XML structure to a file...");

			// Write the content into XML file
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);

			String file = String.format(Constants.METRO_STATION_FILE,
					ms.getNumber());
			StreamResult result = new StreamResult(new File(file));

			transformer.transform(source, result);
		} catch (ParserConfigurationException pce) {
			logger.warning("ParserConfigurationException: " + pce.toString());
		} catch (TransformerException tfe) {
			logger.warning("TransformerException: " + tfe.toString());
		}
	}
}
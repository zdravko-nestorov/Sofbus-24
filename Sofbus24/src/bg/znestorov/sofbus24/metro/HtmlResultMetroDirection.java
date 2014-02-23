package bg.znestorov.sofbus24.metro;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * It is used for parsing the information and filling an object with the
 * directions and all the stations for each of them
 * 
 * @author zanio
 * 
 */
public class HtmlResultMetroDirection {

	public static MetroDirectionTransfer getMetroDirections(String scheduleXml) {
		MetroDirectionTransfer mdt = new MetroDirectionTransfer();

		try {
			InputStream is = new ByteArrayInputStream(Charset.forName("UTF-8")
					.encode(scheduleXml).array());
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(is);
			doc.getDocumentElement().normalize();

			// Get all directions from the XML file
			NodeList directionsList = doc.getElementsByTagName("Direction");
			for (int i = 0; i < directionsList.getLength(); i++) {
				Node directionNode = directionsList.item(i);
				Element directionElement = (Element) directionNode;

				// Get the ID and NAME of the direction
				String directionId = directionElement.getAttribute("id");
				String directionName = directionElement
						.getElementsByTagName("Name").item(0).getTextContent();
				MetroDirection md = new MetroDirection(directionId,
						directionName);

				// Get all stations of this direction
				NodeList stationsList = directionElement
						.getElementsByTagName("Station");
				for (int j = 0; j < stationsList.getLength(); j++) {
					Node stationNode = stationsList.item(i);
					Element stationElement = (Element) stationNode;

					// Get the NUMBER, NAME and URL of the station
					String stationNumber = stationElement
							.getAttribute("number");
					String stationName = stationElement
							.getElementsByTagName("Name").item(0)
							.getTextContent();
					String stationUrl = stationElement
							.getElementsByTagName("StationScheduleURL").item(0)
							.getTextContent();

					// Add the station to the list of current direction
					md.addStation(stationNumber, stationName, stationUrl);
				}

				mdt.addMetroDirection(md);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mdt;
	}

}
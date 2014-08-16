package bg.znestorov.sofbus24.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import bg.znestorov.sofbus24.utils.WorkingDaysCalendar;

/**
 * Class representing each station of the metro (add direction and schedule)
 * 
 * @author Zdravko Nestorov
 * 
 */
public class MetroStationEntity extends StationEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private String direction;
	private HashMap<Integer, ArrayList<String>> holidaySchedule;
	private HashMap<Integer, ArrayList<String>> weekdaySchedule;

	public MetroStationEntity() {
		super();

		this.holidaySchedule = new LinkedHashMap<Integer, ArrayList<String>>();
		this.weekdaySchedule = new LinkedHashMap<Integer, ArrayList<String>>();

		for (int i = 4; i <= 24; i++) {
			this.holidaySchedule.put(i, new ArrayList<String>());
			this.weekdaySchedule.put(i, new ArrayList<String>());
		}
	}

	public MetroStationEntity(StationEntity station) {
		super(station.getNumber(), station.getName(), station.getLat(), station
				.getLon(), station.getType(), station.getCustomField());

		this.holidaySchedule = new LinkedHashMap<Integer, ArrayList<String>>();
		this.weekdaySchedule = new LinkedHashMap<Integer, ArrayList<String>>();

		for (int i = 4; i <= 24; i++) {
			this.holidaySchedule.put(i, new ArrayList<String>());
			this.weekdaySchedule.put(i, new ArrayList<String>());
		}
	}

	public MetroStationEntity(StationEntity station, String direction) {
		super(station.getNumber(), station.getName(), station.getLat(), station
				.getLon(), station.getType(), station.getCustomField());

		this.direction = direction;
		this.holidaySchedule = new LinkedHashMap<Integer, ArrayList<String>>();
		this.weekdaySchedule = new LinkedHashMap<Integer, ArrayList<String>>();

		for (int i = 4; i <= 24; i++) {
			this.holidaySchedule.put(i, new ArrayList<String>());
			this.weekdaySchedule.put(i, new ArrayList<String>());
		}
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public void setDirection(Document docStationSchedule) {
		String direction = "";

		try {
			XPath xpath = XPathFactory.newInstance().newXPath();
			Node directionNode = (Node) xpath.evaluate(
					"Station/Direction/text()", docStationSchedule,
					XPathConstants.NODE);
			direction = directionNode.getTextContent();
		} catch (Exception e) {
		}

		this.direction = direction;
	}

	public HashMap<Integer, ArrayList<String>> getHolidaySchedule() {
		return holidaySchedule;
	}

	public void setHolidaySchedule(
			HashMap<Integer, ArrayList<String>> holidaySchedule) {
		this.holidaySchedule = holidaySchedule;
	}

	public void setHolidaySchedule(Document docStationSchedule) {
		setSchedule(docStationSchedule, "Station/Schedule[@day='Holiday']/Time");
	}

	public HashMap<Integer, ArrayList<String>> getWeekdaySchedule() {
		return weekdaySchedule;
	}

	public void setWeekdaySchedule(
			HashMap<Integer, ArrayList<String>> weekdaySchedule) {
		this.weekdaySchedule = weekdaySchedule;
	}

	public void setWeekdaySchedule(Document docStationSchedule) {
		setSchedule(docStationSchedule, "Station/Schedule[@day='Weekday']/Time");
	}

	public HashMap<Integer, ArrayList<String>> getSchedule() {
		Calendar calendar = Calendar.getInstance();
		if (WorkingDaysCalendar.isWorkingDay(calendar)) {
			return weekdaySchedule;
		} else {
			return holidaySchedule;
		}
	}

	/**
	 * Get the schedule from the XML file and set it to a HashMap schedule via
	 * the xPath expression
	 * 
	 * @param docStationSchedule
	 *            the station schedule taken from an URL address
	 * @param xPathExpression
	 *            the xPath expression, pointing to the correct schedule
	 *            (Weekday or Holiday)
	 */
	private void setSchedule(Document docStationSchedule, String xPathExpression) {
		XPath xpath = XPathFactory.newInstance().newXPath();

		try {
			NodeList timeNodes = (NodeList) xpath.evaluate(xPathExpression,
					docStationSchedule, XPathConstants.NODESET);

			for (int i = 0; i < timeNodes.getLength(); i++) {
				Node timeNode = timeNodes.item(i);

				if (timeNode.getNodeType() == Node.ELEMENT_NODE) {
					Element timeElement = (Element) timeNode;

					int hour = Integer.parseInt(timeElement
							.getAttribute("hour"));
					ArrayList<String> hourSchedule = new ArrayList<String>();

					if (timeElement.getChildNodes().getLength() > 0) {
						String timeSchedule = timeElement.getChildNodes()
								.item(0).getNodeValue();
						hourSchedule.addAll(Arrays.asList(timeSchedule
								.split(",")));
					}

					if (xPathExpression.contains("Weekday")) {
						weekdaySchedule.put(hour, hourSchedule);
					} else {
						holidaySchedule.put(hour, hourSchedule);
					}
				}
			}
		} catch (Exception e) {
		}
	}

	/**
	 * Check if the schedule is set to the MetroStation object
	 * 
	 * @return if the object is filled with the time schedule
	 */
	public boolean isScheduleSet() {
		boolean result = false;

		for (int i = 4; i <= 24; i++) {
			if (!this.holidaySchedule.get(i).isEmpty()
					&& !this.weekdaySchedule.get(i).isEmpty()) {
				result = true;
				break;
			}
		}

		return result;
	}

	@Override
	public String toString() {
		return getClass().getName() + " {\n\tdirection: " + direction
				+ "\n\tholidaySchedule: " + holidaySchedule
				+ "\n\tweekdaySchedule: " + weekdaySchedule + "\n}";
	}
}

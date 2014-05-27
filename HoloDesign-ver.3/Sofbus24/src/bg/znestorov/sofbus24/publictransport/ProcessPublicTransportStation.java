package bg.znestorov.sofbus24.publictransport;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import bg.znestorov.sofbus24.entity.PublicTransportStation;
import bg.znestorov.sofbus24.utils.Constants;

/**
 * Used to process the information from SKGT site, using REGEX and set it to a
 * PublicTransportStation entity
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class ProcessPublicTransportStation {

	private Activity context;
	private PublicTransportStation ptStation;
	private String htmlResult;

	public ProcessPublicTransportStation(Activity context,
			PublicTransportStation ptStation, String htmlResult) {
		this.context = context;
		this.ptStation = ptStation;
		this.htmlResult = htmlResult;
	}

	public Activity getContext() {
		return context;
	}

	public void setContext(Activity context) {
		this.context = context;
	}

	public PublicTransportStation getPtStation() {
		return ptStation;
	}

	public void setPtStation(PublicTransportStation ptStation) {
		this.ptStation = ptStation;
	}

	public String getHtmlResult() {
		return htmlResult;
	}

	public void setHtmlResult(String htmlResult) {
		this.htmlResult = htmlResult;
	}

	/**
	 * Get the schedule for the selected station from SKGT site and parse it to
	 * a PublicTransportStation object
	 * 
	 * @return PublicTransportStation object with schedule set
	 */
	public PublicTransportStation getStationFromHtml() {
		Pattern pattern = Pattern.compile(Constants.SCHECULE_REGEX_STATION_SCHEDULE);
		Matcher matcher = pattern.matcher(htmlResult);

		while (matcher.find()) {
			try {
				String[] timeScheduleArray = matcher.group(2).trim().split(":");

				if (timeScheduleArray.length == 3) {
					// Check if the schedule is set for 00 or 0, and replace it
					// with 24 (this is needed because the whole algorithm is
					// created this way)
					if ("00".equals(timeScheduleArray[0])
							|| "0".equals(timeScheduleArray[0])) {
						timeScheduleArray[0] = "24";
					}

					// Get the time schedule and hour
					String timeSchedule = timeScheduleArray[0] + ":"
							+ timeScheduleArray[1];
					int hourSchedule = Integer.parseInt(timeScheduleArray[0]);

					// Set the schedule to the station
					ptStation.setScheduleHour(hourSchedule, timeSchedule);
				}
			} catch (Exception e) {
			}
		}

		return ptStation;
	}
}

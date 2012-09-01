package com.example.info_station;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Getting the times of the HtmlRequestStation
public class HtmlResultStation {

	// Checking the HtmlResult and filling the times in ArrayList<String>
	public static String showResult(String htmlResult) {
		ArrayList<String> time_stamp = null;
		
		if (htmlResult != null && !"".equals(htmlResult)) {
			time_stamp = new ArrayList<String>();
			Pattern pattern = Pattern.compile("(\\d{2}):(\\d{2})");
			Matcher matcher = pattern.matcher(htmlResult);
			
			Calendar c = Calendar.getInstance(); 
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			
			while (matcher.find()) {
				int station_hour = Integer.parseInt(matcher.group(1));
				int station_minute = Integer.parseInt(matcher.group(2));
				
				if (station_hour > hour) {
					time_stamp.add(matcher.group());
				} else if (station_hour == hour && station_minute >= minute) {
					time_stamp.add(matcher.group());
				}
				
			}
			Collections.sort(time_stamp);
			
			return time_stamp.toString();
		}
		
		return null;
	}

}

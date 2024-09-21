package bg.znestorov.sofbus24.apidb.utils;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class UtilsDuration {

  public static long getTime() {
    return System.currentTimeMillis();
  }

  static String getDate() {
    Date date = new Date();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    return simpleDateFormat.format(date);
  }

  public static String getFullDate() {
    return ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
  }

  public static long getDurationSec(long startTime) {
    return (getTime() - startTime) / 1000;
  }

}

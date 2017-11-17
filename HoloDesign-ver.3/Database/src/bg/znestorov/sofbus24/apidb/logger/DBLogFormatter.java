package bg.znestorov.sofbus24.apidb.logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import static bg.znestorov.sofbus24.apidb.utils.Constants.LINE_SEPARATOR;

public class DBLogFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {

        StringBuilder sb = new StringBuilder();
        sb.append(new Date(record.getMillis()))
                .append(" ")
                .append(record.getLevel().getLocalizedName())
                .append(": ")
                .append(formatMessage(record))
                .append(LINE_SEPARATOR);

        if (record.getThrown() != null) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                sb.append(sw.toString());
            } catch (Exception ex) {
                // Never should go inside
            }
        }

        return sb.toString();
    }

}
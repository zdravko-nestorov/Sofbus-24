package bg.znestorov.sofbus24.apidb.logger;

import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static bg.znestorov.sofbus24.apidb.utils.Constants.DB_LOG_FILE;
import static bg.znestorov.sofbus24.apidb.utils.Constants.SOFBUS_LOGGER;
import static bg.znestorov.sofbus24.apidb.utils.UtilsDuration.getDurationSec;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;

public class DBLogger {

    private static volatile Logger logger = null;

    private static Logger getDbLogger() {

        // Lazy Initialization - if required then only
        if (logger == null) {

            // Thread Safe - might be costly operation in some case
            synchronized (DBLogger.class) {
                if (logger == null) {
                    logger = Logger.getLogger(SOFBUS_LOGGER);
                    logger.setUseParentHandlers(false);

                    try {
                        DBLogFormatter formatter = new DBLogFormatter();

                        // Create the FileHandler and set its params
                        FileHandler fh = new FileHandler(DB_LOG_FILE);
                        fh.setLevel(Level.ALL);
                        fh.setFormatter(formatter);
                        logger.addHandler(fh);

                        // Create the FileHandler and set its params
                        ConsoleHandler ch = new ConsoleHandler();
                        ch.setLevel(Level.ALL);
                        ch.setFormatter(formatter);
                        logger.addHandler(ch);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return logger;
    }

    public static void log(Level level, String msg) {
        getDbLogger().log(level, msg);
    }

    public static void logInfo(String msg) {
        getDbLogger().log(INFO, msg);
    }

    public static void logWarning(String msg) {
        getDbLogger().log(WARNING, msg);
    }

    public static void logSevere(String msg) {
        getDbLogger().log(SEVERE, msg);
    }

    public static void logDuration(String msg, long startTime) {
        logInfo(msg + getDurationSec(startTime) + " seconds...");
    }

}
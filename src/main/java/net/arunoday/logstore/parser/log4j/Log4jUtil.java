package net.arunoday.logstore.parser.log4j;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.arunoday.logstore.LogRecord;

import org.apache.log4j.spi.LoggingEvent;

public class Log4jUtil {

  private static final Logger LOGGER = Logger.getLogger(Log4jUtil.class.getName());

  public static LogRecord translateLog4j(LoggingEvent event) {
    LogRecord ld = new LogRecord();
    ld.setDate(new Date(event.getTimeStamp()));
    StringBuilder sb = new StringBuilder();
    sb.append(event.getMessage());
    if (event.getThrowableInformation() != null) {
      String[] throwableStrRep = event.getThrowableInformation().getThrowableStrRep();
      for (String string : throwableStrRep) {
        sb.append('\n');
        sb.append(string);
      }
    }
    ld.setMessage(sb.toString().trim());

    ld.setLevel(event.getLevel().toString());
    ld.setClazz(event.getLocationInformation().getClassName());
    ld.setMethod(event.getLocationInformation().getMethodName());
    ld.setFile(event.getLocationInformation().getFileName());
    ld.setLine(event.getLocationInformation().getLineNumber());
    ld.setNDC(event.getNDC());
    ld.setThread(event.getThreadName());
    ld.setLoggerName(event.getLoggerName());

    return ld;
  }

  public static Level parseLevel(String s) {
    if (s.equalsIgnoreCase("INFO")) {
      return Level.INFO;
    } else if (s.equalsIgnoreCase("ERROR") || s.equalsIgnoreCase("FATAL")) {
      return Level.SEVERE;
    } else if (s.equalsIgnoreCase("WARN")) {
      return Level.WARNING;
    } else if (s.equalsIgnoreCase("DEBUG")) {
      return Level.FINE;
    } else if (s.equalsIgnoreCase("TRACE")) {
      return Level.FINEST;
    }
    LOGGER.severe("Level \"" + s + "\" not parsed!");
    return null;

  }

}

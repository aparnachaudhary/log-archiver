package net.arunoday.logstore.parser.log4j;

import java.util.Date;

import net.arunoday.logstore.domain.LogRecord;

import org.apache.log4j.spi.LoggingEvent;

/**
 * @author Aparna Chaudhary
 * 
 */
public class Log4jUtil {

	/**
	 * @param event
	 * @return
	 */
	public static LogRecord translateLog4j(LoggingEvent event) {
		LogRecord ld = new LogRecord();
		ld.setDate(new Date(event.getTimeStamp()));
		StringBuilder sb = new StringBuilder();
		sb.append(event.getMessage());
		if (event.getThrowableInformation() != null) {
			String[] throwableStrRep = event.getThrowableInformation()
					.getThrowableStrRep();
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

}

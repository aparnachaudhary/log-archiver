package net.arunoday.logstore;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import net.arunoday.logview.LogRecord;
import net.arunoday.logview.LogRecordCollector;
import net.arunoday.logview.importer.LogImporterUsingParser;
import net.arunoday.logview.parser.ParsingContext;
import net.arunoday.logview.parser.log4j.Log4jPatternMultilineLogParser;
import net.arunoday.logview.reader.ProxyLogDataCollector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author aparnachaudhary
 */
@Component
public class LogProcessor {

	@Autowired
	LogRecordRepository logRecordRepository;

	public static final String PROPERTY_NAME = "name";
	public static final String PROPERTY_PATTERN = "pattern";
	public static final String PROPERTY_DATE_FORMAT = "dateFormat";
	public static final String PROPERTY_CUSTOM_LEVELS = "customLevels";
	public static final String PROPERTY_DESCRIPTION = "description";
	public static final String PROPERTY_TYPE = "type";
	public static final String PROPERTY_CHARSET = "charset";

	public File process(File file) throws Exception {
		System.err.println("File Data: " + file.getName());
		parseFile(file);
		return file;
	}

	private void parseFile(File file) {
		try {
			InputStream in = new FileInputStream(file);
			System.out.println("Available: " + in.available());
			Properties p = new Properties();
			// TIMESTAMP LEVEL [THREAD] CLASS (FILE:LINE) - MESSAGE
			// %d{yyyy-MM-dd HH:mm:ss,SSS} %-5p [%t] %m%n
			p.setProperty(PROPERTY_TYPE, "log4j");
			p.setProperty(PROPERTY_PATTERN, "TIMESTAMP LEVEL [THREAD] MESSAGE");
			p.setProperty(PROPERTY_DATE_FORMAT, "yyyy-MM-dd HH:mm:ss,SSS");
			p.setProperty(PROPERTY_NAME,
					"Lo4j-pattern: %d{ISO8601} %-5p [%t] %m%n");

			Log4jPatternMultilineLogParser parser = new Log4jPatternMultilineLogParser();

			LogRecordCollector collector = new ProxyLogDataCollector();
			ParsingContext context = new ParsingContext();
			LogImporterUsingParser importerUsingParser = new LogImporterUsingParser(
					parser);
			importerUsingParser.init(p);
			importerUsingParser.initParsingContext(context);
			importerUsingParser.importLogs(in, collector, context);
			LogRecord[] logDatas = collector.getLogRecords();
			System.err.println("Have: " + logDatas.length);
			for (LogRecord record : logDatas) {
				System.err.println("logData: " + record.getId() + "|"
						+ record.getThread() + "|" + record.getDate() + "|"
						+ record.getLevel() + "|\"" + record.getMessage()
						+ "\"");
				logRecordRepository.run(record);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

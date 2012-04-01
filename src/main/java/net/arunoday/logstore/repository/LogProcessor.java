package net.arunoday.logstore.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import net.arunoday.logstore.domain.LogRecord;
import net.arunoday.logstore.importer.LogImporterUsingParser;
import net.arunoday.logstore.parser.ParsingContext;
import net.arunoday.logstore.parser.log4j.Log4jPatternMultilineLogParser;
import net.arunoday.logstore.reader.LogRecordCollector;
import net.arunoday.logstore.reader.ProxyLogDataCollector;

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
			LogRecord[] logRecords = collectLogRecords(file);
			logRecordRepository.createCollection();
			for (LogRecord record : logRecords) {
				// System.err.println("record: " + record.getId() + "|"
				// + record.getThread() + "|" + record.getDate() + "|"
				// + record.getLevel() + "|\"" + record.getMessage()
				// + "\"");
				logRecordRepository.save(record);
			}
			logRecordRepository.findAll();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private LogRecord[] collectLogRecords(File file)
			throws FileNotFoundException, Exception {
		InputStream in = new FileInputStream(file);
		Properties p = new Properties();
		// TIMESTAMP LEVEL [THREAD] CLASS (FILE:LINE) - MESSAGE
		// %d{yyyy-MM-dd HH:mm:ss,SSS} %-5p [%t] %m%n
		p.setProperty(PROPERTY_TYPE, "log4j");
		p.setProperty(PROPERTY_PATTERN, "TIMESTAMP LEVEL [THREAD] MESSAGE");
		p.setProperty(PROPERTY_DATE_FORMAT, "yyyy-MM-dd HH:mm:ss,SSS");
		p.setProperty(PROPERTY_NAME, "Lo4j-pattern: %d{ISO8601} %-5p [%t] %m%n");

		Log4jPatternMultilineLogParser parser = new Log4jPatternMultilineLogParser();
		LogRecordCollector collector = new ProxyLogDataCollector();
		ParsingContext context = new ParsingContext();
		LogImporterUsingParser importerUsingParser = new LogImporterUsingParser(
				parser);
		importerUsingParser.init(p);
		importerUsingParser.initParsingContext(context);
		importerUsingParser.importLogs(in, collector, context);
		LogRecord[] logRecords = collector.getLogRecords();
		return logRecords;
	}
}

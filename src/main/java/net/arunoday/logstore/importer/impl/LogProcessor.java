package net.arunoday.logstore.importer.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import net.arunoday.logstore.domain.LogRecord;
import net.arunoday.logstore.parser.ParsingContext;
import net.arunoday.logstore.repository.LogRecordRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Aparna Chaudhary
 * 
 */
@Component
public class LogProcessor {

	@Autowired
	LogRecordRepository logRecordRepository;

	@Autowired
	LogImporterUsingParser logImporterUsingParser;

	/**
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public File process(File file) throws Exception {
		System.err.println("File Data: " + file.getName());
		try {
			LogRecord[] logRecords = collectLogRecords(file);

			logRecordRepository.createCollection();
			for (LogRecord record : logRecords) {
				logRecordRepository.save(record);
			}
			logRecordRepository.findAll();
			// logRecordRepository.dropCollection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws Exception
	 */
	private LogRecord[] collectLogRecords(File file)
			throws FileNotFoundException, Exception {
		InputStream in = new FileInputStream(file);
		ParsingContext context = new ParsingContext();
		logImporterUsingParser.initParsingContext(context);
		logImporterUsingParser.importLogs(in, context);
		LogRecord[] logRecords = logImporterUsingParser.getDataCollector()
				.getLogRecords();
		return logRecords;
	}

}

package net.arunoday.logstore.importer.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import net.arunoday.logstore.domain.LogRecord;
import net.arunoday.logstore.importer.LogImporter;
import net.arunoday.logstore.parser.LogParser;
import net.arunoday.logstore.parser.MultiLineLogParser;
import net.arunoday.logstore.parser.ParserDescription;
import net.arunoday.logstore.parser.ParsingContext;
import net.arunoday.logstore.reader.LogRecordCollector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Aparna Chaudhary
 * 
 */
@Component
public class LogImporterUsingParser implements LogImporter {

	private static final Logger LOGGER = Logger
			.getLogger(LogImporterUsingParser.class.getName());
	@Autowired
	LogParser parser;

	@Autowired
	LogRecordCollector dataCollector;

	private ParserDescription pd;

	public LogImporterUsingParser() {
		super();
		// this.parser = parser;
	}

	@PostConstruct
	public void init() {
		pd = parser.getParserDescription();
	}

	@Override
	public void importLogs(InputStream in, ParsingContext parsingContext) {
		LOGGER.finest("Log import started ");
		String line = null;
		LogRecord logRecord = null;
		String charset = parser.getParserDescription().getCharset();

		BufferedReader logReader = null;
		if (charset == null) {
			logReader = new BufferedReader(new InputStreamReader(in));
		} else {
			try {
				logReader = new BufferedReader(new InputStreamReader(in,
						charset));
			} catch (UnsupportedEncodingException e1) {
				LOGGER.severe(String.format(
						"Requiered charset [%s] is not supported: %s", charset,
						e1.getMessage()));
				LOGGER.info(String.format("Using default charset: %s", Charset
						.defaultCharset().displayName()));
				logReader = new BufferedReader(new InputStreamReader(in));
			}

		}
		while (true) {
			try {
				line = logReader.readLine();
				if (line == null) {
					break;
				}

				if (parser instanceof MultiLineLogParser) {
					synchronized (parsingContext) {
						logRecord = parser.parse(line, parsingContext);
					}
				} else {
					logRecord = parser.parse(line, parsingContext);
				}

				if (logRecord != null) {
					logRecord.setLogSource(parsingContext.getLogSource());
					dataCollector.add(logRecord);
					parsingContext.setLastParsed(System.currentTimeMillis());
				}

			} catch (IOException e) {
				e.printStackTrace();
				LOGGER.severe(String.format(
						"IOException during log import (file %s): %s",
						parsingContext.getLogSource(), e.getMessage()));
				break;
			} catch (ParseException e) {
				LOGGER.severe(String.format(
						"ParseEception during log import (file %s): %s",
						parsingContext.getLogSource(), e.getMessage()));
				e.printStackTrace();
				break;
			}
		}

		try {
			if (parser instanceof MultiLineLogParser) {
				MultiLineLogParser multiLineLogParser = (MultiLineLogParser) parser;
				logRecord = multiLineLogParser.parseBuffer(parsingContext);
				if (logRecord != null) {
					logRecord.setLogSource(parsingContext.getLogSource());
					synchronized (parsingContext) {
						dataCollector.add(logRecord);
					}
					parsingContext.setLastParsed(System.currentTimeMillis());
				}
			}
		} catch (Exception e) {
			LOGGER.info("Cannot parser rest of buffer, probablly stopped importing");
		}

		LOGGER.finest("Log import finished!");
	}

	@Override
	public String getKeyStrokeAccelelator() {
		return pd.getKeyStrokeAccelelator();
	}

	@Override
	public int getMnemonic() {
		return pd.getMenmonic();
	}

	@Override
	public boolean equals(Object obj) {
		return parser.equals(obj);
	}

	@Override
	public int hashCode() {
		return parser.hashCode();
	}

	public LogParser getParser() {
		return parser;
	}

	@Override
	public void initParsingContext(ParsingContext parsingContext) {
		parser.initParsingContext(parsingContext);
	}

	public LogRecordCollector getDataCollector() {
		return dataCollector;
	}

}

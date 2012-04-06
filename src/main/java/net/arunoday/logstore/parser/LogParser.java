package net.arunoday.logstore.parser;

import java.text.ParseException;

import net.arunoday.logstore.domain.LogRecord;

/**
 * @author Aparna Chaudhary
 * 
 */
public interface LogParser {

	public static int LOG_PARSER_VERSION_1 = 1;

	/**
	 * @param parsingContext
	 */
	public void initParsingContext(ParsingContext parsingContext);

	/**
	 * @param line
	 * @param parsingContext
	 * @return
	 * @throws ParseException
	 */
	public LogRecord parse(String line, ParsingContext parsingContext)
			throws ParseException;

	/**
	 * @return
	 */
	public ParserDescription getParserDescription();

	/**
	 * @return
	 */
	public int getVersion();
}

package net.arunoday.logstore.parser;

import java.text.ParseException;

import net.arunoday.logstore.domain.LogRecord;

/**
 * @author Aparna Chaudhary
 */
public interface MultiLineLogParser extends LogParser {

	/**
	 * @param parsingContext
	 * @return
	 * @throws ParseException
	 */
	public abstract LogRecord parseBuffer(ParsingContext parsingContext)
			throws ParseException;

}

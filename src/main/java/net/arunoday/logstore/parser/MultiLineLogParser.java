package net.arunoday.logstore.parser;

import java.text.ParseException;

import net.arunoday.logstore.domain.LogRecord;


public interface MultiLineLogParser extends LogParser {

  public abstract LogRecord parseBuffer(ParsingContext parsingContext) throws ParseException;

}

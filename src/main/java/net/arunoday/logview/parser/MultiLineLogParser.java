package net.arunoday.logview.parser;

import java.text.ParseException;

import net.arunoday.logview.LogRecord;


public interface MultiLineLogParser extends LogParser {

  public abstract LogRecord parseBuffer(ParsingContext parsingContext) throws ParseException;

}

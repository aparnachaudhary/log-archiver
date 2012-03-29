package net.arunoday.logview.parser;

import java.text.ParseException;
import java.util.Properties;

import net.arunoday.logview.LogRecord;


public interface LogParser {

  public static int LOG_PARSER_VERSION_1 = 1;

  public void init(Properties properties) throws Exception;

  public void initParsingContext(ParsingContext parsingContext);

  public LogRecord parse(String line, ParsingContext parsingContext) throws ParseException;

  public ParserDescription getParserDescription();

  public int getVersion();
}

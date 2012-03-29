package net.arunoday.logview.importer;

import java.io.InputStream;
import java.util.Properties;

import javax.swing.Icon;

import net.arunoday.logview.LogRecordCollector;
import net.arunoday.logview.parser.ParsingContext;


public interface LogImporter {

  public static final int LOG_IMPORTER_VERSION_1 = 1;

  public final String PARSER_CLASS = "parser.class";

  public final String PARSER_DISPLAYABLE_NAME = "parser.displayableName";
  public final String PARSER_MNEMONIC = "parser.mnemonic";
  public final String PARSER_KEY_STROKE_ACCELELATOR = "parser.keyStrokeAccelelator";
  public final String PARSER_ICON = "parser.icon";

  public void init(Properties properties) throws Exception;

  /**
   * Initialize parsing context specific resources, which are not thread safe (i.e. DateFormat)
   * 
   * @param parsingContext
   */
  public void initParsingContext(ParsingContext parsingContext);

  public void importLogs(InputStream in, LogRecordCollector dataCollector, ParsingContext parsingContext);

  public String getKeyStrokeAccelelator();

  public int getMnemonic();

  public Icon getIcon();
}

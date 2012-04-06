package net.arunoday.logstore.importer;

import java.io.InputStream;

import net.arunoday.logstore.parser.ParsingContext;

/**
 * @author Aparna Chaudhary
 */
public interface LogImporter {

	public static final int LOG_IMPORTER_VERSION_1 = 1;
	public final String PARSER_CLASS = "parser.class";
	public final String PARSER_DISPLAYABLE_NAME = "parser.displayableName";
	public final String PARSER_MNEMONIC = "parser.mnemonic";
	public final String PARSER_KEY_STROKE_ACCELELATOR = "parser.keyStrokeAccelelator";

	/**
	 * Initialize parsing context specific resources, which are not thread safe
	 * (i.e. DateFormat)
	 * 
	 * @param parsingContext
	 */
	public void initParsingContext(ParsingContext parsingContext);

	/**
	 * @param in
	 * @param parsingContext
	 */
	public void importLogs(InputStream in, ParsingContext parsingContext);

	/**
	 * @return
	 */
	public String getKeyStrokeAccelelator();

	/**
	 * @return
	 */
	public int getMnemonic();

}

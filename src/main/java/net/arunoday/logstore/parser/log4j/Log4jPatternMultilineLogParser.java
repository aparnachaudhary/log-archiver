package net.arunoday.logstore.parser.log4j;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.annotation.PostConstruct;

import net.arunoday.logstore.domain.LogRecord;
import net.arunoday.logstore.parser.MultiLineLogParser;
import net.arunoday.logstore.parser.ParserDescription;
import net.arunoday.logstore.parser.ParsingContext;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * LogFilePatternReceiver can parse and tail log files, converting entries into
 * LoggingEvents. If the file doesn't exist when the receiver is initialized,
 * the receiver will look for the file once every 10 seconds.
 * <p>
 * This receiver relies on java.util.regex features to perform the parsing of
 * text in the log file, however the only regular expression field explicitly
 * supported is a glob-style wildcard used to ignore fields in the log file if
 * needed. All other fields are parsed by using the supplied keywords.
 * <p>
 * <b>Features:</b><br>
 * - specify the URL of the log file to be processed<br>
 * - specify the timestamp format in the file (if one exists, using patterns
 * from {@link java.text.SimpleDateFormat})<br>
 * - specify the pattern (logFormat) used in the log file using keywords, a
 * wildcard character (*) and fixed text<br>
 * - 'tail' the file (allows the contents of the file to be continually read and
 * new events processed)<br>
 * - specify custom charset (default UTF-8) - supports the parsing of multi-line
 * messages and exceptions - 'hostname' property set to URL host (or 'file' if
 * not available) - 'application' property set to URL path (or value of fileURL
 * if not available)
 * <p>
 * <b>Keywords:</b><br>
 * TIMESTAMP<br>
 * LOGGER<br>
 * LEVEL<br>
 * THREAD<br>
 * CLASS<br>
 * FILE<br>
 * LINE<br>
 * METHOD<br>
 * RELATIVETIME<br>
 * MESSAGE<br>
 * NDC<br>
 * PROP(key)<br>
 * <p>
 * Use a * to ignore portions of the log format that should be ignored
 * <p>
 * Example:<br>
 * If your file's patternlayout is this:<br>
 * <b>%d %-5p [%t] %C{2} (%F:%L) - %m%n</b>
 * <p>
 * specify this as the log format:<br>
 * <b>TIMESTAMP LEVEL [THREAD] CLASS (FILE:LINE) - MESSAGE</b>
 * <p>
 * To define a PROPERTY field, use PROP(key)
 * <p>
 * Example:<br>
 * If you used the RELATIVETIME pattern layout character in the file, you can
 * use PROP(RELATIVETIME) in the logFormat definition to assign the RELATIVETIME
 * field as a property on the event.
 * <p>
 * If your file's patternlayout is this:<br>
 * <b>%r [%t] %-5p %c %x - %m%n</b>
 * <p>
 * specify this as the log format:<br>
 * <b>PROP(RELATIVETIME) [THREAD] LEVEL LOGGER * - MESSAGE</b>
 * <p>
 * Note the * - it can be used to ignore a single word or sequence of words in
 * the log file (in order for the wildcard to ignore a sequence of words, the
 * text being ignored must be followed by some delimiter, like '-' or '[') - ndc
 * is being ignored in the following example.
 * <p>
 * Assign a filterExpression in order to only process events which match a
 * filter. If a filterExpression is not assigned, all events are processed.
 * <p>
 * <b>Limitations:</b><br>
 * - no support for the single-line version of throwable supported by
 * patternlayout<br>
 * (this version of throwable will be included as the last line of the message)<br>
 * - the relativetime patternLayout character must be set as a property:
 * PROP(RELATIVETIME)<br>
 * - messages should appear as the last field of the logFormat because the
 * variability in message content<br>
 * - exceptions are converted if the exception stack trace (other than the first
 * line of the exception)<br>
 * is stored in the log file with a tab followed by the word 'at' as the first
 * characters in the line<br>
 * - tailing may fail if the file rolls over.
 * <p>
 * <b>Example receiver configuration settings</b> (add these as params,
 * specifying a LogFilePatternReceiver 'plugin'):<br>
 * param: "timestampFormat" value="yyyy-MM-d HH:mm:ss,SSS"<br>
 * param: "logFormat"
 * value="PROP(RELATIVETIME) [THREAD] LEVEL LOGGER * - MESSAGE"<br>
 * param: "fileURL" value="file:///c:/events.log"<br>
 * param: "tailing" value="true"
 * <p>
 * This configuration will be able to process these sample events:<br>
 * 710 [ Thread-0] DEBUG first.logger first - <test> <test2>something
 * here</test2> <test3 blah=something/> <test4> <test5>something else</test5>
 * </test4></test> <br>
 * 880 [ Thread-2] DEBUG first.logger third - <test> <test2>something
 * here</test2> <test3 blah=something/> <test4> <test5>something else</test5>
 * </test4></test> <br>
 * 880 [ Thread-0] INFO first.logger first - infomsg-0<br>
 * java.lang.Exception: someexception-first<br>
 * at Generator2.run(Generator2.java:102)<br>
 * 
 * @author Code highly based on
 *         http://svn.apache.org/repos/asf/logging/log4j/companions
 *         /receivers/trunk
 *         /src/main/java/org/apache/log4j/varia/LogFilePatternReceiver.java
 */
@Component("log4jPatternMultilineLogParser")
public class Log4jPatternMultilineLogParser implements MultiLineLogParser {

	private static final java.util.logging.Logger LOG = java.util.logging.Logger
			.getLogger(Log4jPatternMultilineLogParser.class.getName());

	@Autowired
	ParserDescription parserDescription;

	private final List<String> keywords = new ArrayList<String>();
	private String[] emptyException = new String[] { "" };
	private Map<String, Level> customLevelDefinitionMap = new HashMap<String, Level>();
	private boolean appendNonMatches;
	private List<String> matchingKeywords = new ArrayList<String>();

	private static final String PROP_START = "PROP(";
	private static final String PROP_END = ")";

	protected static final String PROPERTY_LOG_EVENT_PROPERTIES = "Log4jPatternMultilineLogParser.logEventProperties";
	protected static final String LOGGER = "LOGGER";
	protected static final String MESSAGE = "MESSAGE";
	protected static final String TIMESTAMP = "TIMESTAMP";
	protected static final String NDC = "NDC";
	protected static final String LEVEL = "LEVEL";
	protected static final String THREAD = "THREAD";
	protected static final String CLASS = "CLASS";
	protected static final String FILE = "FILE";
	protected static final String LINE = "LINE";
	protected static final String METHOD = "METHOD";

	// all lines other than first line of exception begin with tab followed by
	// 'at' followed by text
	private static final String EXCEPTION_PATTERN = "^\\s+at.*";
	private static final String REGEXP_DEFAULT_WILDCARD = ".*?";
	private static final String REGEXP_GREEDY_WILDCARD = ".*";
	private static final String PATTERN_WILDCARD = "*";
	private static final String NOSPACE_GROUP = "(\\S*\\s*?)";
	private static final String DEFAULT_GROUP = "(" + REGEXP_DEFAULT_WILDCARD
			+ ")";
	private static final String GREEDY_GROUP = "(" + REGEXP_GREEDY_WILDCARD
			+ ")";
	private static final String MULTIPLE_SPACES_REGEXP = "[ ]+";
	private final String newLine = System.getProperty("line.separator");

	private static final String VALID_DATEFORMAT_CHARS = "GyMwWDdFEaHkKhmsSzZ";
	private static final String VALID_DATEFORMAT_CHAR_PATTERN = "["
			+ VALID_DATEFORMAT_CHARS + "]";

	private String timestampFormat = "yyyy-MM-d HH:mm:ss,SSS";
	private String logFormat;
	private String customLevelDefinitions;
	private String regexp;
	private Pattern regexpPattern;
	private Pattern exceptionPattern;
	private String timestampPatternText;

	public Log4jPatternMultilineLogParser() {
		keywords.add(TIMESTAMP);
		keywords.add(LOGGER);
		keywords.add(LEVEL);
		keywords.add(THREAD);
		keywords.add(CLASS);
		keywords.add(FILE);
		keywords.add(LINE);
		keywords.add(METHOD);
		keywords.add(MESSAGE);
		keywords.add(NDC);
		try {
			exceptionPattern = Pattern.compile(EXCEPTION_PATTERN);
		} catch (PatternSyntaxException pse) {
			// shouldn't happen
		}
	}

	@PostConstruct
	public void init() throws Exception {
		if (timestampFormat != null) {
			timestampPatternText = convertTimestamp();
		}
		initializePatterns();
		createPattern();
	}

	protected void createPattern() {
		regexpPattern = Pattern.compile(regexp);
	}

	/**
	 * Helper method that supports the evaluation of the expression
	 * 
	 * @param event
	 * @return true if expression isn't set, or the result of the evaluation
	 *         otherwise
	 */
	private boolean passesExpression(LoggingEvent event) {
		if (event != null) {
			// if (expressionRule != null) {
			// return (expressionRule.evaluate(event));
			// }
		}
		return true;
	}

	/**
	 * Convert the match into a map.
	 * <p>
	 * Relies on the fact that the matchingKeywords list is in the same order as
	 * the groups in the regular expression
	 * 
	 * @param result
	 * @return map
	 */
	private Map<String, String> processEvent(MatchResult result) {
		Map<String, String> map = new HashMap<String, String>();
		// group zero is the entire match - process all other groups
		for (int i = 1; i < result.groupCount() + 1; i++) {
			String key = matchingKeywords.get(i - 1);
			String value = result.group(i);
			map.put(key, value);
		}
		return map;
	}

	/**
	 * Build the regular expression needed to parse log entries
	 * 
	 */
	protected void initializePatterns() {

		// if custom level definitions exist, parse them
		updateCustomLevelDefinitionMap();

		List<String> buildingKeywords = new ArrayList<String>();

		String newPattern = logFormat;

		int index = 0;
		String current = newPattern;
		// build a list of property names and temporarily replace the property
		// with an empty string,
		// we'll rebuild the pattern later
		List<String> propertyNames = new ArrayList<String>();
		while (index > -1) {
			if (current.indexOf(PROP_START) > -1
					&& current.indexOf(PROP_END) > -1) {
				index = current.indexOf(PROP_START);
				String longPropertyName = current.substring(
						current.indexOf(PROP_START),
						current.indexOf(PROP_END) + 1);
				String shortProp = getShortPropertyName(longPropertyName);
				buildingKeywords.add(shortProp);
				propertyNames.add(longPropertyName);
				current = current.substring(longPropertyName.length() + 1
						+ index);
				newPattern = singleReplace(newPattern, longPropertyName,
						new Integer(buildingKeywords.size() - 1).toString());
			} else {
				// no properties
				index = -1;
			}
		}

		/*
		 * we're using a treemap, so the index will be used as the key to ensure
		 * keywords are ordered correctly
		 * 
		 * examine pattern, adding keywords to an index-based map patterns can
		 * contain only one of these per entry...properties are the only
		 * 'keyword' that can occur multiple times in an entry
		 */
		Iterator<String> iter = keywords.iterator();
		while (iter.hasNext()) {
			String keyword = (String) iter.next();
			int index2 = newPattern.indexOf(keyword);
			if (index2 > -1) {
				buildingKeywords.add(keyword);
				newPattern = singleReplace(newPattern, keyword, new Integer(
						buildingKeywords.size() - 1).toString());
			}
		}

		String buildingInt = "";

		for (int i = 0; i < newPattern.length(); i++) {
			String thisValue = String.valueOf(newPattern.substring(i, i + 1));
			if (isInteger(thisValue)) {
				buildingInt = buildingInt + thisValue;
			} else {
				if (isInteger(buildingInt)) {
					matchingKeywords.add(buildingKeywords.get(Integer
							.parseInt(buildingInt)));
				}
				// reset
				buildingInt = "";
			}
		}

		// if the very last value is an int, make sure to add it
		if (isInteger(buildingInt)) {
			matchingKeywords.add(buildingKeywords.get(Integer
					.parseInt(buildingInt)));
		}

		newPattern = replaceMetaChars(newPattern);

		// compress one or more spaces in the pattern into the [ ]+ regexp
		// (supports padding of level in log files)
		newPattern = newPattern.replaceAll(MULTIPLE_SPACES_REGEXP,
				MULTIPLE_SPACES_REGEXP);
		newPattern = newPattern.replaceAll(Pattern.quote(PATTERN_WILDCARD),
				REGEXP_DEFAULT_WILDCARD);
		// use buildingKeywords here to ensure correct order
		for (int i = 0; i < buildingKeywords.size(); i++) {
			String keyword = (String) buildingKeywords.get(i);
			// make the final keyword greedy (we're assuming it's the message)
			if (i == (buildingKeywords.size() - 1)) {
				newPattern = singleReplace(newPattern, String.valueOf(i),
						GREEDY_GROUP);
			} else if (TIMESTAMP.equals(keyword)) {
				newPattern = singleReplace(newPattern, String.valueOf(i), "("
						+ timestampPatternText.replaceAll("'", "") + ")");
			} else if (LOGGER.equals(keyword) || LEVEL.equals(keyword)) {
				newPattern = singleReplace(newPattern, String.valueOf(i),
						NOSPACE_GROUP);
			} else {
				newPattern = singleReplace(newPattern, String.valueOf(i),
						DEFAULT_GROUP);
			}
		}

		regexp = newPattern;
		LOG.fine("regexp is " + regexp);
	}

	private void updateCustomLevelDefinitionMap() {
		if (customLevelDefinitions != null) {
			StringTokenizer entryTokenizer = new StringTokenizer(
					customLevelDefinitions, ",");

			customLevelDefinitionMap.clear();
			while (entryTokenizer.hasMoreTokens()) {
				StringTokenizer innerTokenizer = new StringTokenizer(
						entryTokenizer.nextToken(), "=");
				String key = innerTokenizer.nextToken();
				String value = innerTokenizer.nextToken();
				customLevelDefinitionMap.put(key, Level.toLevel(value));
			}
		}
	}

	private String singleReplace(String inputString, String oldString,
			String newString) {
		int propLength = oldString.length();
		int startPos = inputString.indexOf(oldString);
		if (startPos == -1) {
			LOG.info("string: " + oldString + " not found in input: "
					+ inputString + " - returning input");
			return inputString;
		}
		if (startPos == 0) {
			inputString = inputString.substring(propLength);
			inputString = newString + inputString;
		} else {
			inputString = inputString.substring(0, startPos) + newString
					+ inputString.substring(startPos + propLength);
		}
		return inputString;
	}

	private String getShortPropertyName(String longPropertyName) {
		String currentProp = longPropertyName.substring(longPropertyName
				.indexOf(PROP_START));
		String prop = currentProp.substring(0,
				currentProp.indexOf(PROP_END) + 1);
		String shortProp = prop.substring(PROP_START.length(),
				prop.length() - 1);
		return shortProp;
	}

	/**
	 * Some perl5 characters may occur in the log file format. Escape these
	 * characters to prevent parsing errors.
	 * 
	 * @param input
	 * @return string
	 */
	private String replaceMetaChars(String input) {
		// escape backslash first since that character is used to escape the
		// remaining meta chars
		input = input.replaceAll("\\\\", "\\\\\\");

		// don't escape star - it's used as the wildcard
		input = input.replaceAll(Pattern.quote("]"), "\\\\]");
		input = input.replaceAll(Pattern.quote("["), "\\\\[");
		input = input.replaceAll(Pattern.quote("^"), "\\\\^");
		input = input.replaceAll(Pattern.quote("$"), "\\\\$");
		input = input.replaceAll(Pattern.quote("."), "\\\\.");
		input = input.replaceAll(Pattern.quote("|"), "\\\\|");
		input = input.replaceAll(Pattern.quote("?"), "\\\\?");
		input = input.replaceAll(Pattern.quote("+"), "\\\\+");
		input = input.replaceAll(Pattern.quote("("), "\\\\(");
		input = input.replaceAll(Pattern.quote(")"), "\\\\)");
		input = input.replaceAll(Pattern.quote("-"), "\\\\-");
		input = input.replaceAll(Pattern.quote("{"), "\\\\{");
		input = input.replaceAll(Pattern.quote("}"), "\\\\}");
		input = input.replaceAll(Pattern.quote("#"), "\\\\#");
		return input;
	}

	/**
	 * Convert a keyword-to-values map to a LoggingEvent
	 * 
	 * @param fieldMap
	 * @param exception
	 * 
	 * @return logging event
	 */
	private LoggingEvent convertToEvent(Map<String, Object> fieldMap,
			String[] exception, DateFormat dateFormat) {
		if (fieldMap == null) {
			return null;
		}

		// a logger must exist at a minimum for the event to be processed
		if (!fieldMap.containsKey(LOGGER)) {
			fieldMap.put(LOGGER, "Unknown");
		}
		if (exception == null) {
			exception = emptyException;
		}

		Logger logger = null;
		long timeStamp = 0L;
		String level = null;
		String threadName = null;
		Object message = null;
		String ndc = null;
		String className = null;
		String methodName = null;
		String eventFileName = null;
		String lineNumber = null;

		logger = Logger.getLogger((String) fieldMap.remove(LOGGER));

		if ((dateFormat != null) && fieldMap.containsKey(TIMESTAMP)) {
			try {
				timeStamp = dateFormat.parse(
						(String) fieldMap.remove(TIMESTAMP)).getTime();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// use current time if timestamp not parseable
		if (timeStamp == 0L) {
			timeStamp = System.currentTimeMillis();
		}

		message = fieldMap.remove(MESSAGE);
		if (message == null) {
			message = "";
		}

		level = (String) fieldMap.remove(LEVEL);
		Level levelImpl;
		if (level == null) {
			levelImpl = Level.DEBUG;
		} else {
			// first try to resolve against custom level definition map, then
			// fall back to regular levels
			level = level.trim();
			levelImpl = customLevelDefinitionMap.get(level);
			if (levelImpl == null) {
				levelImpl = Level.toLevel(level.trim());
				if (!level.equals(levelImpl.toString())) {
					// check custom level map
					levelImpl = Level.DEBUG;
					LOG.fine("found unexpected level: " + level + ", logger: "
							+ logger.getName() + ", msg: " + message);
					// make sure the text that couldn't match a level is
					// added to the message
					message = level + " " + message;
				}
			}
		}

		threadName = (String) fieldMap.remove(THREAD);
		ndc = (String) fieldMap.remove(NDC);
		className = (String) fieldMap.remove(CLASS);
		methodName = (String) fieldMap.remove(METHOD);
		eventFileName = (String) fieldMap.remove(FILE);
		lineNumber = (String) fieldMap.remove(LINE);

		LocationInfo info = null;

		if ((eventFileName != null) || (className != null)
				|| (methodName != null) || (lineNumber != null)) {
			info = new LocationInfo(eventFileName, className, methodName,
					lineNumber);
		} else {
			info = LocationInfo.NA_LOCATION_INFO;
		}
		LoggingEvent event = new LoggingEvent(null, logger, timeStamp,
				levelImpl, message, threadName, new ThrowableInformation(
						exception), ndc, info, null);
		return event;
	}

	@Override
	public LogRecord parse(String line, ParsingContext parsingContext)
			throws ParseException {

		LogRecord logData = null;
		if (line.trim().equals("")) {
			parsingContext.getUnmatchedLog().append('\n');
			parsingContext.getUnmatchedLog().append(line);
			return null;
		}

		Matcher eventMatcher = regexpPattern.matcher(line);
		Matcher exceptionMatcher = exceptionPattern.matcher(line);
		HashMap<String, Object> logEventParsingProperties = (HashMap<String, Object>) parsingContext
				.getCustomConextProperties().get(PROPERTY_LOG_EVENT_PROPERTIES);
		if (eventMatcher.matches()) {
			// build an event from the previous match (held in current map)
			LoggingEvent event = buildEvent(parsingContext);
			if (event != null) {
				if (passesExpression(event)) {
					// doPost(event);
					logData = Log4jUtil.translateLog4j(event);
				}
			}
			logEventParsingProperties.putAll(processEvent(eventMatcher
					.toMatchResult()));
		} else if (exceptionMatcher.matches()) {
			// an exception line
			parsingContext.getUnmatchedLog().append('\n');
			parsingContext.getUnmatchedLog().append(line);
		} else {
			// neither...either post an event with the line or append as
			// additional lines
			// if this was a logging event with multiple lines, each line
			// will show up as its own event instead of being
			// appended as multiple lines on the same event..
			// choice is to have each non-matching line show up as its own
			// line, or append them all to a previous event
			if (appendNonMatches) {
				// hold on to the previous time, so we can do our best to
				// preserve time-based ordering if the event is a non-match
				String lastTime = (String) logEventParsingProperties
						.get(TIMESTAMP);
				// build an event from the previous match (held in current
				// map)
				if (logEventParsingProperties.size() > 0) {
					LoggingEvent event = buildEvent(parsingContext);
					if (event != null) {
						if (passesExpression(event)) {
							logData = Log4jUtil.translateLog4j(event);
						}
					}
				}
				if (lastTime != null) {
					logEventParsingProperties.put(TIMESTAMP, lastTime);
				}
				logEventParsingProperties.put(MESSAGE, line);
			} else {
				if (parsingContext.getUnmatchedLog().length() > 0) {
					parsingContext.getUnmatchedLog().append('\n');
				}
				parsingContext.getUnmatchedLog().append(line.trim());
			}
		}

		return logData;
	}

	@Override
	public ParserDescription getParserDescription() {
		return parserDescription;
	}

	@Override
	public LogRecord parseBuffer(ParsingContext parsingContext)
			throws ParseException {
		LogRecord logData = null;
		// build an event from the previous match (held in current map)
		LoggingEvent event = buildEvent(parsingContext);
		if (event != null) {
			if (passesExpression(event)) {
				// doPost(event);
				logData = Log4jUtil.translateLog4j(event);
			}
		}
		return logData;
	}

	@Override
	public void initParsingContext(ParsingContext parsingContext) {
		if (timestampFormat != null) {
			parsingContext.setDateFormat(new SimpleDateFormat(timestampFormat));
		}
		parsingContext.getCustomConextProperties().put(
				PROPERTY_LOG_EVENT_PROPERTIES, new HashMap<String, Object>());

	}

	/** ============================================================= */
	/** UTILITY METHODS */
	/** ============================================================= */

	/**
	 * Walk the additionalLines list, looking for the EXCEPTION_PATTERN.
	 * <p>
	 * Return the index of the first matched line (the match may be the 1st line
	 * of an exception)
	 * <p>
	 * Assumptions: <br>
	 * - the additionalLines list may contain both message and exception lines<br>
	 * - message lines are added to the additionalLines list and then exception
	 * lines (all message lines occur in the list prior to all exception lines)
	 * 
	 * @return -1 if no exception line exists, line number otherwise
	 */
	private int getExceptionLine(ParsingContext ctx) {
		String[] additionalLines = ctx.getUnmatchedLog().toString().split("\n");
		for (int i = 0; i < additionalLines.length; i++) {
			Matcher exceptionMatcher = exceptionPattern
					.matcher(additionalLines[i]);
			if (exceptionMatcher.matches()) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Combine all message lines occurring in the additionalLines list, adding a
	 * newline character between each line
	 * <p>
	 * the event will already have a message - combine this message with the
	 * message lines in the additionalLines list (all entries prior to the
	 * exceptionLine index)
	 * 
	 * @param firstMessageLine
	 *            primary message line
	 * @param exceptionLine
	 *            index of first exception line
	 * @return message
	 */
	private String buildMessage(String firstMessageLine, int exceptionLine,
			ParsingContext ctx) {
		if (ctx.getUnmatchedLog().length() == 0) {
			return firstMessageLine;
		}
		StringBuffer message = new StringBuffer();
		if (firstMessageLine != null) {
			message.append(firstMessageLine);
		}
		String[] additionalLines = ctx.getUnmatchedLog().toString().split("\n");
		int linesToProcess = (exceptionLine == -1 ? additionalLines.length
				: exceptionLine);

		for (int i = 0; i < linesToProcess; i++) {
			message.append(newLine);
			message.append(additionalLines[i]);
		}
		return message.toString();
	}

	/**
	 * Combine all exception lines occurring in the additionalLines list into a
	 * String array
	 * <p>
	 * (all entries equal to or greater than the exceptionLine index)
	 * 
	 * @param exceptionLine
	 *            index of first exception line
	 * @return exception
	 */
	private String[] buildException(int exceptionLine, ParsingContext ctx) {
		if (exceptionLine == -1) {
			return emptyException;
		}
		String[] additionalLines = ctx.getUnmatchedLog().toString().split("\n");
		String[] exception = new String[additionalLines.length - exceptionLine
				- 1];
		for (int i = 0; i < exception.length; i++) {
			exception[i] = additionalLines[i + exceptionLine];
		}
		return exception;
	}

	/**
	 * Construct a logging event from currentMap and additionalLines
	 * (additionalLines contains multiple message lines and any exception lines)
	 * <p>
	 * CurrentMap and additionalLines are cleared in the process
	 * 
	 * @return event
	 */
	private LoggingEvent buildEvent(ParsingContext ctx) {
		HashMap<String, Object> logEventParsingProperitesMap = (HashMap<String, Object>) ctx
				.getCustomConextProperties().get(PROPERTY_LOG_EVENT_PROPERTIES);
		if (logEventParsingProperitesMap.size() == 0) {
			String[] additionalLines = ctx.getUnmatchedLog().toString()
					.split("\n");
			if (additionalLines.length > 0) {
				for (String line : additionalLines) {
					LOG.finest(String.format(
							"found non-matching (file %s) line: \"%s\"",
							ctx.getLogSource(), line));
				}
			}
			ctx.getUnmatchedLog().setLength(0);
			return null;
		}
		// the current map contains fields - build an event
		int exceptionLine = getExceptionLine(ctx);
		String[] exception = buildException(exceptionLine, ctx);
		String[] additionalLines = ctx.getUnmatchedLog().toString().split("\n");
		// messages are listed before exceptions in additional lines
		if (additionalLines.length > 0 && exception.length > 0) {
			logEventParsingProperitesMap.put(
					MESSAGE,
					buildMessage(
							(String) logEventParsingProperitesMap.get(MESSAGE),
							exceptionLine, ctx));
		}
		DateFormat dateFormat = ctx.getDateFormat();
		LoggingEvent event = convertToEvent(logEventParsingProperitesMap,
				exception, dateFormat);

		logEventParsingProperitesMap.clear();
		ctx.getUnmatchedLog().setLength(0);
		return event;
	}

	/** ============================================================= */
	/** GETTER/SETTER METHODS */
	/** ============================================================= */

	@Override
	public int getVersion() {
		return LOG_PARSER_VERSION_1;
	}

	/**
	 * If the log file contains non-log4j level strings, they can be mapped to
	 * log4j levels using the format (android example):
	 * V=TRACE,D=DEBUG,I=INFO,W=WARN,E=ERROR,F=FATAL,S=OFF
	 * 
	 * @param customLevelDefinitions
	 *            the level definition string
	 */
	public void setCustomLevelDefinitions(String customLevelDefinitions) {
		this.customLevelDefinitions = customLevelDefinitions;
	}

	public String getCustomLevelDefinitions() {
		return customLevelDefinitions;
	}

	/**
	 * Get Log format.
	 * 
	 * @return log format
	 */
	public String getLogFormat() {
		return logFormat;
	}

	/**
	 * Sets the log format.
	 * 
	 * @param logFormat
	 *            the format
	 */
	public void setLogFormat(String logFormat) {
		this.logFormat = logFormat;
	}

	/**
	 * Sets a pattern from {@link java.text.SimpleDateFormat}
	 * 
	 * @param timestampFormat
	 */
	public void setTimestampFormat(String timestampFormat) {
		this.timestampFormat = timestampFormat;
	}

	/**
	 * Gets timestamp format.
	 * 
	 * @return timestamp format
	 */
	public String getTimestampFormat() {
		return timestampFormat;
	}

	/** ============================================================= */
	/** HELPER METHODS */
	/** ============================================================= */

	/**
	 * Helper method that will convert timestamp format to a pattern
	 * 
	 * 
	 * @return string
	 */
	private String convertTimestamp() {
		// some locales (for example, French) generate timestamp text with
		// characters not included in \w -
		// now using \S (all non-whitespace characters) instead of /w
		String result = timestampFormat.replaceAll(
				VALID_DATEFORMAT_CHAR_PATTERN + "+", "\\\\S+");
		// make sure dots in timestamp are escaped
		result = result.replaceAll(Pattern.quote("."), "\\\\.");
		return result;
	}

	private boolean isInteger(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

}

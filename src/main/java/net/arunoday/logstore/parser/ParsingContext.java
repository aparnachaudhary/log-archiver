package net.arunoday.logstore.parser;

import java.text.DateFormat;
import java.util.HashMap;

public class ParsingContext {

  private StringBuilder unmatchedLog;
  private long lastParsed = 0;
  private int generatedId = 0;
  private volatile boolean parsingInProgress = true;
  private String name;
  private String logSource;
  private HashMap<String, Object> customConextProperties;
  private DateFormat dateFormat;

  public ParsingContext() {
    this("?");
  }

  public ParsingContext(String name) {
    this(name, null);
  }

  public ParsingContext(String name, String logSource) {
    this.name = name;
    this.logSource = logSource;
    unmatchedLog = new StringBuilder();
    customConextProperties = new HashMap<String, Object>();
  }

  public HashMap<String, Object> getCustomConextProperties() {
    return customConextProperties;
  }

  public void setCustomConextProperties(HashMap<String, Object> customConextProperties) {
    this.customConextProperties = customConextProperties;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isParsingInProgress() {
    return parsingInProgress;
  }

  public void setParsingInProgress(boolean parsingInProgress) {
    this.parsingInProgress = parsingInProgress;
  }

  public int getGeneratedId() {
    return generatedId;
  }

  public int getGeneratedIdAndIncrease() {
    return generatedId++;
  }

  public void setGeneratedId(int generatedId) {
    this.generatedId = generatedId;
  }

  public long getLastParsed() {
    return lastParsed;
  }

  public void setLastParsed(long lastParsed) {
    this.lastParsed = lastParsed;
  }

  public StringBuilder getUnmatchedLog() {
    return unmatchedLog;
  }

  public void setUnmatchedLog(StringBuilder unmatchedLog) {
    this.unmatchedLog = unmatchedLog;
  }

  public String getLogSource() {
    return logSource;
  }

  public void setLogSource(String logSource) {
    this.logSource = logSource;
  }

  public DateFormat getDateFormat() {
    return dateFormat;
  }

  public void setDateFormat(DateFormat dateFormat) {
    this.dateFormat = dateFormat;
  }
}

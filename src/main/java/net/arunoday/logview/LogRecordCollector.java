package net.arunoday.logview;

public interface LogRecordCollector {

  public abstract void add(LogRecord... logRecords);

  public abstract LogRecord[] getLogRecords();

  public abstract int clear();

}

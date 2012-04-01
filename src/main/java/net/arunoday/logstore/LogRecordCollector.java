package net.arunoday.logstore;

public interface LogRecordCollector {

  public abstract void add(LogRecord... logRecords);

  public abstract LogRecord[] getLogRecords();

  public abstract int clear();

}

package net.arunoday.logstore.reader;

import java.util.LinkedList;

import net.arunoday.logstore.LogRecord;
import net.arunoday.logstore.LogRecordCollector;


public class ProxyLogDataCollector implements LogRecordCollector {

  private LinkedList<LogRecord> list;

  public ProxyLogDataCollector() {
    list = new LinkedList<LogRecord>();
  }

  public void add(LogRecord... logDatas) {
    for (int i = 0; i < logDatas.length; i++) {
      list.addLast(logDatas[i]);
    }

  }

  public LogRecord[] getLogRecords() {
    LogRecord[] datas = new LogRecord[list.size()];
    datas = list.toArray(datas);
    return datas;
  }

  @Override
  public int clear() {
    int size = list.size();
    list.clear();
    return size;
  }

}

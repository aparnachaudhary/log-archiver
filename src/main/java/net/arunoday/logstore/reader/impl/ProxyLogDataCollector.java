package net.arunoday.logstore.reader.impl;

import java.util.LinkedList;

import org.springframework.stereotype.Component;

import net.arunoday.logstore.domain.LogRecord;
import net.arunoday.logstore.reader.LogRecordCollector;

/**
 * @author Aparna Chaudhary
 * 
 */
@Component
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

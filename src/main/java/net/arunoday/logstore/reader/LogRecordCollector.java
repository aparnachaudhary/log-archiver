package net.arunoday.logstore.reader;

import net.arunoday.logstore.domain.LogRecord;

/**
 * @author Aparna Chaudhary
 * 
 */
public interface LogRecordCollector {

	public abstract void add(LogRecord... logRecords);

	public abstract LogRecord[] getLogRecords();

	public abstract int clear();

}

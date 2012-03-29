package net.arunoday.logview;

import java.util.Date;

public class LogRecordBuilder {

	private LogRecord ld;

	public LogRecordBuilder() {
		ld = new LogRecord();
	}

	public LogRecord build() {
		return ld;
	}

	public LogRecordBuilder withId(String id) {
		ld.setId(id);
		return this;
	}

	public LogRecordBuilder withMessage(String message) {
		ld.setMessage(message);
		return this;
	}

	public LogRecordBuilder withClass(String clazz) {
		ld.setClazz(clazz);
		return this;
	}

	public LogRecordBuilder withDate(Date date) {
		ld.setDate(date);
		return this;
	}

	public LogRecordBuilder withLevel(String level) {
		ld.setLevel(level);
		return this;
	}

	public LogRecordBuilder withLoggerName(String loggerName) {
		ld.setLoggerName(loggerName);
		return this;
	}

	public LogRecordBuilder withMarked(boolean marked) {
		ld.setMarked(marked);
		return this;
	}

	public LogRecordBuilder withMessageId(String messageId) {
		ld.setMessageId(messageId);
		return this;
	}

	public LogRecordBuilder withMethod(String method) {
		ld.setMethod(method);
		return this;
	}

	// public LogRecordBuilder withProperties(Map<String, String> properties) {
	// ld.setProperties(properties);
	// return this;
	// }

	public LogRecordBuilder withThread(String thread) {
		ld.setThread(thread);
		return this;
	}

	public LogRecordBuilder withLogSource(String logSource) {
		ld.setLogSource(logSource);
		return this;
	}

}

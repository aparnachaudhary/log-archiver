package net.arunoday.logstore.repository;

import net.arunoday.logstore.domain.LogRecord;

import org.springframework.data.repository.CrudRepository;

/**
 * Repository for {@link LogRecord}.
 * 
 * @author Aparna Chaudhary
 */
public interface LogRecordRepository extends CrudRepository<LogRecord, String> {

	/**
	 * Creates a new collection for LogRecord entity.
	 */
	void createCollection();

	/**
	 * Drops LogRecord collection.
	 */
	void dropCollection();

}

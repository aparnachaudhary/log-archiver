package net.arunoday.logstore.repository;

import net.arunoday.logstore.domain.LogRecord;

import org.springframework.data.repository.CrudRepository;

/**
 * @author Aparna Chaudhary
 *
 */
public interface LogRecordRepository extends CrudRepository<LogRecord, String> {

	void createCollection();

	void dropCollection();

}

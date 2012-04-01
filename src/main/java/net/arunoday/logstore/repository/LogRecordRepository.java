package net.arunoday.logstore.repository;

import net.arunoday.logstore.LogRecord;

import org.springframework.data.repository.CrudRepository;

public interface LogRecordRepository extends CrudRepository<LogRecord, String>{

	void createCollection();
	
	void dropCollection();


}

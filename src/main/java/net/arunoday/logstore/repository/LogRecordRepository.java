package net.arunoday.logstore.repository;

import net.arunoday.logstore.domain.LogRecord;

import org.springframework.data.repository.CrudRepository;

/**
<<<<<<< HEAD
 * Repository for {@link LogRecord}.
 * @author Aparna Chaudhary
=======
 * @author Aparna Chaudhary
 *
>>>>>>> 8dee5bd6267b5d3be5242b5de9d14fcd529f3608
 */
public interface LogRecordRepository extends CrudRepository<LogRecord, String> {

	void createCollection();

	void dropCollection();

}

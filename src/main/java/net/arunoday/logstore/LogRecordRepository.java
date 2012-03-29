package net.arunoday.logstore;

import net.arunoday.logview.LogRecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;

@Repository
public class LogRecordRepository {

	@Autowired
	MongoOperations mongoOperations;

	public void run(LogRecord record) {

		try {
			if (!mongoOperations.collectionExists(LogRecord.class)) {
				//mongoOperations.dropCollection(LogRecord.class);
				mongoOperations.createCollection(LogRecord.class);
			}
			System.err.println("Storing Data :" + record.getId());
			mongoOperations.insert(record);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//List<LogRecord> results = mongoOperations.findAll(LogRecord.class);
		// System.out.println("Results: " + results.size());
	}

}

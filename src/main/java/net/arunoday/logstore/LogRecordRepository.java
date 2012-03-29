package net.arunoday.logstore;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.List;

import net.arunoday.logview.LogRecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoCollectionUtils;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class LogRecordRepository {

	@Autowired
	MongoOperations mongoOperations;

	public void createCollection() {
		try {
			if (!mongoOperations.collectionExists(LogRecord.class)) {
				mongoOperations.createCollection(LogRecord.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void dropCollection() {
		try {
			if (mongoOperations.collectionExists(LogRecord.class)) {
				mongoOperations.dropCollection(LogRecord.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void persist(LogRecord record) {
		try {
			mongoOperations.insert(record);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void findAll() {
		String collectionName = MongoCollectionUtils
				.getPreferredCollectionName(LogRecord.class);
		System.err.println("Collection Name:" + collectionName);
		System.err.println("Total Log Records: "
				+ mongoOperations.getCollection(collectionName).count());

		// LogRecord qp = mongoOperations.findOne(
		// query(where("thread").is("main")), LogRecord.class);

		List<LogRecord> result = mongoOperations.find(new Query(where("thread")
				.is("main")), LogRecord.class);

		System.err.println("Total Found : " + result.size());
		System.err.println(result);

		List<LogRecord> results = mongoOperations.findAll(LogRecord.class);
		System.out.println("Results: " + results.size());

	}
}

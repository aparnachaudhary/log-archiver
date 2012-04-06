package net.arunoday.logstore.repository.impl;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.List;

import net.arunoday.logstore.domain.LogRecord;
import net.arunoday.logstore.repository.LogRecordRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoCollectionUtils;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

/**
 * @author Aparna Chaudhary
 * 
 */
@Repository("logRecordRepository")
// FIXME: Add implementation
public class LogRecordRepositoryImpl implements LogRecordRepository {

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

	public Iterable<LogRecord> findAll() {
		String collectionName = MongoCollectionUtils
				.getPreferredCollectionName(LogRecord.class);
		System.err.println("Collection Name:" + collectionName);
		System.err.println("Total Log Records: "
				+ mongoOperations.getCollection(collectionName).count());

		List<LogRecord> results = mongoOperations.findAll(LogRecord.class);
		System.out.println("Results: " + results.size());

		return results;
	}

	@Override
	public LogRecord save(LogRecord entity) {
		try {
			mongoOperations.insert(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return entity;
	}

	@Override
	public Iterable<LogRecord> save(Iterable<? extends LogRecord> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LogRecord findOne(String id) {
		return mongoOperations.findOne(new Query(where("level").is("ERROR")),
				LogRecord.class);
	}

	@Override
	public boolean exists(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void delete(String id) {
		Assert.notNull(id, "The given id must not be null!");
		// FIXME
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(LogRecord entity) {
		Assert.notNull(entity, "The given entity must not be null!");
		delete(entity.getId());

	}

	@Override
	public void delete(Iterable<? extends LogRecord> entities) {
		Assert.notNull(entities, "The given Iterable of entities not be null!");

		for (LogRecord entity : entities) {
			delete(entity);
		}

	}

	@Override
	public void deleteAll() {
		String collectionName = MongoCollectionUtils
				.getPreferredCollectionName(LogRecord.class);
		mongoOperations.remove(new Query(), collectionName);

	}

}

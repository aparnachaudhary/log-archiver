package net.arunoday.logstore.repository.impl;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.List;

import net.arunoday.logstore.domain.LogRecord;
import net.arunoday.logstore.repository.LogRecordRepository;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoCollectionUtils;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

/**
 * @author Aparna Chaudhary
 */
@Repository("logRecordRepository")
public class LogRecordRepositoryImpl implements LogRecordRepository {

	private static final Logger logger = Logger
			.getLogger(LogRecordRepositoryImpl.class);

	@Autowired
	MongoOperations mongoOperations;

	public void createCollection() {
		try {
			if (!mongoOperations.collectionExists(LogRecord.class)) {
				mongoOperations.createCollection(LogRecord.class);
			}
		} catch (Exception e) {
			logger.error("Failed to create mongo-collection", e);
			throw new RuntimeException("Failed to create mongo-collection", e);
		}
	}

	public void dropCollection() {
		try {
			if (mongoOperations.collectionExists(LogRecord.class)) {
				mongoOperations.dropCollection(LogRecord.class);
			}
		} catch (Exception e) {
			logger.error("Failed to drop mongo-collection", e);
			throw new RuntimeException("Failed to drop mongo-collection", e);
		}
	}

	public Iterable<LogRecord> findAll() {
		List<LogRecord> results = mongoOperations.findAll(LogRecord.class);
		logger.debug("Total Results: " + results.size());
		return results;
	}

	@Override
	public LogRecord save(LogRecord entity) {
		try {
			mongoOperations.insert(entity);
		} catch (Exception e) {
			logger.error("LogRecord store failed", e);
			throw new RuntimeException("LogRecord store failed", e);
		}
		return entity;
	}

	@Override
	public Iterable<LogRecord> save(Iterable<? extends LogRecord> entities) {
		throw new UnsupportedOperationException();
	}

	@Override
	public LogRecord findOne(String id) {
		return mongoOperations.findOne(new Query(where("id").is(id)),
				LogRecord.class);
	}

	@Override
	public boolean exists(String id) {
		Assert.notNull(id, "The given id must not be null!");
		String collectionName = getCollectionName();

		return mongoOperations.findOne(new Query(where("id").is(id)),
				Object.class, collectionName) != null;
	}

	private String getCollectionName() {
		String collectionName = MongoCollectionUtils
				.getPreferredCollectionName(LogRecord.class);
		return collectionName;
	}

	@Override
	public long count() {
		String collectionName = getCollectionName();
		return mongoOperations.getCollection(collectionName).count();
	}

	@Override
	public void delete(String id) {
		Assert.notNull(id, "The given id must not be null!");
		mongoOperations.remove(new Query(where("id").is(id)), LogRecord.class);
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
		String collectionName = getCollectionName();
		mongoOperations.remove(new Query(), collectionName);
	}

}

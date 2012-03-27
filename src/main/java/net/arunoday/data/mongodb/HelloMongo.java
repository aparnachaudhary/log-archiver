package net.arunoday.data.mongodb;

import java.util.List;

import net.arunoday.data.mongodb.domain.Account;
import net.arunoday.data.mongodb.domain.Person;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;

@Repository
public class HelloMongo {

	@Autowired
	MongoOperations mongoOperations;

	public void run() {

		if (mongoOperations.collectionExists(Person.class)) {
			mongoOperations.dropCollection(Person.class);
		}

		mongoOperations.createCollection(Person.class);

		Person p = new Person("John", 39);
		Account a = new Account("1234-59873-893-1", Account.Type.SAVINGS, 123.45D);
		p.getAccounts().add(a);

		mongoOperations.insert(p);

		List<Person> results = mongoOperations.findAll(Person.class);
		System.out.println("Results: " + results);
	}

}

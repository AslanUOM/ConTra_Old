package com.aslan.contra.db;

import java.util.HashMap;
import java.util.Map;

import com.aslan.contra.entities.Home;
import com.aslan.contra.entities.Person;

public class PersonService extends GenericService<Person> {

	@Override
	public Class<Person> getEntityType() {
		return Person.class;
	}

	public Iterable<Person> friends(String phoneNumber) {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("phone_number", phoneNumber);
		String cypher = "MATCH (person:Person)-[:FRIEND]->(friends) WHERE person.phoneNumber = {phone_number} RETURN friends";
		Iterable<Person> friends = session.query(getEntityType(), cypher, parameters);
		return friends;
	}

	public Person find(String phoneNumber) {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("phone_number", phoneNumber);
		String cypher = "MATCH (person:Person) WHERE person.phoneNumber = {phone_number} RETURN person";
		Person person = session.queryForObject(getEntityType(), cypher, parameters);
		return person;
	}
}

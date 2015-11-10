package com.aslan.contra.db;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.ogm.session.result.Result;

import com.aslan.contra.entities.Location;
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

	/**
	 * Find a person using the formatted phone number. Formatted phone number is
	 * a unique attribute used to identify person.
	 * 
	 * @param phoneNumber
	 * @return
	 */
	public Person find(String phoneNumber) {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("phone_number", phoneNumber);
		String cypher = "MATCH (person:Person) WHERE person.phoneNumber = {phone_number} RETURN person";
		Person person = session.queryForObject(getEntityType(), cypher, parameters);
		return person;
	}

	/**
	 * Retrieve the friends of the given person if they are in the same
	 * location.
	 * 
	 * @param phoneNumber
	 *            the formatted phone number of the person.
	 * @return a list of friends available in that location.
	 */
	public Iterable<Person> nearByFriends(String phoneNumber) {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("phone_number", phoneNumber);
		String cypher = "MATCH (person:Person)-[:CURRENT_LOCATION]->(location)<-[:CURRENT_LOCATION]-(friends)<-[:FRIEND]-(person) WHERE person.phoneNumber = {phone_number} RETURN friends";
		Iterable<Person> friends = session.query(getEntityType(), cypher, parameters);
		return friends;
	}

	/**
	 * Remove the existing CURRENT_LOCATION relationship and add a new one. Note
	 * that this method will not remove the previous current location node. It
	 * will remove the relationship only.
	 * 
	 * @param person
	 * @param currentLocation
	 */
	public void updateCurrentLocation(Person person, Location currentLocation) {
		// Delete the existing relationship
		Map<String, String> parameters = new HashMap<>();
		parameters.put("phone_number", person.getPhoneNumber());
		String cypher = "MATCH (person:Person{phoneNumber:{phone_number}})-[currentLocation:CURRENT_LOCATION]->(Location) DELETE currentLocation";
		session.query(cypher, parameters);

		person.setCurrentLocation(currentLocation);
		createOrUpdate(person);
	}

	/**
	 * Return true if there is a relationship such that person-[FRIEND]->with.
	 * If not, it will return false.
	 * 
	 * @param person
	 *            the node with outgoing relationship.
	 * @param with
	 *            the node with incoming relationship.
	 * @return true if person is FREIND with 'with'.
	 */
	public boolean isFriendWith(Person person, Person with) {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("person_phone_number", person.getPhoneNumber());
		parameters.put("friend_phone_number", with.getPhoneNumber());
		String cypher = "MATCH (person:Person {phoneNumber:{person_phone_number})-[r:FRIEND]->(friend:Person {phoneNumber:{friend_phone_number}) RETURN r";
		Result result = session.query(cypher, parameters);
		boolean hasFriendship = result.iterator().hasNext();
		return hasFriendship;
	}

	/**
	 * Retrieve the GCM tokens of all the devices of the given person.
	 * 
	 * @param phoneNumber
	 *            formatted phone number/userId of the person.
	 * @return tokens of all the devices.
	 */
	public Iterable<String> allDeviceTokens(String phoneNumber) {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("phone_number", phoneNumber);
		// TODO: This query can be enhanced to check the active devices only
		String cypher = "MATCH (person:Person {phoneNumber:{person_phone_number})-[:OWNS]->(device:Device) RETURN device.token";
		Iterable<String> tokens = session.query(String.class, cypher, parameters);
		return tokens;
	}
}

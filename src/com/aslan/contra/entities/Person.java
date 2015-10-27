package com.aslan.contra.entities;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Person extends Entity {
	/**
	 * Name of the Person.
	 */
	private String name;

	/**
	 * Phone number of the person. Must be unique.
	 */
	private String phoneNumber;

	/**
	 * Home location.
	 */
	@Relationship(type = "LIVES_IN")
	private Home home;

	/**
	 * Work location.
	 */
	@Relationship(type = "WORKS_IN")
	private Work work;

	/**
	 * Current location of the person.
	 */
	@Relationship(type = "CURRENT_LOCATION")
	private Location currentLocation;

	/**
	 * Set of devices. One to many relationship.
	 */
	@Relationship(type = "OWNS")
	private Set<Device> devices;

	/**
	 * The persons who are listed as friends by this person.
	 */
	@Relationship(type = "FRIENDS")
	private Set<Person> friends = new HashSet<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Home getHome() {
		return home;
	}

	public void setHome(Home home) {
		this.home = home;
	}

	public Work getWork() {
		return work;
	}

	public void setWork(Work work) {
		this.work = work;
	}

	public Set<Device> getDevice() {
		return devices;
	}

	public void setDevice(Set<Device> devices) {
		this.devices = devices;
	}

	public Set<Person> getFriends() {
		return friends;
	}

	public void setFriends(Set<Person> friends) {
		this.friends = friends;
	}

	public Location getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(Location currentLocation) {
		this.currentLocation = currentLocation;
	}

}

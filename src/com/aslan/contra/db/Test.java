package com.aslan.contra.db;

import com.aslan.contra.entities.Person;

public class Test {

	public static void main(String[] args) {
		PersonService service = new PersonService();

		// Person p1 = new Person();
		// p1.setName("Gobinath");
		// p1.setPhoneNumber("+94-770780210");
		//
		// Person p2 = new Person();
		// p2.setName("Vishnu");
		// p2.setPhoneNumber("+94-778709767");
		//
		// service.createOrUpdate(p1);
		// service.createOrUpdate(p2);

		// Person p = service.find("+94-770780210");
		// p.getFriends().add(service.find("+94-778709767"));
		// service.createOrUpdate(p);
		
		for(Person p : service.friends("+94-778709767")) {
			System.out.println(p.getName());
		}
	}

}

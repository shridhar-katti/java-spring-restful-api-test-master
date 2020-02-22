package uk.co.huntersix.spring.rest.referencedata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import uk.co.huntersix.spring.rest.model.Person;

@Service
public class PersonDataService {
	private  List<Person> personDataList = new ArrayList<>(
			Arrays.asList(new Person("Mary", "Smith"), new Person("Mary", "Evan"), new Person("Scott", "Evan"),
					new Person("Brian", "Archer"), new Person("Collin", "Brown")));

	public Optional<Person> findPerson(String lastName, String firstName) {
		List<Person> filteredList = personDataList.stream()
				.filter(p -> p.getFirstName().equalsIgnoreCase(firstName) && p.getLastName().equalsIgnoreCase(lastName))
				.collect(Collectors.toList());

		if (filteredList.isEmpty()) {
			return Optional.ofNullable(null);

		}

		return Optional.ofNullable(filteredList.get(0));
	}

	public List<Person> findPersonByLastName(String lastName) {
		return personDataList.stream().filter(p -> p.getLastName().equalsIgnoreCase(lastName)).collect(Collectors.toList());
	}

	public Optional<Person> addPerson(String lastName, String firstName) {

		Optional<Person> person = Optional.ofNullable(new Person(firstName, lastName));
		personDataList.add(person.get());

		return person;
	}

	public List<Person> updateFirstName(String lastName, String firstName) {

		List<Person> filteredList = findPersonByLastName(lastName);
		filteredList.stream().forEach(p -> p.setFirstName(firstName));

		return filteredList;
	}
}

package uk.co.huntersix.spring.rest.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.hibernate.validator.internal.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import uk.co.huntersix.spring.rest.model.ErrorResponse;
import uk.co.huntersix.spring.rest.model.Person;
import uk.co.huntersix.spring.rest.referencedata.PersonDataService;

@RestController
public class PersonController {
	private PersonDataService personDataService;

	public PersonController(@Autowired PersonDataService personDataService) {
		this.personDataService = personDataService;
	}

	@GetMapping("/person/{lastName}/{firstName}")
	public ResponseEntity<Object> person(@PathVariable(value = "lastName") String lastName,
			@PathVariable(value = "firstName") String firstName) {
		Optional<Person> person = personDataService.findPerson(lastName, firstName);

		if (person.isPresent()) {
			return ResponseEntity.ok(person.get());
		}

		return new ResponseEntity<>(new ErrorResponse("404", "Person not found."), HttpStatus.NOT_FOUND);
	}

	@GetMapping("/person/{lastName}")
	public ResponseEntity<List<Person>> person(@PathVariable(value = "lastName") String lastName) {
		List<Person> persons = personDataService.findPersonByLastName(lastName);

		return ResponseEntity.ok(persons);
	}

	@PostMapping("/person")
	public ResponseEntity<Object> addPerson(@Valid @RequestBody final PersonRequest request) {

		Optional<Person> person = personDataService.findPerson(request.getLastName(), request.getFirstName());

		if (person.isPresent()) {
			return new ResponseEntity<>(new ErrorResponse("409", "Person already exists."), HttpStatus.CONFLICT);
		}

		person = personDataService.addPerson(request.getLastName(), request.getFirstName());

		return new ResponseEntity<>(person.isPresent() ? person.get() : null, HttpStatus.CREATED);

	}

	@PatchMapping("/person/{lastName}")
	public ResponseEntity<Object> updateFirstName(@PathVariable(value = "lastName") String lastName,
			@RequestBody final PersonRequest request) {

		if (StringHelper.isNullOrEmptyString(request.getFirstName())) {
			return new ResponseEntity<>(new ErrorResponse("400", "Required attributes missing."),
					HttpStatus.BAD_REQUEST);
		}
		List<Person> persons = personDataService.updateFirstName(lastName, request.getFirstName());

		return ResponseEntity.ok(persons);
	}
}
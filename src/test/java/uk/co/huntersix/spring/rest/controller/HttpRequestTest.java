package uk.co.huntersix.spring.rest.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import uk.co.huntersix.spring.rest.model.ErrorResponse;
import uk.co.huntersix.spring.rest.model.Person;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HttpRequestTest {
	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void shouldReturnPersonDetails() {
		assertThat(this.restTemplate.getForObject(getBaseUrl("smith","mary"), String.class))
				.contains("Mary");
	}

	@Test
	public void testPersonDoesNotExists() {
		ErrorResponse response = this.restTemplate.getForObject(getBaseUrl("smith","test"),
				ErrorResponse.class);

		assertThat(response.getErrorCode().equals("404"));
	}

	@Test
	public void testPersonWithLastNameExists()  {
		Person[] response = this.restTemplate.getForObject(getBaseUrl("Mary"), Person[].class);

		assertThat(response.length == 1);
	}

	@Test
	public void testPersonWithLastNameDoesNotExists()  {
		Person[] response = this.restTemplate.getForObject(getBaseUrl("test"), Person[].class);

		assertThat(response.length == 0);
	}

	@Test
	public void testPersonWithLastNameExistsMultiple()  {
		Person[] response = this.restTemplate.getForObject(getBaseUrl("Mary"), Person[].class);

		assertThat(response.length == 2);
	}

	@Test
	public void testAddPerson()  {
		Person response = this.restTemplate.postForObject(getBaseUrl(),
				new Person("john", "jill"), Person.class);

		assertThat(response.getLastName().equals("jill"));
	}

	@Test
	public void testAddPersonWhenPersonAlreadyExists() {
		ResponseEntity<Object> response = this.restTemplate.postForEntity(getBaseUrl(),
				new Person("Mary", "Smith"), Object.class);

		assertThat(response.getStatusCode().equals(HttpStatus.CONFLICT));
	}

	@Test
	public void testAddPersonFirstNameEmpty()  {
		ResponseEntity<Object> response = this.restTemplate.postForEntity(getBaseUrl(),
				new Person("Mary", null), Object.class);

		assertThat(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
	}

	@Test
	public void testAddPersonLastNameEmpty()  {
		ResponseEntity<Object> response = this.restTemplate.postForEntity(getBaseUrl(),
				new Person(null, "Smith"), Object.class);

		assertThat(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
	}

	@Test
	public void testUpdateFirstNameNoMatchFound()  {
		Person[] response = this.restTemplate.patchForObject(getBaseUrl("Mary"),
				new Person("john", "jill"), Person[].class);
		assertTrue(response.length==0);
	}
	
	@Test
	public void testUpdateFirstNameOneMatchFound()  {
		Person[] response = this.restTemplate.patchForObject(getBaseUrl("Smith"),
				new Person("john", "jill"), Person[].class);
		assertTrue(response.length==1);
	}
	
	@Test
	public void testUpdateFirstNameMoreMatchFound(){
		Person[] response = this.restTemplate.patchForObject(getBaseUrl("Evan"),
				new Person("john", "jill"), Person[].class);
		assertTrue(response.length==2);
	}
	
	private String getBaseUrl(String... additionalPathVariables) {
		
		StringBuilder sb =  new StringBuilder("http://localhost:");
		sb.append(port).append("/person");
		for(int index=0;index<additionalPathVariables.length;index++) {
			sb.append("/").append(additionalPathVariables[index]);
		}
		return sb.toString();
	}
}
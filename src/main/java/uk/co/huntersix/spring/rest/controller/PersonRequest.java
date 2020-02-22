package uk.co.huntersix.spring.rest.controller;

import javax.validation.constraints.NotBlank;

public class PersonRequest {

	@NotBlank(message = "Required input \"firstName\" cannot be empty. ")
	private String firstName;
	
	@NotBlank(message = "Required input \"lastName\" cannot be empty. ")
	private String lastName;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	
	
}

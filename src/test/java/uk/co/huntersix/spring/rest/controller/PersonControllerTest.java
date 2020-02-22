package uk.co.huntersix.spring.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.co.huntersix.spring.rest.model.Person;
import uk.co.huntersix.spring.rest.referencedata.PersonDataService;

@RunWith(SpringRunner.class)
@WebMvcTest(PersonController.class)
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonDataService personDataService;

    @Test
    public void shouldReturnPersonFromService() throws Exception {
        when(personDataService.findPerson(any(), any())).thenReturn(Optional.of(new Person("Mary", "Smith")));
        this.mockMvc.perform(get("/person/smith/mary"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("id").exists())
            .andExpect(jsonPath("firstName").value("Mary"))
            .andExpect(jsonPath("lastName").value("Smith"));
    }
    
    @Test
    public void testPersonDoesNotExistsFromService() throws Exception {
        when(personDataService.findPerson(any(), any())).thenReturn(Optional.ofNullable(null));
        this.mockMvc.perform(get("/person/smith/a"))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("id").doesNotExist())
            .andExpect(jsonPath("firstName").doesNotExist())
            .andExpect(jsonPath("lastName").doesNotExist());
    }
    
    @Test
    public void findPersonByLastNameNoMatch() throws Exception {
        when(personDataService.findPersonByLastName(any())).thenReturn(Collections.emptyList());
        this.mockMvc.perform(get("/person/a"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }
    
    @Test
    public void findPersonByLastNameOneMatch() throws Exception {
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Mary","Smith"));
		when(personDataService.findPersonByLastName(any())).thenReturn(persons);
        this.mockMvc.perform(get("/person/Mary"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].id").exists())
            .andExpect(jsonPath("$[0].firstName").value("Mary"))
            .andExpect(jsonPath("$[0].lastName").value("Smith"));
    }
    
    @Test
    public void findPersonByLastNameMoreMatch() throws Exception {
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Mary","Smith"));
        persons.add(new Person("Mary","Evan"));
		when(personDataService.findPersonByLastName(any())).thenReturn(persons);
        this.mockMvc.perform(get("/person/Mary"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].id").exists())
            .andExpect(jsonPath("$[0].firstName").value("Mary"))
            .andExpect(jsonPath("$[0].lastName").value("Smith"))
            .andExpect(jsonPath("$[1].id").exists())
            .andExpect(jsonPath("$[1].firstName").value("Mary"))
            .andExpect(jsonPath("$[1].lastName").value("Evan"));
    }
    
    @Test
    public void addPerson() throws Exception {
        Person p = new Person("Mary","Evan");
		when(personDataService.addPerson(any(),any())).thenReturn(Optional.ofNullable(p));
        this.mockMvc.perform(post("/person").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(p)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("id").exists())
            .andExpect(jsonPath("firstName").value("Mary"))
            .andExpect(jsonPath("lastName").value("Evan"));
    }
    
    @Test
    public void addPersonWhenPersonExists() throws Exception {
        Person p = new Person("Mary","Smith");
        when(personDataService.findPerson(any(),any())).thenReturn(Optional.ofNullable(p));
        this.mockMvc.perform(post("/person").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(p)))
            .andDo(print())
            .andExpect(status().isConflict())
            .andExpect(jsonPath("id").doesNotExist())
            .andExpect(jsonPath("firstName").doesNotExist())
            .andExpect(jsonPath("lastName").doesNotExist());
    }
    
    
    @Test
    public void updatePersonByLastNameNoMatchFound() throws Exception {
    	PersonRequest personRequest = new PersonRequest();
    	personRequest.setFirstName("Tiger");
        when(personDataService.updateFirstName(any(),any())).thenReturn(Collections.emptyList());
        this.mockMvc.perform(patch("/person/a").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(personRequest)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }
    
    @Test
    public void updatePersonByLastNameOneMatch() throws Exception {
    	
    	PersonRequest personRequest = new PersonRequest();
    	personRequest.setFirstName("Tiger");
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Tiger","Smith"));
		when(personDataService.updateFirstName(any(),any())).thenReturn(persons);
        this.mockMvc.perform(patch("/person/Mary").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(personRequest)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].id").exists())
            .andExpect(jsonPath("$[0].firstName").value("Tiger"))
            .andExpect(jsonPath("$[0].lastName").value("Smith"));
    }
    
    @Test
    public void updatePersonByLastNameMoreMatch() throws Exception {
    	PersonRequest personRequest = new PersonRequest();
    	personRequest.setFirstName("Tiger");
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Tiger","Evan"));
        persons.add(new Person("Tiger","Evan"));
		when(personDataService.updateFirstName(any(),any())).thenReturn(persons);
        this.mockMvc.perform(patch("/person/Mary").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(personRequest)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].id").exists())
            .andExpect(jsonPath("$[0].firstName").value("Tiger"))
            .andExpect(jsonPath("$[0].lastName").value("Evan"))
            .andExpect(jsonPath("$[1].id").exists())
            .andExpect(jsonPath("$[1].firstName").value("Tiger"))
            .andExpect(jsonPath("$[1].lastName").value("Evan"));
    }
}
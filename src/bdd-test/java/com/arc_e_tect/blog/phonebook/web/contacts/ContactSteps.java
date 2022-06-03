package com.arc_e_tect.blog.phonebook.web.contacts;

import com.arc_e_tect.blog.phonebook.domain.Contact;
import com.arc_e_tect.blog.phonebook.repository.ContactRepository;
import com.arc_e_tect.blog.phonebook.repository.SequenceRepository;
import com.arc_e_tect.blog.phonebook.web.StepData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.flogger.Flogger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Flogger
public class ContactSteps {
    @Autowired
    protected StepData stepData;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private SequenceRepository sequenceRepository;

    @Autowired
    private ContactsHttpClient httpClient;

    @Before
    public void setup() {
        log.atInfo().log("Clearing database.");
        contactRepository.deleteAll();
        sequenceRepository.deleteAll();
    }

    @Given("the phonebook is empty")
    public void the_phonebook_is_empty() {
    }

    @Given("the contact {string} is listed in the phonebook")
    public void the_contact_is_listed_in_the_phonebook(String contact) {
        log.atInfo().log("Number of contacts stored before save: %d", contactRepository.count());
        Contact newContact = new Contact();
        newContact.setName(contact);
        contactRepository.save(newContact);
        log.atInfo().log("Number of contacts stored after save : %d", contactRepository.count());
    }

    @When("all contacts are requested")
    public void the_api_consumer_requests() throws IOException {
        httpClient.getAll();
    }

    @Then("the response contains no contacts")
    public void the_response_contains_no_contacts() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(httpClient.getBody());
        JsonNode contactsNode = rootNode.path("_embedded").path("contacts");
        assertEquals(0, contactsNode.size());
    }

    @Then("the response contains the contact {string}")
    public void the_response_contains_the_contact(String name) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(httpClient.getBody());
        JsonNode contactsNode = rootNode.path("_embedded").path("contacts");
        Iterator<JsonNode> it = contactsNode.elements();
        boolean found = false;
        while (it.hasNext()) {
            JsonNode contactNode = it.next();
            String contactName = contactNode.path("name").asText();
            if (name.equals(contactName)) {
                found = true;
            }
        }
        assertTrue(found);
    }
}

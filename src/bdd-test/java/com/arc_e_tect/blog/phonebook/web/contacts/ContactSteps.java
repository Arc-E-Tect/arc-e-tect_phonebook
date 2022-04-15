package com.arc_e_tect.blog.phonebook.web.contacts;

import com.arc_e_tect.blog.phonebook.web.StepData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.flogger.Flogger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Flogger
public class ContactSteps {
    @Autowired
    protected StepData stepData;

    @Autowired
    private ContactsHttpClient httpClient;

    @Given("the phonebook is empty")
    public void the_phonebook_is_empty() {
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

}

package com.arc_e_tect.blog.phonebook.web;

import com.arc_e_tect.blog.phonebook.common.IndexHttpClient;
import com.arc_e_tect.blog.phonebook.common.StepData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class IndexSteps {
    @Autowired
    private IndexHttpClient httpClient;

    @Autowired
    StepData stepData;

    @When("the client calls index page")
    public void the_client_calls_index_page() throws IOException {
        httpClient.getRoot();
    }

    @Then("the response contains links to {string}")
    public void the_response_contains_links_to(String expected) throws JsonProcessingException {
        JsonNode rootNode = stepData.getResponseJsonNode();
        JsonNode linksNode = rootNode.path("_links");

        JsonNode expectedNode = linksNode.path(expected);

        assertFalse(expectedNode.isMissingNode(), "The expected link was found.");
    }
}

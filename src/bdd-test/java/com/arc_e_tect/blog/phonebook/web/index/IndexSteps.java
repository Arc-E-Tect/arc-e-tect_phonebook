package com.arc_e_tect.blog.phonebook.web.index;

import com.arc_e_tect.blog.phonebook.web.AbstractSteps;
import com.arc_e_tect.blog.phonebook.web.IndexHttpClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class IndexSteps extends AbstractSteps {
    @Autowired
    private IndexHttpClient httpClient;

    @When("the API consumer requests {string}")
    public void the_api_consumer_requests(String string) {
        httpClient.executeGet(string);
    }

    @Then("the response contains a link to {string}")
    public void the_response_contains_a_link_to(String expected) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(httpClient.getBody());
        JsonNode linksNode = rootNode.path("_links");

        JsonNode expectedNode = linksNode.path(expected);

        assertFalse(expectedNode.isMissingNode(), "The expected link was found.");
    }
}

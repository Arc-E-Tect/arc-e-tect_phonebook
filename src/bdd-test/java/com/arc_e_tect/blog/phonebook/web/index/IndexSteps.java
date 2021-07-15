package com.arc_e_tect.blog.phonebook.web.index;

import com.arc_e_tect.blog.phonebook.TestSupportFunctions;
import com.arc_e_tect.blog.phonebook.web.AbstractSteps;
import com.arc_e_tect.blog.phonebook.web.IndexHttpClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IndexSteps extends AbstractSteps {
    @Autowired
    private IndexHttpClient httpClient;

    @When("the API consumer requests {string}")
    public void the_api_consumer_requests(String string) {
        httpClient.executeGet(string);
    }

    @Then("the response is the same as if requesting {string}")
    public void the_response_is_the_same_as_if_requesting(String alternativeLink) throws IOException, JSONException {
        JsonNode rootNode = stepData.getResponseJsonNode();
        httpClient.executeGet(alternativeLink);
        JsonNode altNode = stepData.getResponseJsonNode();

        TestSupportFunctions.matchJson(rootNode.asText(), altNode.asText());
    }

    @Then("the response contains a link to {string}")
    public void the_response_contains_a_link_to(String expected) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = stepData.getMapper().readTree(httpClient.getBody());
        JsonNode linksNode = rootNode.path("_links");

        JsonNode expectedNode = linksNode.path(expected);

        assertFalse(expectedNode.isMissingNode(), String.format( "The expected link %s was found.", expected));
    }

    @Then("the relative path to {string} is {string}")
    public void the_relative_path_to_is(String link, String expected) throws JsonProcessingException {
        JsonNode rootNode = stepData.getResponseJsonNode();
        JsonNode linksNode = rootNode.path("_links");

        JsonNode linkNode = linksNode.path(link);
        JsonNode referenceNode = linkNode.path("href");
        String referenceString = referenceNode.asText();

        String baseUrl = String.format("%s:%d",stepData.getBaseurl(), stepData.getServerPort());

        referenceString = referenceString.substring(referenceString.indexOf(baseUrl) + baseUrl.length());

        assertTrue(referenceString.equals(expected), String.format("The relative path for %s is set to %s",link,expected));
    }
}

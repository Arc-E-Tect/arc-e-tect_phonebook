package com.arc_e_tect.blog.phonebook.web.index;

import com.arc_e_tect.blog.phonebook.TestSupportFunctions;
import com.arc_e_tect.blog.phonebook.commons.MockServerExpectations;
import com.arc_e_tect.blog.phonebook.web.StepData;
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

public class IndexSteps {
    @Autowired
    protected StepData stepData;

    @Autowired
    private IndexHttpClient httpClient;

    @When("the API consumer requests the root")
    public void the_api_consumer_requests_the_root() {
        MockServerExpectations.create_GetRootInfo();
        httpClient.getRoot();
        MockServerExpectations.remove_GetRootInfo();
    }

    @When("the API consumer requests the index")
    public void the_api_consumer_requests_the_index() {
        MockServerExpectations.create_GetIndexInfo();
        httpClient.getIndex();
        MockServerExpectations.remove_GetIndexInfo();
    }

    @Then("the response is the same as if requesting {string}")
    public void the_response_is_the_same_as_if_requesting(String alternativeLink) throws IOException, JSONException {
        JsonNode rootNode = stepData.getResponseJsonNode();
        MockServerExpectations.create_GetIndexInfo();
        httpClient.executeGet(alternativeLink);
        MockServerExpectations.remove_GetIndexInfo();
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

        String baseUrl = String.format("%s:%d",stepData.getBaseurl(), stepData.getMockPort());

        referenceString = referenceString.substring(referenceString.indexOf(baseUrl) + baseUrl.length());

        assertTrue(referenceString.equals(expected), String.format("The relative path for %s is set to %s",link,expected));
    }
}

package com.arc_e_tect.blog.phonebook.web.contacts;

import com.arc_e_tect.blog.phonebook.commons.MockServerExpectations;
import com.arc_e_tect.blog.phonebook.resource.ContactResource;
import com.arc_e_tect.blog.phonebook.web.StepData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.flogger.Flogger;
import org.mockserver.client.MockServerClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Flogger
public class ContactSteps {
    @Autowired
    protected StepData stepData;

    @Autowired
    private ContactsHttpClient httpClient;

    @DataTableType(replaceWithEmptyString = "[blank]")
    public String stringType(String cell) {
        return cell;
    }

    @Before
    public void setup() {
    }

    @After
    public void teardown() {
        new MockServerClient("localhost",9091).reset();
    }

    @Given("the listed contacts")
    public void the_listed_contacts(io.cucumber.datatable.DataTable dataTable) {
        int contactId=0;
        List<Map<String, String>> signUpForms = dataTable.asMaps(String.class, String.class);

        Iterator<Map<String, String>> it = signUpForms.iterator();
        List<ContactResource> contactList = new ArrayList<>();
        while (it.hasNext()) {
            Map<String,String> item = it.next();

            String name = item.get("name");
            String phone = item.get("phone");
            ContactResource resource = new ContactResource();
            resource.setId(contactId++);
            resource.setName(name);
            resource.setPhone(phone);

            contactList.add(resource);
        }

        MockServerExpectations.create_ListedContacts(contactList);

    }

    @When("all contacts are requested")
    public void the_api_consumer_requests() throws IOException {
        httpClient.getAll();
    }

    @Then("the response contains a contact {string} with phone {string}")
    public void the_response_contains_a_contact_with_phone(String name, String phone) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(httpClient.getBody());
        JsonNode contactsNode = rootNode.path("_embedded").path("contacts");
        Iterator<JsonNode> it = contactsNode.elements();
        boolean found = false;
        while (it.hasNext()) {
            JsonNode contactNode = it.next();
            String contactName = contactNode.path("name").asText();
            String contactPhone = contactNode.path("phone").asText();
            if (name.equals(contactName) && phone.equals(contactPhone)) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }
}

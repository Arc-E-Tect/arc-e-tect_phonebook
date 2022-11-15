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
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.arc_e_tect.blog.phonebook.commons.MockServerExpectations.contactPostTemplate;
import static org.junit.jupiter.api.Assertions.*;

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
        new MockServerClient("localhost",9091).reset();
    }

    @After
    public void teardown() {
        new MockServerClient("localhost",9091).reset();
    }

    @Given("the listed contact(s)")
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
            resource.setName(name);
            resource.setPhone(phone);

            String id = item.get("id");
            if (id != null) {
                resource.setId(Long.parseLong(id));
            } else {
                resource.setId(++contactId);
            }

            contactList.add(resource);
        }

        stepData.setContactList(contactList);
    }

    @Given("the contact with id {long} is not listed in the phonebook")
    public void the_contact_with_id_is_not_listed_in_the_phonebook(Long id) {
        Iterator<ContactResource> it = stepData.getContactList().iterator();
        while (it.hasNext()) {
            ContactResource resource = it.next();
            if (resource.getId() == id) {
                it.remove();
            }
        }
    }

    @Given("the phonebook is empty")
    public void the_listed_contacts() {
        stepData.setContactList(new ArrayList<>());
    }

    @When("all contacts are requested")
    public void the_api_consumer_requests() throws IOException {
        MockServerExpectations.create_GetContacts(stepData.getContactList());
        httpClient.getAll();
    }

    @When("the contact with name {string} is requested")
    public void the_contact_with_name_is_requested(String name) throws IOException {
        MockServerExpectations.create_GetContacts(name, stepData.getContactList());
        httpClient.getSingleByName(name);
    }

    @When("the contact with id {long} is requested")
    public void the_contact_with_name_is_requested(long id) throws IOException {
        if (stepData.getContactList().isEmpty()) {
            MockServerExpectations.create_ContactNotFound("GET", id);
        } else {
            boolean found = false;
            Iterator<ContactResource> it = stepData.getContactList().iterator();
            while(it.hasNext()) {
                ContactResource resource = it.next();
                if (resource.getId() == id) {
                    found = true;
                    break;
                }
            }
            if (found) {
                MockServerExpectations.create_GetContact(id, stepData.getContactList());
            } else {
                MockServerExpectations.create_ContactNotFound("GET", id);
            }
        }
        httpClient.getSingleById(id);
    }

    @When("the contact with name {string} is deleted")
    public void the_contact_with_name_is_deleted(String name) {
        MockServerExpectations.create_DeleteContactByName(name);
        httpClient.deleteContactByName(name);
    }

    @When("the contact with id {int} is deleted")
    public void the_contact_with_id_is_deleted(int id) {
        MockServerExpectations.create_DeleteContactById(id);
        httpClient.deleteContactById(id);
    }

    @When("adding to the phonebook the contact")
    public void adding_to_the_phonebook_the_contact(io.cucumber.datatable.DataTable dataTable) {
        int contactId=0;
        List<Map<String, String>> signUpForms = dataTable.asMaps(String.class, String.class);

        Iterator<Map<String, String>> it = signUpForms.iterator();
        Map<String,String> item = it.next();

        String name = item.get("name");
        String phone = item.get("phone");
        ContactResource contactResource = new ContactResource();
        contactResource.setName(name);
        contactResource.setPhone(phone);

        String id = item.get("id");
        if (id != null) {
            contactResource.setId(Long.parseLong(id));
        } else {
            contactResource.setId(++contactId);
        }

        String requestJson = String.format(contactPostTemplate,
                contactResource.getId(), contactResource.getName(), contactResource.getPhone());

        boolean duplicate = false;
        Iterator<ContactResource> iterator = stepData.getContactList().iterator();
        while(iterator.hasNext()) {
            ContactResource resource = iterator.next();
            if (resource.getId() == contactResource.getId()) {
                duplicate=true;
            }
        }

        if (duplicate) {
            MockServerExpectations.create_PostContactConflict(contactResource);
        } else {
            MockServerExpectations.create_PostContact(contactResource);
        }

        httpClient.postNewContact(contactResource.getId(),requestJson);
    }

    @When("the phone number of contact {int} is changed to {string}")
    public void the_phone_number_of_contact_is_changed_to(int id, String phone) throws JsonProcessingException {
        ContactResource resource = null;
        Iterator<ContactResource> it = stepData.getContactList().iterator();
        while(it.hasNext()) {
            resource = it.next();
            if (resource.getId() == id) {
                resource.setPhone(phone);
                MockServerExpectations.create_PatchPhone(resource);
                resource.setName(null);
                httpClient.patchContact(id,resource);
                break;
            }
        }

        if (resource == null) {
            MockServerExpectations.create_ContactNotFound("PATCH", id);
            httpClient.patchContact(id, resource);
        }
    }

    @When("the name of contact {int} is changed to {string}")
    public void the_name_of_contact_is_changed_to(int id, String newName) throws JsonProcessingException {
        Iterator<ContactResource> it = stepData.getContactList().iterator();
        while(it.hasNext()) {
            ContactResource resource = it.next();
            if (resource.getId() == id) {
                resource.setName(newName);
                MockServerExpectations.create_PatchName(resource);

                resource.setPhone(null);
                httpClient.patchContact(id,resource);
                break;
            }
        }
    }

    @When("contact {int} is changed with data of contact {int}")
    public void contact_is_changed_with_data_of_contact(int id, int otherId) throws JsonProcessingException {
        Iterator<ContactResource> it = stepData.getContactList().iterator();
        while(it.hasNext()) {
            ContactResource resource = it.next();
            if (resource.getId() == id) {
                resource.setId(otherId);
                MockServerExpectations.create_PatchContactUnprocessable(resource, id);
                httpClient.patchContact(id,resource);
                break;
            }
        }
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

    @Then("the response contains the contact {string} with phone {string}")
    public void the_response_contains_the_contact_with_phone(String name, String phone) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode contactNode = objectMapper.readTree(httpClient.getBody());
        String contactName = contactNode.path("name").asText();
        String contactPhone = contactNode.path("phone").asText();

        assertAll("Contact",
                () -> assertEquals(name, contactName),
                () -> assertEquals(phone, contactPhone)
        );
    }

    @Then("the response contains the new contact {string} with phone {string}")
    public void the_response_contains_the_new_contact_with_phone(String name, String phone) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode contactNode = objectMapper.readTree(httpClient.getBody());
        String contactName = contactNode.path("name").asText();
        String contactPhone = contactNode.path("phone").asText();

        assertAll("Contact",
                () -> assertEquals(name, contactName),
                () -> assertEquals(phone, contactPhone)
        );
    }

    @Then("the response contains a single contact with id {long}")
    public void the_response_contains_a_single_contact_with_id(Long id) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode contactNode = objectMapper.readTree(httpClient.getBody());
        long contactId = contactNode.path("id").asLong();
        assertEquals(id, contactId);
    }

    @Then("the phonebook does not contain a contact with name {string}")
    public void the_phonebook_does_not_contain_a_contact_with_name(String name) {
    }

    @Then("the phonebook does not contain a contact with id {int}")
    public void the_phonebook_does_not_contain_a_contact_with_id(int id) {
    }

    @Then("the response is an error indicating that the contact could not be found")
    public void the_response_is_an_error_indicating_that_the_contact_could_not_be_found() {
        assertEquals(HttpStatus.NOT_FOUND, httpClient.getHttpStatus());
    }

    @Then("the response is an error indicating that the contact already exists")
    public void the_response_is_an_error_indicating_that_the_contact_already_exists() {
        assertEquals(HttpStatus.CONFLICT, httpClient.getHttpStatus());
    }

    @Then("the response is an error indicating that the contact could not be patched")
    public void the_response_is_an_error_indicating_that_the_contact_could_not_be_patched() {
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, httpClient.getHttpStatus());
    }
}

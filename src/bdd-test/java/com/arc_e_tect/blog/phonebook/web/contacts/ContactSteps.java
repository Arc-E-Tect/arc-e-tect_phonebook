package com.arc_e_tect.blog.phonebook.web.contacts;

import com.arc_e_tect.blog.phonebook.domain.TestContact;
import com.arc_e_tect.blog.phonebook.resource.ContactResource;
import com.arc_e_tect.blog.phonebook.web.StepData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import io.cucumber.java.Before;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.flogger.Flogger;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static org.junit.jupiter.api.Assertions.*;

@Flogger
public class ContactSteps {
    @Autowired
    protected StepData stepData;

    @Autowired
    private ContactsHttpClient httpClient;

    MongoClient mongoClient;
    MongoDatabase mongoDatabase;
    CodecRegistry pojoCodecRegistry;
    MongoCollection<TestContact> collection;

    private long contactId = 1;
    private TestContact contact;

    @DataTableType(replaceWithEmptyString = "[blank]")
    public String stringType(String cell) {
        return cell;
    }

    @Before
    public void setup() {
        contactId = 1;
        contact = new TestContact();
        log.atInfo().log("Clearing database.");
        mongoClient = MongoClients.create("mongodb://localhost:37017");
        pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        mongoDatabase = mongoClient.getDatabase("ContactsDB").withCodecRegistry(pojoCodecRegistry);
        collection = mongoDatabase.getCollection("contact", TestContact.class);
        collection.drop();
    }

    @Given("the phonebook is empty")
    public void the_phonebook_is_empty() {
        collection.drop();
    }

    @Given("the contact with name {string} is listed in the phonebook")
    public void the_contact_with_name_is_listed_in_the_phonebook(String name) {
        TestContact newContact = new TestContact();
        newContact.setName(name);
        newContact.setId(contactId++);
        collection.insertOne(newContact);
    }

    @Given("the contact with name {string} is not listed in the phonebook")
    public void the_contact_with_name_is_not_listed_in_the_phonebook(String name) {
        Bson query = eq("name", name);
        try {
            DeleteResult result = collection.deleteOne(query);
            log.atInfo().log("Deleted document count: %S", result.getDeletedCount());
        } catch (MongoException me) {
            log.atWarning().log("Unable to delete due to an error: %s", me);
        }
    }

    @Given("the unlisted contact")
    public void the_unlisted_contact(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> signUpForms = dataTable.asMaps(String.class, String.class);

        String name = signUpForms.get(0).get("name");

        Bson query = eq("name", name);
        try {
            DeleteResult result = collection.deleteOne(query);
            log.atInfo().log("Deleted document count: %S", result.getDeletedCount());
        } catch (MongoException me) {
            log.atWarning().log("Unable to delete due to an error: %s", me);
        }
    }

    @When("the contact with name {string} is added to the phonebook")
    public void the_contact_with_name_is_added_to_the_phonebook(String name) throws IOException {
        ContactResource resource = new ContactResource();
        resource.setName(name);
        httpClient.postNewContact(resource);
    }

    @When("the contact is added to the phonebook")
    public void the_contact_is_added_to_the_phonebook(io.cucumber.datatable.DataTable dataTable) throws IOException {
        List<Map<String, String>> signUpForms = dataTable.asMaps(String.class, String.class);

        String name = signUpForms.get(0).get("name");
        String phone = signUpForms.get(0).get("phone");

        ContactResource resource = new ContactResource();
        resource.setId(contactId++);
        resource.setName(name);
        resource.setPhone(phone);
        httpClient.postNewContact(resource);
    }

    @When("the contact with no name is added to the phonebook")
    public void the_contact_with_no_name_is_added_to_the_phonebook() throws IOException {
        ContactResource resource = new ContactResource();
        httpClient.postNewContact(resource);
    }

    @Given("the listed contact")
    public void the_listed_contact(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> signUpForms = dataTable.asMaps(String.class, String.class);

        String name = signUpForms.get(0).get("name");
        String phone = signUpForms.get(0).get("phone");

        contact.setId(contactId++);
        contact.setName(name);
        contact.setPhone(phone);
        collection.insertOne(contact);
    }

    @When("the phone number of contact {string} is changed to {string}")
    public void the_phone_number_of_contact_is_changed_to(String name, String phone) throws JsonProcessingException {
        ContactResource resource = new ContactResource(0, name, phone);
        httpClient.patchContact(name,resource);
    }

    @When("the name of contact {string} is changed to {string}")
    public void the_name_of_contact_is_changed_to(String name, String newName) throws JsonProcessingException {
        ContactResource resource = new ContactResource(0, newName, null);
        httpClient.patchContact(name,resource);
    }

    @When("the contact with name {string} is deleted")
    public void the_contact_with_name_is_deleted(String name) {
        httpClient.deleteContact(name);
    }

    @When("all contacts are requested")
    public void the_api_consumer_requests() throws IOException {
        httpClient.getAll();
    }

    @When("the contact with name {string} is requested")
    public void the_contact_with_name_is_requested(String name) throws IOException {
        httpClient.getSingleByName(name);
    }

    @Then("the contact with phone {string} has name {string}")
    public void the_contact_with_phone_has_name(String phone, String name) {
        Bson query = eq("name", name);
        try {
            TestContact found = collection.find(query).first();
            assertAll("Found with phone and name",
                    () -> assertNotNull(found),
                    () -> assertEquals(phone, found.getPhone()));
        } catch (MongoException me) {
            log.atWarning().log("Unable to find due to an error: %s", me);
        }
    }

    @Then("the response contains no contacts")
    public void the_response_contains_no_contacts() {
        assertEquals(204, httpClient.getHttpStatus().value());
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

    @Then("the contact cannot be found")
    public void the_contact_cannot_be_found() {
        assertEquals(HttpStatus.NOT_FOUND, httpClient.getHttpStatus());
    }

    @Then("the response contains no contact")
    public void the_response_contains_no_contact() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode contactNode = objectMapper.readTree(httpClient.getBody());
        assertAll("No Contact",
                () -> assertNull(contactNode.get("id")),
                () -> assertNull(contactNode.get("name")));
    }

    @Then("the phonebook contains the contact with name {string}")
    public void the_phonebook_contains_the_contact_with_name(String name) {
        Bson query = eq("name", name);
        try {
            TestContact found = collection.find(query).first();
            assertAll("Found with name",
                    () -> assertNotNull(found),
                    () -> assertEquals(name, found.getName()));
        } catch (MongoException me) {
            log.atWarning().log("Unable to find due to an error: %s", me);
        }
    }

    @Then("the phonebook does not contain the contact with name {string}")
    public void the_phonebook_does_not_contain_the_contact_with_name(String name) {
        Bson query = eq("name", name);
        try {
            TestContact found = collection.find(query).first();
            assertNull(found);
        } catch (MongoException me) {
            log.atWarning().log("Unable to find due to an error: %s", me);
        }
    }

    @Then("the phonebook does not contain a contact with no name")
    public void the_phonebook_does_not_contain_a_contact_with_no_name() {
        Bson query = eq("name", "");
        try {
            TestContact found = collection.find(query).first();
            assertNull(found);
        } catch (MongoException me) {
            log.atWarning().log("Unable to find due to an error: %s", me);
        }
    }

    @Then("the response contains the contact with name {string}")
    public void the_response_contains_the_contact_with_name(String name) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode contactNode = objectMapper.readTree(httpClient.getBody());
        String contactName = contactNode.path("name").asText();
        assertEquals(name, contactName);
    }

    @Then("the response contains the contact with phone {string}")
    public void the_response_contains_the_contact_with_phone(String phone) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode contactNode = objectMapper.readTree(httpClient.getBody());
        String contactPhone = contactNode.path("phone").asText();
        assertEquals(phone, contactPhone);
    }

    @Then("the contact with name {string} has phone number {string}")
    public void the_contact_with_name_has_phone(String name, String phone) {
        Bson query = eq("name", name);
        try {
            TestContact found = collection.find(query).first();
            assertAll("Found with name and phone",
                    () -> assertNotNull(found),
                    () -> assertEquals(phone, found.getPhone()));
        } catch (MongoException me) {
            log.atWarning().log("Unable to find due to an error: %s", me);
        }
    }

    @Then("the contact with name {string} has no phone number")
    public void the_contact_with_name_has_phone(String name) {
        Bson query = eq("name", name);
        try {
            TestContact found = collection.find(query).first();
            assertAll("Found with name and no phone",
                    () -> assertNotNull(found),
                    () -> assertNull(found.getPhone()));
        } catch (MongoException me) {
            log.atWarning().log("Unable to find due to an error: %s", me);
        }
    }

    @Then("the contact formerly known as {string} now has name {string}")
    public void the_contact_formerly_known_as_now_has_name(String oldName, String newName) {
        Bson query = eq("name", newName);
        try {
            TestContact found = collection.find(query).first();
            assertAll("Found with name and phone",
                    () -> assertNotNull(found),
                    () -> assertEquals(contact.getId(), found.getId()),
                    () -> assertEquals(contact.getPhone(), found.getPhone())
            );
        } catch (MongoException me) {
            log.atWarning().log("Unable to find due to an error: %s", me);
        }
    }

    @Then("the response is an error indicating that invalid contact data was provided")
    public void the_response_is_an_error_indicating_that_invalid_contact_data_was_provided() {
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, httpClient.getHttpStatus());
    }

    @Then("the response is an error indicating that a contact with the same name already exists")
    public void the_response_is_an_error_indicating_that_a_contact_with_the_same_name_already_exists() {
        assertEquals(HttpStatus.CONFLICT, httpClient.getHttpStatus());
    }
}

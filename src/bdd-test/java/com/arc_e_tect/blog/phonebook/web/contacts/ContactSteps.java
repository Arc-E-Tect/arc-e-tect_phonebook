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
import com.mongodb.client.result.InsertOneResult;
import io.cucumber.java.Before;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.flogger.Flogger;
import org.bson.BsonValue;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.ArrayList;
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
            TestContact testContact = new TestContact();
            testContact.setName(name);
            testContact.setPhone(phone);

            String id = item.get("id");
            if (id != null) {
                testContact.setId(Long.parseLong(id));
            } else {
                testContact.setId(++contactId);
            }
            InsertOneResult result = collection.insertOne(testContact);
            BsonValue instertedID = result.getInsertedId();
        }
    }

    @Given("the contact with id {long} is not listed in the phonebook")
    public void the_contact_with_id_is_not_listed_in_the_phonebook(Long id) {
        Bson query = eq("id", id);
        try {
            DeleteResult result = collection.deleteOne(query);
            log.atInfo().log("Deleted document count: %S", result.getDeletedCount());
        } catch (MongoException me) {
            log.atWarning().log("Unable to delete due to an error: %s", me);
        }
    }

    @When("the contact with id {long} is requested")
    public void the_contact_with_name_is_requested(long id) throws IOException {
        httpClient.getSingleById(id);
    }

    @When("the contact with name {string} is requested")
    public void the_contact_with_name_is_requested(String name) throws IOException {
        httpClient.getSingleByName(name);
    }

    @When("all contacts are requested")
    public void all_contacts_are_requested() throws IOException {
        httpClient.getAll();
    }

    @When("the contact with name {string} is deleted")
    public void the_contact_with_name_is_deleted(String name) {
        httpClient.deleteContactByName(name);
    }

    @When("the contact with id {int} is deleted")
    public void the_contact_with_id_is_deleted(int id) {
        httpClient.deleteContactById(id);
    }

    @Then("the response is an error indicating that the contact could not be found")
    public void the_response_is_an_error_indicating_that_the_contact_could_not_be_found() {
        assertEquals(HttpStatus.NOT_FOUND, httpClient.getHttpStatus());
    }

    @Then("the response contains a/the single contact with id {long}")
    public void the_response_contains_a_single_contact_with_id(Long id) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode contactNode = objectMapper.readTree(httpClient.getBody());
        long contactId = contactNode.path("id").asLong();
        assertEquals(id, contactId);
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

    @Then("{int} contacts are retrieved")
    public void contacts_are_retrieved(Integer expectedNumberOfContacts) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(httpClient.getBody());
        JsonNode contactsNode = rootNode.path("_embedded").path("contacts");
        assertEquals((int) expectedNumberOfContacts, contactsNode.size());
    }

    @Then("the phonebook does not contain a/the contact with name {string}")
    public void the_phonebook_does_not_contain_the_contact_with_name(String name) {
        Bson query = eq("name", name);
        try {
            TestContact found = collection.find(query).first();
            assertNull(found);
        } catch (MongoException me) {
            log.atWarning().log("Unable to find due to an error: %s", me);
        }
    }

    @Then("the phonebook does not contain a/the contact with id {long}")
    public void the_phonebook_does_not_contain_a_contact_with_id(Long id) {
        Bson query = eq("id", id);
        try {
            TestContact found = collection.find(query).first();
            assertNull(found);
        } catch (MongoException me) {
            log.atWarning().log("Unable to find due to an error: %s", me);
        }
    }

}

package com.arc_e_tect.blog.phonebook.web.contacts;

import com.arc_e_tect.blog.phonebook.domain.TestContact;
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
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.flogger.Flogger;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Iterator;

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

    @Before
    public void setup() {
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

    @Given("the contact {string} is listed in the phonebook")
    public void the_contact_is_listed_in_the_phonebook(String contact) {
        TestContact newContact = new TestContact();
        newContact.setName(contact);
        newContact.setId(1L);
        collection.insertOne(newContact);
    }

    @Given("the contact with id {long} is listed in the phonebook")
    public void the_contact_with_id_is_listed_in_the_phonebook(Long id) {
        TestContact newContact = new TestContact();
        newContact.setName("John Doe");
        newContact.setId(id);
        collection.insertOne(newContact);
    }

    @Given("the contact with id {long} is not listed in the phonebook")
    public void the_contact_with_id_is_not_listed_in_the_phonebook(Long id) {
        Bson query = eq("id", Long.toString(id));
        try {
            DeleteResult result = collection.deleteOne(query);
            log.atInfo().log("Deleted document count: %S", result.getDeletedCount());
        } catch (MongoException me) {
            log.atWarning().log("Unable to delete due to an error: %s", me);
        }
    }

    @When("all contacts are requested")
    public void the_api_consumer_requests() throws IOException {
        httpClient.getAll();
    }

    @When("the contact with id {long} is requested")
    public void the_contact_with_id_is_requested(Long id) throws IOException {
        httpClient.getSingleById(id);
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

    @Then("the response contains the contact with id {long}")
    public void the_response_contains_the_contact_with_id(Long id) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode contactNode = objectMapper.readTree(httpClient.getBody());
        Long contactName = contactNode.path("id").asLong();
        assertEquals(id, contactName);
    }

    @Then("the contact cannot be found")
    public void the_contact_cannot_be_found() {
        assertEquals(404, httpClient.getHttpStatus().value());
    }

    @Then("the response contains no contact")
    public void the_response_contains_no_contact() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode contactNode = objectMapper.readTree(httpClient.getBody());
        assertAll("No Contact",
                () -> assertNull(contactNode.get("id")),
                () -> assertNull(contactNode.get("name")));
    }
}

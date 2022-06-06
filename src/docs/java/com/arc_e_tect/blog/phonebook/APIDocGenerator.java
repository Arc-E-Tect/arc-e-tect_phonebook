package com.arc_e_tect.blog.phonebook;

import com.arc_e_tect.blog.phonebook.domain.Contact;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.extern.flogger.Flogger;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.hypermedia.LinksSnippet;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Flogger
@ExtendWith({ RestDocumentationExtension.class, SpringExtension.class })
@SpringBootTest(classes = ArcETectPhonebookApplication.class)
@ActiveProfiles({"docgen"})
public class APIDocGenerator {

    private MockMvc mockMvc;

    MongoClient mongoClient;
    MongoDatabase mongoDatabase;
    CodecRegistry pojoCodecRegistry;
    MongoCollection<Contact> collection;

    public APIDocGenerator() {
    }

    protected final LinksSnippet indexLinks = links(
            linkWithRel("self").description("The URL to the index page itself."),
            linkWithRel("contacts").description("The URL referring to the list of Contacts in the Phonebook.")
    );

    protected final LinksSnippet contactLinks = links(
            linkWithRel("self").description("The URL to the endpoint with to retrieve all contacts.")
    );

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation).uris()
                        .withPort(9090))
                .alwaysDo(document("{method-name}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
                .build();
        mongoClient = MongoClients.create("mongodb://localhost:37017");
        pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        mongoDatabase = mongoClient.getDatabase("ContactsDB").withCodecRegistry(pojoCodecRegistry);
        collection = mongoDatabase.getCollection("contact", Contact.class);
        collection.drop();
    }

    @Test
    void getAllEndpoints() throws Exception {
        this.mockMvc.perform(get("/index")
                .contentType(MediaType.parseMediaType("application/hal+json"))).andDo(print())
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("{method-name}",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        indexLinks
                ));
    }

    @Test
    void getAllContactsWhenPhonebookIsEmpty() throws Exception {
        collection.drop();
        this.mockMvc.perform(get("/contacts")
                        .contentType(MediaType.parseMediaType("application/hal+json"))).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.parseMediaType("application/hal+json")))
                .andDo(MockMvcRestDocumentationWrapper.document("{method-name}",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        links(halLinks(),
                                (linkWithRel("self").description("The URL to the endpoint to retrieve all contacts.")))
                ));
    }

    @Test
    void getAllContacts() throws Exception {
        Contact newContact = new Contact();
        newContact.setName("John Doe");
        newContact.setId(1l);
        collection.insertOne(newContact);
        this.mockMvc.perform(get("/contacts")
                        .contentType(MediaType.parseMediaType("application/hal+json"))).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.parseMediaType("application/hal+json")))
                .andDo(MockMvcRestDocumentationWrapper.document("{method-name}",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("_embedded").ignored().optional(),
                                subsectionWithPath("_embedded.contacts").description("The list of Contacts retrieved").optional().ignored(),
                                fieldWithPath("_embedded.contacts[].id").description("Unique id for the Contact. The id is unique within the context of the Arc-E-Tect phonebook.").type(JsonFieldType.NUMBER),
                                fieldWithPath("_embedded.contacts[].name").description("The name of the Contact. Typically this is the firstname and lastname combined.").type(JsonFieldType.STRING),
                                fieldWithPath("_embedded.contacts[].phone").description("The Contact's phone number. Typically this includes the country code, the area code and the subscriber code." +
                                        "The alias is for documentation purposes only").type(JsonFieldType.STRING).optional(),
                                subsectionWithPath("_links").ignored().optional()),
                        links(halLinks(),
                                (linkWithRel("self").description("The URL to the endpoint to retrieve all contacts.")))
                ));
    }
}

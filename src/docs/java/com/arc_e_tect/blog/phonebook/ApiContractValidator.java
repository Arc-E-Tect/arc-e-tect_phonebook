package com.arc_e_tect.blog.phonebook;

import com.arc_e_tect.blog.phonebook.domain.Contact;
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
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Flogger
@ExtendWith({ RestDocumentationExtension.class, SpringExtension.class })
@SpringBootTest(classes = ArcETectPhonebookApplication.class)
@ActiveProfiles({"docgen"})
public class ApiContractValidator {

    private MockMvc mockMvc;

    MongoClient mongoClient;
    MongoDatabase mongoDatabase;
    CodecRegistry pojoCodecRegistry;
    MongoCollection<Contact> collection;

    public ApiContractValidator() {
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
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        indexLinks
                ));
    }

    @Test
    void getAllContactsWhenPhonebookIsEmpty() throws Exception {
        collection.drop();
        this.mockMvc.perform(get("/contacts")
                        .contentType(MediaType.parseMediaType("application/hal+json"))).andDo(print())
                .andExpect(status().isNoContent())
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())
                ));
    }

    @Test
    void getAllContacts() throws Exception {
        Contact newContact = new Contact();
        newContact.setName("Peter Parker");
        newContact.setId(1l);
        newContact.setPhone("+1 (555) 748432");
        collection.insertOne(newContact);
        this.mockMvc.perform(get("/contacts")
                        .contentType(MediaType.parseMediaType("application/hal+json"))).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.parseMediaType("application/hal+json")))
                .andDo(MockMvcRestDocumentation.document("{method-name}",
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

    @Test
    void getSingleContact() throws Exception {
        Contact newContact = new Contact();
        newContact.setName("Peter Parker");
        newContact.setId(42l);
        newContact.setPhone("+1 (555) 748432");
        collection.insertOne(newContact);
        this.mockMvc.perform(get("/contacts/{contact_id}", newContact.getId())
                        .contentType(MediaType.parseMediaType("application/hal+json"))).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.parseMediaType("application/hal+json")))
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("contact_id")
                                .description("Unique id of the Contact that is to be retrieved.")),
                        responseFields(
                                fieldWithPath("id").description("Unique id for the Contact. The id is unique within the context of the Arc-E-Tect phonebook.").type(JsonFieldType.NUMBER),
                                fieldWithPath("name").description("The name of the Contact. Typically this is the firstname and lastname combined.").type(JsonFieldType.STRING),
                                fieldWithPath("phone").description("The Contact's phone number. Typically this includes the country code, the area code and the subscriber code." +
                                        "The alias is for documentation purposes only").type(JsonFieldType.STRING).optional(),
                                subsectionWithPath("_links").ignored().optional()),
                        links(halLinks(),
                                (linkWithRel("self").description("The URL to the endpoint to retrieve this contact.")),
                                (linkWithRel("contacts").description("The URL to the endpoint to retrieve all contacts.")))
                ));
    }

    @Test
    void getSingleContactEmptyPhonebook() throws Exception {
        collection.drop();
        this.mockMvc.perform(get("/contacts/{contact_id}", 666)
                        .contentType(MediaType.parseMediaType("application/hal+json"))).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.parseMediaType("application/hal+json")))
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("contact_id")
                                .description("Unique id of the Contact that is to be retrieved.")),
                        responseFields(
                                fieldWithPath("timestamp").description("The time the at which the error occurred. The timestamp is the server time.").type(JsonFieldType.STRING),
                                fieldWithPath("status").description("The http status related to the error.").type(JsonFieldType.STRING),
                                fieldWithPath("title").description("Short description of the error. Typically this is a one-liner.").type(JsonFieldType.STRING),
                                fieldWithPath("detail").description("Verbose description of the error.").type(JsonFieldType.STRING).optional(),
                                subsectionWithPath("_links").ignored().optional())
                ));
    }

    @Test
    void getUnlistedContact() throws Exception {
        collection.drop();
        this.mockMvc.perform(get("/contacts/{contact_id}", "666")
                        .contentType(MediaType.parseMediaType("application/hal+json"))).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.parseMediaType("application/hal+json")))
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("contact_id")
                                .description("Unique id of the Contact that is to be retrieved.")),
                        responseFields(
                                fieldWithPath("timestamp").description("The time the at which the error occurred. The timestamp is the server time.").type(JsonFieldType.STRING),
                                fieldWithPath("status").description("The http status related to the error.").type(JsonFieldType.STRING),
                                fieldWithPath("title").description("Short description of the error. Typically this is a one-liner.").type(JsonFieldType.STRING),
                                fieldWithPath("detail").description("Verbose description of the error.").type(JsonFieldType.STRING).optional(),
                                subsectionWithPath("_links").ignored().optional())
                ));
    }

    @Test
    void postNewContact() throws Exception {
        collection.drop();

        String jsonDoc =
                """
                {"name":"Peter Parker", "phone":"+1 (555) 748432"}
                """;

        this.mockMvc.perform(post("/contacts")
                        .content(jsonDoc)
                        .contentType(MediaType.parseMediaType("application/hal+json"))).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.parseMediaType("application/hal+json")))
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("id").description("Unique id for the Contact. The id is unique within the context of the Arc-E-Tect phonebook.").type(JsonFieldType.NUMBER),
                                fieldWithPath("name").description("The name of the Contact. Typically this is the firstname and lastname combined.").type(JsonFieldType.STRING),
                                fieldWithPath("phone").description("The Contact's phone number. Typically this includes the country code, the area code and the subscriber code.").type(JsonFieldType.STRING),
                                subsectionWithPath("_links").ignored().optional()),
                        links(halLinks(),
                                (linkWithRel("self").description("The URL to the endpoint to retrieve this contact.")),
                                (linkWithRel("contacts").description("The URL to the endpoint to retrieve all contacts.")))
                ));
    }

    @Test
    void postListedContact() throws Exception {
        Contact newContact = new Contact();
        newContact.setName("Peter Parker");
        newContact.setId(42l);
        newContact.setPhone("+1 (555) 748432");
        collection.insertOne(newContact);

        String jsonDoc =
                """
                {"id":42, "name":"Peter Parker", "phone":"+1 (555) 748432"}
                """;

        this.mockMvc.perform(post("/contacts")
                        .content(jsonDoc)
                        .contentType(MediaType.parseMediaType("application/hal+json"))).andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.parseMediaType("application/hal+json")))
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("timestamp").description("The time the at which the error occurred. The timestamp is the server time.").type(JsonFieldType.STRING),
                                fieldWithPath("status").description("The http status related to the error.").type(JsonFieldType.STRING),
                                fieldWithPath("title").description("Short description of the error. Typically this is a one-liner.").type(JsonFieldType.STRING),
                                fieldWithPath("detail").description("Verbose description of the error.").type(JsonFieldType.STRING).optional(),
                                subsectionWithPath("_links").ignored().optional())
                ));
    }

    @Test
    void patchListedContactNameAndPhone() throws Exception {
        Contact newContact = new Contact();
        newContact.setName("Peter Parker");
        newContact.setId(42l);
        newContact.setPhone("+1 (555) 748432");
        collection.insertOne(newContact);

        String jsonDoc =
                """
                {"id":42, "name":"John Stark", "phone":"+1 (555) 748432"}
                """;


        this.mockMvc.perform(patch("/contacts/{contact_id}", newContact.getId())
                        .content(jsonDoc)
                        .contentType(MediaType.parseMediaType("application/hal+json"))).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.parseMediaType("application/hal+json")))
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("contact_id")
                                .description("Unique id of the Contact that is to be patched.")),
                        responseFields(
                                fieldWithPath("id").description("Unique id for the Contact. The id is unique within the context of the Arc-E-Tect phonebook.").type(JsonFieldType.NUMBER),
                                fieldWithPath("name").description("The name of the Contact. Typically this is the firstname and lastname combined.").type(JsonFieldType.STRING),
                                fieldWithPath("phone").description("The Contact's phone number. Typically this includes the country code, the area code and the subscriber code.").type(JsonFieldType.STRING),
                                subsectionWithPath("_links").ignored().optional()),
                        links(halLinks(),
                                (linkWithRel("self").description("The URL to the endpoint to retrieve this contact.")),
                                (linkWithRel("contacts").description("The URL to the endpoint to retrieve all contacts.")))
                ));
    }

    @Test
    void patchListedContactName() throws Exception {
        Contact newContact = new Contact();
        newContact.setName("Peter Parker");
        newContact.setId(42l);
        newContact.setPhone("+1 (555) 748432");
        collection.insertOne(newContact);

        String jsonDoc = "{\"name\":\"John Stark\"}";
        this.mockMvc.perform(patch("/contacts/{contact_id}", newContact.getId())
                        .content(jsonDoc)
                        .contentType(MediaType.parseMediaType("application/hal+json"))).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.parseMediaType("application/hal+json")))
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("contact_id")
                                .description("Unique id of the Contact that is to be patched.")),
                        responseFields(
                                fieldWithPath("id").description("Unique id for the Contact. The id is unique within the context of the Arc-E-Tect phonebook.").type(JsonFieldType.NUMBER),
                                fieldWithPath("name").description("The name of the Contact. Typically this is the firstname and lastname combined.").type(JsonFieldType.STRING),
                                fieldWithPath("phone").description("The Contact's phone number. Typically this includes the country code, the area code and the subscriber code.").type(JsonFieldType.STRING),
                                subsectionWithPath("_links").ignored().optional()),
                        links(halLinks(),
                                (linkWithRel("self").description("The URL to the endpoint to retrieve this contact.")),
                                (linkWithRel("contacts").description("The URL to the endpoint to retrieve all contacts.")))
                ));
    }

    @Test
    void patchListedContactPhone() throws Exception {
        Contact newContact = new Contact();
        newContact.setName("Peter Parker");
        newContact.setId(42l);
        newContact.setPhone("+1 (555) 748432");
        collection.insertOne(newContact);

        String jsonDoc = "{\"phone\":\"+1 (555) 432748\"}";
        this.mockMvc.perform(patch("/contacts/{contact_id}", newContact.getId())
                        .content(jsonDoc)
                        .contentType(MediaType.parseMediaType("application/hal+json"))).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.parseMediaType("application/hal+json")))
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("contact_id")
                                .description("Name of the Contact that is to be patched.")),
                        responseFields(
                                fieldWithPath("id").description("Unique id for the Contact. The id is unique within the context of the Arc-E-Tect phonebook.").type(JsonFieldType.NUMBER),
                                fieldWithPath("name").description("The name of the Contact. Typically this is the firstname and lastname combined.").type(JsonFieldType.STRING),
                                fieldWithPath("phone").description("The Contact's phone number. Typically this includes the country code, the area code and the subscriber code.").type(JsonFieldType.STRING),
                                subsectionWithPath("_links").ignored().optional()),
                        links(halLinks(),
                                (linkWithRel("self").description("The URL to the endpoint to retrieve this contact.")),
                                (linkWithRel("contacts").description("The URL to the endpoint to retrieve all contacts.")))
                ));
    }

    @Test
    void deleteListedContact() throws Exception {
        Contact newContact = new Contact();
        newContact.setName("Peter Parker");
        newContact.setId(42l);
        newContact.setPhone("+1 (555) 748432");
        collection.insertOne(newContact);
        this.mockMvc.perform(delete("/contacts/{contact_id}", newContact.getId())
                .contentType(MediaType.parseMediaType("application/hal+json"))).andDo(print())
                .andExpect(status().isNoContent())
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("contact_id")
                                .description("Unique id of the Contact that is to be deleted."))
                ));
    }

    @Test
    void deleteListedContactByName() throws Exception {
        Contact newContact = new Contact();
        newContact.setName("Peter Parker");
        newContact.setId(42l);
        newContact.setPhone("+1 (555) 748432");
        collection.insertOne(newContact);
        this.mockMvc.perform(delete("/contacts?contactName={contact_name}", newContact.getName())
                .contentType(MediaType.parseMediaType("application/hal+json"))).andDo(print())
                .andExpect(status().isNoContent())
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        queryParameters(parameterWithName("contactName")
                                .description("Name of the Contact that is to be deleted."))
                ));
    }

    @Test
    void deleteUnlistedContact() throws Exception {
        this.mockMvc.perform(delete("/contacts/{contact_id}", 666)
                        .contentType(MediaType.parseMediaType("application/hal+json"))).andDo(print())
                .andExpect(status().isNoContent())
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("contact_id")
                                .description("Unique id of the Contact that is to be deleted."))
                ));
    }
}

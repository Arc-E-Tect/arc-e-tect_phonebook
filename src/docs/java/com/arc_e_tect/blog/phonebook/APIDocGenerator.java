package com.arc_e_tect.blog.phonebook;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import lombok.extern.flogger.Flogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.hypermedia.LinksSnippet;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Flogger
@ExtendWith({ RestDocumentationExtension.class, SpringExtension.class })
@SpringBootTest(classes = ArcETectPhonebookApplication.class)
@ActiveProfiles({"docgen"})
public class APIDocGenerator {

    private MockMvc mockMvc;

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
    void getAllContacts() throws Exception {
        this.mockMvc.perform(get("/contacts")
                        .contentType(MediaType.parseMediaType("application/hal+json"))).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.parseMediaType("application/hal+json")))
                .andDo(MockMvcRestDocumentationWrapper.document("{method-name}",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        links(linkWithRel("self").description("The URL to the endpoint with to retrieve all contacts."))
                ));
    }
}

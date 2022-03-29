package com.arc_e_tect.blog.phonebook.web.contacts;

import com.arc_e_tect.blog.phonebook.domain.Contact;
import com.arc_e_tect.blog.phonebook.resource.ContactResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@RestController
@RequestMapping(value= {"/contacts"})
public class ContactsController {
    HashMap<String, String> contacts;
    private final ContactsResourceAssembler resourceAssembler;

    @Autowired
    ContactsController(ContactsResourceAssembler resourceAssembler) {
        this.resourceAssembler = resourceAssembler;
    }

    @Operation(summary = "Get all Contacts.",
            description = "# Contact\n\nGet all **Contacts** stored in the _Arc-E-Tect_ Phonebook.\n\nContacts have three fields: an id, which is a number, a name, which is the fullname of the Contact and a phone-number.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Contact.class)) }),
            })
    @GetMapping(produces = {"application/hal+json", MediaType.APPLICATION_JSON_VALUE})
    public CollectionModel<ContactResource> getAllContacts(HttpServletResponse response) {
        return null;
    }

}

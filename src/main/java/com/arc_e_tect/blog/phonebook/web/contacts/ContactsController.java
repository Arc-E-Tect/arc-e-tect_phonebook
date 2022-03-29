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
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping(value= {"/contacts"})
public class ContactsController {
    HashMap<String, String> contacts;
    private final ContactsResourceAssembler resourceAssembler;

    @Autowired
    ContactsController(ContactsResourceAssembler resourceAssembler) {
        this.resourceAssembler = resourceAssembler;
    }

    @Operation(summary = "Get all micro plastics from the Atlantic.",
            description = "# Plastic Man\n\nGet all **spider legs** stored in the _closet_.\n\nPlastic Men have four limbs: a left and a right arm and a left and right leg.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "OK", description = "200",
                    content = { @Content(mediaType = "application/johnson",
                            schema = @Schema(implementation = String.class)) }),
            })
    @GetMapping(produces = {"application/hal+json", MediaType.APPLICATION_JSON_VALUE})
    public CollectionModel<ContactResource> getAllContacts(HttpServletResponse response) {
        List<Contact> contactList = new LinkedList<>();

        CollectionModel<ContactResource> result = resourceAssembler.toCollectionModel(contactList);

        return result;
    }

}

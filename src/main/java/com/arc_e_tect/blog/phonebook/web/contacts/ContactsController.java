package com.arc_e_tect.blog.phonebook.web.contacts;

import com.arc_e_tect.blog.phonebook.domain.Contact;
import com.arc_e_tect.blog.phonebook.resource.ContactResource;
import com.arc_e_tect.blog.phonebook.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(value= {"/contacts"})
public class ContactsController {
    HashMap<String, String> contacts;
    private final ContactsResourceAssembler resourceAssembler;

    private final ContactService contactService;

    @Autowired
    ContactsController(ContactService contactService, ContactsResourceAssembler resourceAssembler) {
        this.resourceAssembler = resourceAssembler;
        this.contactService = contactService;
    }

    @GetMapping(produces = {"application/hal+json", MediaType.APPLICATION_JSON_VALUE})
    public CollectionModel<ContactResource> getAllContacts(HttpServletResponse response) {
        List<Contact> contactList = contactService.retrieveAllContacts();

        if (contactList == null || contactList.size() == 0) {
            response.setStatus(204);
            return null;
        }

        CollectionModel<ContactResource> result = resourceAssembler.toCollectionModel(contactList);

        return result;
    }

    @GetMapping(value="/{id}", produces = {"application/hal+json", MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ContactResource> getSingleContactById(@PathVariable Long id, HttpServletResponse response) {
        Contact contact = contactService.getContactByID(id);

        ContactResource result = resourceAssembler.toModel(contact);

        return new ResponseEntity<ContactResource>(result, HttpStatus.OK);
    }
}

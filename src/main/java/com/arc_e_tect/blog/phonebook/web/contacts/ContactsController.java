package com.arc_e_tect.blog.phonebook.web.contacts;

import com.arc_e_tect.blog.phonebook.domain.Contact;
import com.arc_e_tect.blog.phonebook.resource.ContactResource;
import com.arc_e_tect.blog.phonebook.service.ContactService;
import com.arc_e_tect.blog.phonebook.service.exception.ContactNotFoundException;
import com.arc_e_tect.blog.phonebook.service.exception.DuplicateContactException;
import com.arc_e_tect.blog.phonebook.web.contacts.advisories.InvalidContactDataExcption;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public CollectionModel<ContactResource> getAllContacts(HttpServletResponse response,
                                                           @RequestParam String contactName) {
        List<Contact> contactList = null;
        if (contactName == null || "".equals(contactName)) {
            contactList = contactService.retrieveAllContacts();
        } else {
            contactList = contactService.retrieveAllContacts(contactName);
        }

        if (contactList.size() == 0) {
            response.setStatus(204);
            return null;
        }

        CollectionModel<ContactResource> result = resourceAssembler.toCollectionModel(contactList);

        return result;
    }

    @GetMapping(value="/{id}", produces = {"application/hal+json", MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ContactResource> getSingleContactById(@PathVariable Long id, HttpServletResponse response) {
        Contact contact = contactService.getContactById(id);

        ContactResource result = resourceAssembler.toModel(contact);

        return new ResponseEntity<ContactResource>(result, HttpStatus.OK);
    }

    @PostMapping(consumes = {"application/hal+json", MediaType.APPLICATION_JSON_VALUE},
            produces = {"application/hal+json", MediaType.APPLICATION_JSON_VALUE})
    public ContactResource postContact(@RequestBody ContactResource newResource, HttpServletResponse response) {
        if ((newResource.getName()== null) || ("".equals(newResource.getName()))) {
            throw new InvalidContactDataExcption(newResource.getName(), "name");
        }

        try {
            contactService.getContactById(newResource.getId());
            response.setStatus(HttpStatus.CONFLICT.value());
            throw new DuplicateContactException(newResource.getName());
        } catch (ContactNotFoundException cnfe) {
            // this is good.
        }

        // save to MongoDB database
        Contact contact = new Contact(newResource.getId(), newResource.getName(), newResource.getPhone());
        contact = contactService.saveContact(contact);

        response.setStatus(HttpStatus.CREATED.value());

        return resourceAssembler.toModel(contact);
    }

    @DeleteMapping(value = "/{name}", produces = {"application/hal+json", MediaType.APPLICATION_JSON_VALUE})
    public void deleteContact(@PathVariable String name, HttpServletResponse response) {
        contactService.deleteContactByName(name);
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

    @PatchMapping(value = "/{id}", produces = {"application/hal+json", MediaType.APPLICATION_JSON_VALUE})
    public ContactResource patchContact(@PathVariable Long id, @RequestBody ContactResource newResource, HttpServletResponse response) {
        Contact contact = new Contact(newResource.getId(), newResource.getName(), newResource.getPhone());
        contact = contactService.updateContact(id, contact);
        response.setStatus(HttpStatus.OK.value());

        return resourceAssembler.toModel(contact);
    }
}

package com.arc_e_tect.blog.phonebook.web.contacts;

import com.arc_e_tect.blog.phonebook.domain.Contact;
import com.arc_e_tect.blog.phonebook.resource.ContactResource;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ContactsResourceAssembler extends RepresentationModelAssemblerSupport<Contact, ContactResource> {

    public static final String LINK_COLLECTION_CONTACTS = "contacts";

    public ContactsResourceAssembler() {
        super(ContactsController.class, ContactResource.class);
    }

    @Override
    public ContactResource toModel(Contact contact) {
        ContactResource result = super.createModelWithId(contact.getName(), contact);

        BeanUtils.copyProperties(contact, result);

        result.add(linkTo(methodOn(ContactsController.class).getAllContacts(null,null)).withRel(LINK_COLLECTION_CONTACTS));

        return result;
    }

    @Override
    public CollectionModel<ContactResource> toCollectionModel(Iterable<? extends Contact> contacts) {
        CollectionModel<ContactResource> ContactResources = super.toCollectionModel(contacts);

        ContactResources.add(linkTo(methodOn(ContactsController.class).getAllContacts(null,null)).withSelfRel());

        return ContactResources;
    }

}

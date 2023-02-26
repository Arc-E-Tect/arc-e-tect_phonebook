package com.arc_e_tect.blog.phonebook.service;

import com.arc_e_tect.blog.phonebook.domain.Contact;
import com.arc_e_tect.blog.phonebook.service.exception.ContactNotFoundException;

import java.util.List;

public interface ContactService {

    Contact saveContact(Contact contact);

    Contact getContactById(Long id) throws ContactNotFoundException;

    List<Contact> retrieveAllContacts();
    List<Contact> retrieveAllContacts(String contactName);

    Contact updateContact(Long id, Contact patch);

    void deleteContactByName(String name);
}

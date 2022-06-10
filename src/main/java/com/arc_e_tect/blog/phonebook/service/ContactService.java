package com.arc_e_tect.blog.phonebook.service;

import com.arc_e_tect.blog.phonebook.domain.Contact;
import com.arc_e_tect.blog.phonebook.service.exception.ContactNotFoundException;

import java.util.List;

public interface ContactService {

    Contact saveContact(Contact contact);

    Contact getContactByID(Long id) throws ContactNotFoundException;
    Contact getContactByName(String name) throws ContactNotFoundException;
    List<Contact> retrieveAllContacts();
}

package com.arc_e_tect.blog.phonebook.service;

import com.arc_e_tect.blog.phonebook.domain.Contact;
import com.arc_e_tect.blog.phonebook.service.exception.ContactNotFoundException;

import java.util.List;

public interface ContactService {

    Contact getContactByID(Long id) throws ContactNotFoundException;
    List<Contact> retrieveAllContacts();
}

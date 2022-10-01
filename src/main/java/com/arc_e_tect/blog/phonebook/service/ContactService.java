package com.arc_e_tect.blog.phonebook.service;

import com.arc_e_tect.blog.phonebook.domain.Contact;

import java.util.List;

public interface ContactService {

    List<Contact> retrieveAllContacts();
}

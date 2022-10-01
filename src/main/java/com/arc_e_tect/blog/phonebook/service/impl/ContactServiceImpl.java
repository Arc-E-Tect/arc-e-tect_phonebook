package com.arc_e_tect.blog.phonebook.service.impl;

import com.arc_e_tect.blog.phonebook.domain.Contact;
import com.arc_e_tect.blog.phonebook.repository.ContactRepository;
import com.arc_e_tect.blog.phonebook.service.ContactService;
import lombok.Setter;
import lombok.extern.flogger.Flogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Flogger
@Service
public class ContactServiceImpl implements ContactService {

    @Autowired @Setter
    ContactRepository repo;

    @Override
    public List<Contact> retrieveAllContacts(){
       return repo.findAll();
    }

}
package com.arc_e_tect.blog.phonebook.service.impl;

import com.arc_e_tect.blog.phonebook.domain.Contact;
import com.arc_e_tect.blog.phonebook.repository.ContactRepository;
import com.arc_e_tect.blog.phonebook.service.ContactService;
import com.arc_e_tect.blog.phonebook.service.exception.ContactNotFoundException;
import lombok.Setter;
import lombok.extern.flogger.Flogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Flogger
@Service
public class ContactServiceImpl implements ContactService {

    @Autowired @Setter
    ContactRepository repo;

    @Override
    public Contact saveContact(Contact contact) {
        return repo.save(contact);
    }

    @Override
    public Contact getContactByID(Long id) throws ContactNotFoundException {
        Optional<Contact> result = repo.findById(id);
        if (!result.isPresent()) {
            throw new ContactNotFoundException(id);
        }

        return result.get();
    }

    @Override
    public Contact getContactByName(String name) throws ContactNotFoundException {
        Contact result = repo.findByName(name);
        if (result == null) {
            throw new ContactNotFoundException(name);
        }

        return result;
    }

    @Override
    public List<Contact> retrieveAllContacts(){
       return repo.findAll();
    }

    @Override
    public void deleteContactById(Long id) {
        repo.deleteById(id);
    }

}
package com.arc_e_tect.blog.phonebook.service.impl;

import com.arc_e_tect.blog.phonebook.domain.Contact;
import com.arc_e_tect.blog.phonebook.repository.ContactRepository;
import com.arc_e_tect.blog.phonebook.service.ContactService;
import com.arc_e_tect.blog.phonebook.service.exception.ContactNotFoundException;
import lombok.Setter;
import lombok.extern.flogger.Flogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public Contact getContactById(Long id) throws ContactNotFoundException {
        Optional<Contact>  result = repo.findById(id);
        if (result.isEmpty()) {
            throw new ContactNotFoundException(id);
        }

        return result.get();
    }

    @Override
    public List<Contact> retrieveAllContacts(String name) throws ContactNotFoundException {
        Optional<List<Contact>>  result = repo.findByName(name);
        if (result.isEmpty()) {
            return new ArrayList<>();
        }

        return result.get();
    }

    @Override
    public List<Contact> retrieveAllContacts(){
       return repo.findAll();
    }

    @Override
    public void deleteContact(Long id) {
        repo.deleteById(id);
    }

    @Override
    public void deleteContactByName(String name) {
        Optional<List<Contact>> result = repo.findByName(name);

        if (result.isPresent()) {
            List<Contact> deletables = result.get();
            for(Contact deletable : deletables) {
                repo.deleteById(deletable.getId());
            }
        }
    }

    @Override
    public Contact updateContact(Long id, Contact patch) {
        Optional<Contact> result = repo.findById(id);
        if (result.isEmpty()) {
            throw new ContactNotFoundException(id);
        }

        Contact contact = result.get();

        if (patch.getName() != null) {
            contact.setName(patch.getName());
        }
        if (patch.getPhone() != null) {
            contact.setPhone(patch.getPhone());
        }

        contact = repo.save(contact);

        return contact;
    }


}
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
        Optional<Contact>  result = repo.findByName(name);
        if (!result.isPresent()) {
            throw new ContactNotFoundException(name);
        }

        return result.get();
    }

    @Override
    public List<Contact> retrieveAllContacts(){
       return repo.findAll();
    }

    @Override
    public void deleteContactByName(String name) {
        Optional<Contact> result = repo.findByName(name);
        if (result.isPresent()) {
            Contact deletable = result.get();
            repo.deleteById(deletable.getId());
        }
    }

    @Override
    public Contact updateContactByName(String name, Contact patch) {
        Optional<Contact> result = repo.findByName(name);
        if (!result.isPresent()) {
            throw new ContactNotFoundException(name);
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
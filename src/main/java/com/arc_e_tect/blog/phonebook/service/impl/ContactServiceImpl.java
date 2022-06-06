package com.arc_e_tect.blog.phonebook.service.impl;

import com.arc_e_tect.blog.phonebook.domain.Contact;
import com.arc_e_tect.blog.phonebook.repository.ContactRepository;
import com.arc_e_tect.blog.phonebook.service.ContactService;
import com.arc_e_tect.blog.phonebook.service.exception.ContactNotFoundException;
import lombok.Setter;
import lombok.extern.flogger.Flogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Flogger
@Service
public class ContactServiceImpl implements ContactService {

    @Autowired @Setter
    ContactRepository repo;

    @Override
    public Contact getContactByID(Long id) throws ContactNotFoundException {
        try {
            Optional<Contact> result = repo.findById(id);
            if (!result.isPresent()) {
                throw new ContactNotFoundException(id);
            }

            return result.get();
        } catch (DataAccessResourceFailureException darfe) {
            throw darfe;
        }
    }

    @Override
    public List<Contact> retrieveAllContacts(){
       return repo.findAll();
    }

}
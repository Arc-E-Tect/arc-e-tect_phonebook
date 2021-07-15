package com.arc_e_tect.blog.phonebook.web.contacts;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value= {"/contacts"})
public class ContactsController {

    @GetMapping
    public RepresentationModel getAllContacts() {
        return null;
    }

}

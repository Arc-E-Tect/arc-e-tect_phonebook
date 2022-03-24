package com.arc_e_tect.blog.phonebook.web.contacts;

import org.springframework.hateoas.CollectionModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value= {"/contacts"})
public class ContactsController {

    @GetMapping(produces = {"application/hal+json", MediaType.APPLICATION_JSON_VALUE})
    public CollectionModel<String> getAllContacts(HttpServletResponse response) {
        return null;
    }

}

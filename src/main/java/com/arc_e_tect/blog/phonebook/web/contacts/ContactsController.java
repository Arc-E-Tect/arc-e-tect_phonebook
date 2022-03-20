package com.arc_e_tect.blog.phonebook.web.contacts;

import com.arc_e_tect.blog.phonebook.web.index.IndexController;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value= {"/contacts"})
public class ContactsController {
    HashMap<String, String> contacts;

    @GetMapping(produces = {"application/hal+json", MediaType.APPLICATION_JSON_VALUE})
    public RepresentationModel contacts() {
        RepresentationModel contactResources = new RepresentationModel();
        contactResources.add(linkTo(IndexController.class).withSelfRel());
        contactResources.add(linkTo(ContactsController.class).withRel("contacts"));
        return contactResources;
    }

}

package com.arc_e_tect.blog.phonebook.web.index;

import com.arc_e_tect.blog.phonebook.web.contacts.ContactsController;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value= {"/", "/index"})
public class IndexController {

    @GetMapping
    public RepresentationModel index() {
        RepresentationModel index = new RepresentationModel();
        index.add(linkTo(IndexController.class).withSelfRel());
        index.add(linkTo(ContactsController.class).withRel("contacts"));
        return index;
    }

}

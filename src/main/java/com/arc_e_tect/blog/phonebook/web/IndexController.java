package com.arc_e_tect.blog.phonebook.web;

import com.arc_e_tect.blog.phonebook.web.version.VersionController;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/")
public class IndexController extends CPMWebController {

    @GetMapping
    public RepresentationModel index() {
        RepresentationModel index = new RepresentationModel();
        index.add(linkTo(IndexController.class).withSelfRel());
        index.add(linkTo(VersionController.class).withRel("version"));
        return index;
    }

}

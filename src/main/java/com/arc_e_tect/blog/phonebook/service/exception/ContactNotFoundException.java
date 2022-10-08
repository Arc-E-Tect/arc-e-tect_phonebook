package com.arc_e_tect.blog.phonebook.service.exception;

import lombok.Getter;

public class ContactNotFoundException extends RuntimeException {
    @Getter
    private final long id;
    private final String name;

    public ContactNotFoundException(long id) {
        super(String.format("Contact %d was not found", id));
        this.id = id;
        this.name = "";
    }

    public ContactNotFoundException(String name) {
        super(String.format("Contact %s was not found", name));
        this.id = -1l;
        this.name = name;
    }
}

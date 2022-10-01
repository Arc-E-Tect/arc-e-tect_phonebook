package com.arc_e_tect.blog.phonebook.service.exception;

import lombok.Getter;

public class ContactNotFoundException extends RuntimeException {
    @Getter
    private final long id;

    public ContactNotFoundException(long id) {
        super(String.format("Contact %d was not found", id));
        this.id = id;
    }
}

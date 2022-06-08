package com.arc_e_tect.blog.phonebook.service.exception;

import lombok.Getter;

@Getter
public class DuplicateContactException extends RuntimeException {
    private final long id;

    public DuplicateContactException(long id) {
        super(String.format("Contact %d already exists", id));
        this.id = id;
    }
}

package com.arc_e_tect.blog.phonebook.service.exception;

import lombok.Getter;

@Getter
public class DuplicateContactException extends RuntimeException {
    private final long id;
    private final String name;

    public DuplicateContactException(long id) {
        super(String.format("Contact %d already exists", id));
        this.id = id;
        this.name = "";
    }

    public DuplicateContactException(String name) {
        super(String.format("Contact %s already exists", name));
        this.id = -1l;
        this.name = name;
    }
}

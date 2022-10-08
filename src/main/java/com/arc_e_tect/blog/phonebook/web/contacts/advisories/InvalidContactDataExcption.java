package com.arc_e_tect.blog.phonebook.web.contacts.advisories;

import lombok.Getter;

@Getter
public class InvalidContactDataExcption extends RuntimeException {
    private final String fieldName;
    private final String fieldValue;

    public InvalidContactDataExcption(String fieldValue, String fieldName) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}

package com.arc_e_tect.blog.phonebook;

import com.arc_e_tect.blog.phonebook.model.Contact;

import java.util.HashMap;

public class PhonebookApplication {
    public static void main(String[] args) {
        // This application is doing nothing and is only used to show TDD as part of the Arc-E-Tect
        // 'Software Engineering done Right' series.
    }

    private HashMap<String, Contact> phonebook = new HashMap<>();
    protected HashMap<String, Contact> getPhonebook() {
        return phonebook;
    }

    public void addContactToPhonebook(String searchString, Contact contact) {
        phonebook.put(searchString, contact);
    }

    public String getPhoneNumberContactFromPhonebook(String searchString) {
        return phonebook.get(searchString).getPhoneNumber();
    }
}

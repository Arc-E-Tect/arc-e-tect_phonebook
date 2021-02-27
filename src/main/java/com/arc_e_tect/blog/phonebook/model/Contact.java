package com.arc_e_tect.blog.phonebook.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class Contact {
    @NonNull private final String phoneNumber;
}

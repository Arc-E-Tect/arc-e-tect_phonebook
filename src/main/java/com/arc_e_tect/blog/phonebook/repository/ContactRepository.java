package com.arc_e_tect.blog.phonebook.repository;

import com.arc_e_tect.blog.phonebook.domain.Contact;
import lombok.NonNull;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContactRepository extends MongoRepository<Contact, Long> {
    Contact findByName(@NonNull String name);
}

package com.arc_e_tect.blog.phonebook.repository;

import com.arc_e_tect.blog.phonebook.domain.Contact;
import lombok.NonNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends MongoRepository<Contact, Long> {
    Optional<List<Contact>> findByName(@NonNull String name);
}

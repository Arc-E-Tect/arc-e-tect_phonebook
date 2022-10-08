package com.arc_e_tect.blog.phonebook.repository;

import com.arc_e_tect.blog.phonebook.domain.DatabaseSequence;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SequenceRepository extends MongoRepository<DatabaseSequence, String> {
}

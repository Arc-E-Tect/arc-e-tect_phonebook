package com.arc_e_tect.blog.phonebook.domain;

import lombok.*;
import org.bson.types.ObjectId;

@Getter @Setter @ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper=false)
@NoArgsConstructor @AllArgsConstructor
public class TestContact {
    private ObjectId id;
    private String name;
    private String phone;
}

package com.arc_e_tect.blog.phonebook.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.*;
import org.springframework.hateoas.server.core.Relation;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName(value = "contact")
@Relation(collectionRelation = "contacts")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter @Setter @ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper=false)
@NoArgsConstructor @AllArgsConstructor
public class Contact {
    private long id;
    private String name;
    private String phone;
}

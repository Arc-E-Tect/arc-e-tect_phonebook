package com.arc_e_tect.blog.phonebook.domain;

import lombok.*;

@Getter @Setter @ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper=false)
@NoArgsConstructor @AllArgsConstructor
public class TestContact {
    private long id;
    private String name;
    private String phone;
}

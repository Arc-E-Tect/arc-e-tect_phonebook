package com.arc_e_tect.blog.phonebook.resource;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter @Setter @ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper=false)
@NoArgsConstructor @RequiredArgsConstructor
public class VersionResource extends RepresentationModel {
    @EqualsAndHashCode.Include @JsonProperty("applicationName") @JsonAlias("moduleName")
    @NonNull private String applicationName;
    @EqualsAndHashCode.Include
    @NonNull private String versionName;
    @EqualsAndHashCode.Include
    @NonNull private Long versionCode;
}

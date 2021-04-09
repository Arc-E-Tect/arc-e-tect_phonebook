package com.arc_e_tect.blog.phonebook.resource;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.criticalpathomizer.cpm.application.web.version.exception.InvalidVersionResourceException;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.HashSet;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
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
    @JsonProperty("versions")
    private Set<VersionResource> versions = new HashSet<>();

    public void addVersionInformation(JsonNode newVersionJson) {
        try {
            VersionResource versionResource = new ObjectMapper().readValue(newVersionJson.toString(),VersionResource.class);
            addVersionInformation(versionResource);
        } catch (JsonProcessingException e) {
            throw new InvalidVersionResourceException(newVersionJson.toString());
        }
    }

    public void addVersionInformation(VersionResource newVersion) {
        if (versions == null) {
            versions = new HashSet<>();
        }
        versions.add(newVersion);
    }
}

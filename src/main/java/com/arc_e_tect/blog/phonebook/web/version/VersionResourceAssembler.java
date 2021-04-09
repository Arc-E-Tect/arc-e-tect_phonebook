package com.arc_e_tect.blog.phonebook.web.version;

import io.criticalpathomizer.cpm.application.resource.VersionResource;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
class VersionResourceAssembler implements RepresentationModelAssembler<VersionResource, EntityModel<VersionResource>> {
    @Override
    public EntityModel<VersionResource> toModel(VersionResource version) {
        return new EntityModel<>(version);
    }
}

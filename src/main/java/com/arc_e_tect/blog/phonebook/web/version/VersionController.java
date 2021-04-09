package com.arc_e_tect.blog.phonebook.web.version;

import io.criticalpathomizer.cpm.application.resource.VersionResource;
import io.criticalpathomizer.cpm.application.web.CPMWebController;
import io.criticalpathomizer.cpm.application.web.exception.ResourceNotFoundException;
import io.criticalpathomizer.cpm.application.web.exception.ServiceNotAvailableException;
import lombok.extern.flogger.Flogger;
import org.joda.time.DateTime;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Flogger
@RestController
@RequestMapping("/version")
public class VersionController extends CPMWebController {

    private final io.criticalpathomizer.cpm.application.web.version.VersionResourceAssembler assembler;

    VersionController(io.criticalpathomizer.cpm.application.web.version.VersionResourceAssembler assembler) {
        this.assembler = assembler;
    }

    @GetMapping(produces = "application/json; charset=UTF-8")
    public EntityModel<VersionResource> version() {
        log.atInfo().log(" --> Start serving version request.");
        DateTime now = DateTime.now();

        VersionResource versionInformation = new VersionResource(getAppName(), getAppVersion(), getAppBuildnumber());
        try {
            versionInformation.addVersionInformation(namespaceClient.getVersionInfo());
        } catch (ResourceNotFoundException rnfe) {
            log.atSevere().log("X--> Error while contacting Namespace Service. Details: %s ", rnfe.getMessage());
        } catch (ServiceNotAvailableException snae) {
            log.atSevere().log("X--> Error while contacting Namespace Service. Details: %s ", snae.getMessage());
        }

        try {
            versionInformation.addVersionInformation(modelClient.getVersionInfo());
        } catch (ResourceNotFoundException rnfe) {
            log.atSevere().log("X--> Error while contacting Model Service. Details: %s ", rnfe.getMessage());
        } catch (ServiceNotAvailableException snae) {
            log.atSevere().log("X--> Error while contacting Model Service. Details: %s ", snae.getMessage());
        }

        EntityModel<VersionResource> result = assembler.toModel(versionInformation);
        log.atInfo().log(" >-- Completed serving version request, which took %d msec", DateTime.now().getMillisOfDay() - now.getMillisOfDay());
        return result;
    }
}

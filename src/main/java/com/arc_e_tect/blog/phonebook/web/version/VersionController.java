package com.arc_e_tect.blog.phonebook.web.version;

import com.arc_e_tect.blog.phonebook.resource.VersionResource;
import com.arc_e_tect.blog.phonebook.web.CPMWebController;
import lombok.NoArgsConstructor;
import lombok.extern.flogger.Flogger;
import org.joda.time.DateTime;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Flogger
@RestController
@RequestMapping("/version") @NoArgsConstructor
public class VersionController extends CPMWebController {

    @GetMapping(produces = "application/json; charset=UTF-8")
    public VersionResource version() {
        log.atInfo().log(" --> Start serving version request.");
        DateTime now = DateTime.now();

        VersionResource versionInformation = new VersionResource(getAppName(), getAppVersion(), getAppBuildnumber());

        log.atInfo().log(" >-- Completed serving version request, which took %d msec", DateTime.now().getMillisOfDay() - now.getMillisOfDay());
        return versionInformation;
    }
}

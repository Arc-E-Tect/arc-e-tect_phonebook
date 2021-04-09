package com.arc_e_tect.blog.phonebook.web;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class CPMWebController {
    @Value("${app.name}")
    private String appName;
    @Value("${app.version}")
    private String appVersion;
    @Value("${app.buildNumber}")
    private Long appBuildnumber;

    @Value("${local.server.port:9000}")
    protected int port;
    @Value("${cpnamespace.port}")
    protected int cpnamespacePort;
    @Value("${cpmodel.port}")
    protected int cpmodelPort;
    @Value("${base.url:http://localhost:}")
    protected String baseUrl;
}

package com.arc_e_tect.blog.phonebook;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("com/arc_e_tect/blog/phonebook")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty," +
        "json:build/reports/prototyping/Cucumber.json," +
        "junit:build/reports/prototyping/Cucumber.xml," +
        "html:build/reports/prototyping/Cucumber.html")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "not @ignore and not @wip")
public class RunApiCucumberTest {
}

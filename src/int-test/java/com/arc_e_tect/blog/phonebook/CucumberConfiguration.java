package com.arc_e_tect.blog.phonebook;

import com.arc_e_tect.blog.phonebook.common.StepData;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CucumberConfiguration {
    @Bean
    @Scope("cucumber-glue")
    public StepData stepData() {
        return new StepData();
    }
}

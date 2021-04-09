package com.arc_e_tect.blog.phonebook;

import lombok.extern.flogger.Flogger;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

@Flogger
@SpringBootTest
@WebAppConfiguration
@ContextConfiguration
public class SpringBootApplicationTest {

    @Test
    public void contextLoads() {
        log.atInfo().log(" === Spring Boot context loaded for Intergration Tests.");
    }
}

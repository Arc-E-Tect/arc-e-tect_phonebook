package com.arc_e_tect.blog.phonebook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.arc_e_tect.blog"})
public class ArcETectPhonebookApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArcETectPhonebookApplication.class, args);
	}
}

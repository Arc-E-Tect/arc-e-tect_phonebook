package com.arc_e_tect.blog.phonebook.web.contacts;

import com.arc_e_tect.blog.phonebook.web.AbstractSteps;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import lombok.extern.flogger.Flogger;

@Flogger
public class ContactSteps extends AbstractSteps {

    @Given("the phonebook has {int} contacts")
    public void the_phonebook_has_contacts(Integer numberOfContacts) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("the response contains no contacts")
    public void the_response_contains_no_contacts() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

}

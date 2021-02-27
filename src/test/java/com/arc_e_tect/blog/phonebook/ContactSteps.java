package com.arc_e_tect.blog.phonebook;

import com.arc_e_tect.blog.phonebook.model.Contact;
import com.arc_e_tect.blog.phonebook.PhonebookApplication;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ContactSteps {
    PhonebookApplication phonebookApplication = new PhonebookApplication();
    String retrievedPhoneNumber;

    @Given("The contact {string} is in our Phonebook with phone number {string}")
    public void the_contact_is_in_our_Phonebook_with_phone_number(String contact, String phoneNumber) {
        phonebookApplication.addContactToPhonebook(contact, new Contact(phoneNumber));
    }

    @When("we ask for phone number of the contact {string}")
    public void we_ask_for_phone_number_of_the_contact(String contact) {
        retrievedPhoneNumber = phonebookApplication.getPhoneNumberContactFromPhonebook(contact);
    }

    @Then("we get the phone number {string}")
    public void we_get_the_phone_number(String phoneNumber) {
        assert(phoneNumber.equals(retrievedPhoneNumber));
    }
}

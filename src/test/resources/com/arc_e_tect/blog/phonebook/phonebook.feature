Feature: application - Get Contacts

  Scenario: application - 01 - Getting Arc-E-Tect
    Given The contact "Arc-E-Tect" is in our Phonebook with phone number "+1 555 3247843"
    When we ask for phone number of the contact "Arc-E-Tect"
    Then we get the phone number "+1 555 3247843"


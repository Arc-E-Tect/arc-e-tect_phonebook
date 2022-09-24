Feature: Get all contacts available in the phonebook

  Scenario: 01 - From an empty phonebook
    Given the phonebook is empty
    When all contacts are requested
    Then the response contains no contacts

  Scenario: 02 - From a phonebook with a single contact in it
    Given the contact with name "Peter Parker" is listed in the phonebook
    When all contacts are requested
    Then the response contains the contact "Peter Parker"

  Scenario: 03 - From a phonebook with several contacts in it
    Given the contact with name "Peter Parker" is listed in the phonebook
    And the contact with name "Charly Brown" is listed in the phonebook
    When all contacts are requested
    Then the response contains the contact "Peter Parker"
    And the response contains the contact "Charly Brown"

Feature: Get all contacts available in the phonebook

  Scenario: 01 - Requests all contacts from an empty phonebook
    Given the phonebook is empty
    When all contacts are requested
    Then the response contains no contacts

  Scenario: 02 - Request all contacts from a phonebook with a contact in it
    Given the contact with name "Peter Parker" is listed in the phonebook
    When all contacts are requested
    Then the response contains the contact "Peter Parker"

  Scenario: 03 - Request all contacts from a phonebook with several contacts in it
    Given the contact with name "Peter Parker" is listed in the phonebook
    And the contact with name "Charly Brown" is listed in the phonebook
    When all contacts are requested
    Then the response contains the contact "Peter Parker"
    And the response contains the contact "Charly Brown"

Feature: Delete a Contact from the phonebook

  This feature-file specifies the scenarios related to deleting a single contact from the Arc-E-Tect Phonebook.

  Scenario: 01 - A listed contact
    Given the contact with name "Peter Parker" is listed in the phonebook
    When the contact with name "Peter Parker" is deleted
    Then the phonebook does not contain the contact with name "Peter Parker"

  Scenario: 02 - An unlisted contact
    Given the contact with name "John Doe" is not listed in the phonebook
    When the contact with name "John Doe" is deleted
    Then the phonebook does not contain the contact with name "John Doe"


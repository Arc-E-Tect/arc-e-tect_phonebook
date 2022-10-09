Feature: Get a single contact from the phonebook

  This feature-file specifies the scenarios related to retrieving a single contact from the Arc-E-Tect Phonebook.

  Scenario: 01 - A contact that is listed
    Given the contact with name "Peter Parker" is listed in the phonebook
    When the contact with name "Peter Parker" is requested
    Then the response contains the contact with name "Peter Parker"

  @error
  Scenario: 02 - A contact from an empty phonebook
    Given the phonebook is empty
    When the contact with name "Peter Parker" is requested
    Then the contact cannot be found
    And the response contains no contact

  @error
  Scenario: 03 - A contact that is not listed
    Given the contact with name "John Doe" is not listed in the phonebook
    When the contact with name "John Doe" is requested
    Then the contact cannot be found
    And the response contains no contact

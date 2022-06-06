Feature: Get all contacts available in the phonebook

  Scenario: 01 - Requests a contact that is listed in the phonebook
    Given the contact "John Doe" is listed in the phonebook
    When a the contact with name "John Doe" is requested
    Then the response contains the contact with name "John Doe"

  @error
  Scenario: 02 - Requests a contact from an empty phonebook
    Given the phonebook is empty
    When a the contact with name "John Doe" is requested
    Then the contact cannot be found
    And the response contains no contacts

  @error
  Scenario: 03 - Requests a contact that is not listed in the phonebook
    Given the contact "John Doe" is not listed in the phonebook
    When a the contact with name "John Doe" is requested
    Then the contact cannot be found
    And the response contains no contacts

Feature: Get a single contact from the phonebook

  This feature-file specifies the scenarios related to retrieving a single contact from the Arc-E-Tect Phonebook.
  The following conventions are used. The contact with the id 42, which is the universal answer, is used for the happy-flow
  in the scenarios. The contact with the id 666, the number of the beast, is used for those error flows where the id is
  relevant for the scenario.

  Scenario: 01 - Requests a contact that is listed in the phonebook
    Given the contact with id 42 is listed in the phonebook
    When the contact with id 42 is requested
    Then the response contains the contact with id 42

  @error
  Scenario: 02 - Requests a contact from an empty phonebook
    Given the phonebook is empty
    When the contact with id 1 is requested
    Then the contact cannot be found
    And the response contains no contact

  @error
  Scenario: 03 - Requests a contact that is not listed in the phonebook
    Given the contact with id 666 is not listed in the phonebook
    When a the contact with id 666 is requested
    Then the contact cannot be found
    And the response contains no contact

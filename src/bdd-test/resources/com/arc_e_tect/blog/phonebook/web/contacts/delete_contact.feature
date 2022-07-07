Feature: Delete a Contact from the phonebook

  This feature-file specifies the scenarios related to deleting a single contact from the Arc-E-Tect Phonebook.
  The following conventions are used. The contact with the id 42, which is the universal answer, is used for the happy-flow
  in the scenarios. The contact with the id 666, the number of the beast, is used for those error flows where the id is
  relevant for the scenario. Contacts with the id 24 are unknown, as you can file a missing person report after 24 hours.

  Scenario: 01 - Deleting an existing contact from the phonebook
    Given the contact with id 42 is listed in the phonebook
    When the contact with id 42 is deleted
    Then the phonebook does not contain the contact with id 42

  Scenario: 02 - Deleting a non-existing contact from the phonebook
    Given the contact with id 24 is not listed in the phonebook
    When the contact with id 24 is deleted
    Then the phonebook does not contain the contact with id 24


Feature: Update a contact in the phonebook

  This feature-file specifies the scenarios related to storing a contact into the Arc-E-Tect Phonebook.
  The following conventions are used. The contact with the id 42, which is the universal answer, is used for the happy-flow
  in the scenarios. The contact with the id 666, the number of the beast, is used for those error flows where the id is
  relevant for the scenario.

  Scenario: Update the phone number of the contact
    Given the listed contact
    | id | name     | phone         |
    | 42 | John Doe | 1-555-2124578 |
    When the phone number of contact 42 is changed to "1-666-2124578"
    Then the contact with id 42 has phone number "1-666-2124578"
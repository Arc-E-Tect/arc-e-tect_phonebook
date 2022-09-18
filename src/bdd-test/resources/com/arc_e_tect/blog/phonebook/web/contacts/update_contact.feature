Feature: Update a contact in the phonebook

  This feature-file specifies the scenarios related to storing a contact into the Arc-E-Tect Phonebook.
  The following conventions are used. The contact with the id 42, which is the universal answer, is used for the happy-flow
  in the scenarios. The contact with the id 666, the number of the beast, is used for those error flows where the id is
  relevant for the scenario.

  Scenario: Update the phone number of the contact
    Given the listed contact
      | id | name    | phone         |
      | 42 | Peter Parker | 1-555-2124578 |
    When the phone number of contact "Peter Parker" is changed to "1-666-2124578"
    Then the contact with id 42 has phone number "1-666-2124578"

  Scenario: Update the name of the contact
    Given the listed contact
      | id | name         | phone         |
      | 42 | Peter Parker | 1-555-2124578 |
    When the name of contact "Peter Parker" is changed to "John Stark"
    Then the contact with id 42 has name "John Stark"

  Scenario: Update the phone number of an unknown contact
    Given the phonebook is empty
    When the phone number of contact "John Doe" is changed to "1-666-2124578"
    Then the contact cannot be found
    And the response contains no contact

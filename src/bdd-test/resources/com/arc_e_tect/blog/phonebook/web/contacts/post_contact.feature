Feature: Store a contact into the phonebook

  This feature-file specifies the scenarios related to storing a contact into the Arc-E-Tect Phonebook.
  The following conventions are used. The contact with the id 42, which is the universal answer, is used for the happy-flow
  in the scenarios. The contact with the id 666, the number of the beast, is used for those error flows where the id is
  relevant for the scenario.

  Scenario: 01 - Add a new contact to the phonebook
    Given the contact with id 42 is not listed in the phonebook
    When the contact with id 42 and name "John Doe" is added to the phonebook
    Then the phonebook contains the contact with id 42
    And the contact with id 42 has name "John Doe"
    And the response contains the contact with id 42

  @error
  Scenario: 02 - Try to add a contact to the phonebook that is already listed
    Given the contact with id 666 is listed in the phonebook
    And the contact with id 666 has name "Mephisto"
    When the contact with id 666 and name "John Doe" is added to the phonebook
    Then the phonebook contains the contact with id 666
    And the contact with id 666 has name "Mephisto"
    And the response contains no contact
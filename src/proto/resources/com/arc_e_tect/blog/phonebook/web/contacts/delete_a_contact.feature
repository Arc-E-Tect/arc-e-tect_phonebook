Feature: Delete a Contact from the phonebook

  This feature-file specifies the scenarios related to deleting a single contact from the Arc-E-Tect Phonebook.

  @prototyping
  Scenario: 01 - A listed contact
    Given the listed contact
      | id | name         |  | phone           |
      | 1  | Peter Parker |  | +1 (555) 748432 |
    When the contact with name "Peter Parker" is deleted
    Then the phonebook does not contain a contact with name "Peter Parker"

  @prototyping
  Scenario: 03 - Multiple listed contacts with the same name
    Given the listed contacts
      | id | name         |  | phone           |
      | 1  | Peter Parker |  | +1 (555) 748432 |
      | 2  | Peter Parker |  | +1 (555) 234947 |
    When the contact with name "Peter Parker" is deleted
    Then the phonebook does not contain a contact with name "Peter Parker"

  @prototyping
  Scenario: 04 - Multiple listed contacts with the same name
    Given the listed contacts
      | id | name         |  | phone           |
      | 1  | Peter Parker |  | +1 (555) 748432 |
      | 2  | Peter Parker |  | +1 (555) 234947 |
    When the contact with id 2 is deleted
    Then the phonebook does not contain a contact with id 2


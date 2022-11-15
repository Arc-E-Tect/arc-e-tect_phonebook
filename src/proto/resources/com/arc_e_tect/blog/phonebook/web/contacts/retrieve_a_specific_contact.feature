Feature: Get a single contact from the phonebook

  This feature-file specifies the scenarios related to retrieving a single contact from the Arc-E-Tect Phonebook.

  Scenario: 01 - A contact that is listed
    Given the listed contact
      | id | name         | phone           |
      | 1  | Peter Parker | +1 (555) 748432 |
    When the contact with name "Peter Parker" is requested
    Then the response contains a contact "Peter Parker" with phone "+1 (555) 748432"

  Scenario: 02 - Multiple Contacts with the same name
    Given the listed contacts
      | id | name         | phone           |
      | 1  | Peter Parker | +1 (555) 748432 |
      | 2  | Peter Parker | +1 (555) 234847 |
      | 3  | Charly Brown |                 |
    When the contact with name "Peter Parker" is requested
    Then the response contains a contact "Peter Parker" with phone "+1 (555) 748432"
    And the response contains a contact "Peter Parker" with phone "+1 (555) 234847"

  Scenario: 03 - A contact that is listed
    Given the listed contact
      | id | name         | phone           |
      | 1  | Peter Parker | +1 (555) 748432 |
    When the contact with id 1 is requested
    Then the response contains a single contact with id 1

  @error
  Scenario: 04 - A contact from an empty phonebook
    Given the phonebook is empty
    When the contact with id 1 is requested
    Then the response is an error indicating that the contact could not be found

  @error
  Scenario: 05 - A contact that is not listed
    Given the contact with id 666 is not listed in the phonebook
    When the contact with id 666 is requested
    Then the response is an error indicating that the contact could not be found


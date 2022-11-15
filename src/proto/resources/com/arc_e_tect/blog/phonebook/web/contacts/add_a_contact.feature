@ignore
Feature: Add a contact to the phonebook

  Scenario: 01 - A new contact
    Given the contact with name "Peter Parker" is not listed in the phonebook
    When adding to the phonebook the contact
      | id | name         | phone           |
      | 42 | Peter Parker | +1 (555) 432748 |
    Then the phonebook contains a contact with name "Peter Parker" and phone "+1 (555) 432748"
    And the response contains a single contact with name "Peter Parker"
    And the response contains a single contact with phone "+1 (555) 432748"

  Scenario: 02 - A new contact without a phone
    Given the contact with name "Peter Parker" is not listed in the phonebook
    When adding to the phonebook the contact
      | id | name         |  | phone |
      | 42 | Peter Parker |  |       |
    Then the phonebook contains a contact with name "Peter Parker" and no phone
    And the response contains a single contact with name "Peter Parker"

  Scenario: 03 - A contact with the same name as a listed Contact
    Given the listed contact
      | id | name         | phone           |
      | 42 | Peter Parker | +1 (555) 748432 |
    When adding to the phonebook the contact
      | id | name         | phone           |
      | 1  | Peter Parker | +1 (555) 432748 |
    Then the phonebook contains a contact with name "Peter Parker" and phone "+1 (555) 748432"
    And the phonebook contains a contact with name "Peter Parker" and phone "+1 (555) 432748"
    And the response contains a single contact with name "Peter Parker"
    And the response contains a single contact with phone "+1 (555) 432748"

  @error
  Scenario: 04 - A contact with the same id as a listed Contact
    Given the listed contact
      | id | name         | phone           |
      | 42 | Peter Parker | +1 (555) 748432 |
    When adding to the phonebook the contact
      | id | name         | phone           |
      | 42 | Peter Parker | +1 (555) 432748 |
    Then the phonebook contains a contact with name "Peter Parker" and phone "+1 (555) 748432"
    And the response is an error indicating that the contact already exists

Feature: Add a contact to the phonebook

  Scenario: 01 - A new contact
    Given the phonebook is empty
    When adding to the phonebook the contact
      | id | name         | phone           |
      | 42 | Peter Parker | +1 (555) 432748 |
    Then the response contains the new contact "Peter Parker" with phone "+1 (555) 432748"

  Scenario: 02 - A new contact without a phone
    Given the phonebook is empty
    When adding to the phonebook the contact
      | id | name         | phone   |
      | 42 | Peter Parker | [blank] |
    Then the response contains the new contact "Peter Parker" with phone ""

  Scenario: 03 - A contact with the same name as a listed Contact
    Given the listed contact
      | id | name         | phone           |
      | 42 | Peter Parker | +1 (555) 748432 |
    When adding to the phonebook the contact
      | id | name         | phone           |
      | 1  | Peter Parker | +1 (555) 432748 |
    Then the response contains the new contact "Peter Parker" with phone "+1 (555) 432748"

  @error
  Scenario: 04 - A contact with the same id as a listed Contact
    Given the listed contact
      | id | name         | phone           |
      | 42 | Peter Parker | +1 (555) 748432 |
    When adding to the phonebook the contact
      | id | name         | phone           |
      | 42 | Peter Parker | +1 (555) 432748 |
    Then the response is an error indicating that the contact already exists

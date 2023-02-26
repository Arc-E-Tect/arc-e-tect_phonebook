Feature: Get all contacts available in the phonebook

  Scenario: 01 - From a phonebook with several contacts with the same name in it
    Given the listed contacts
      | id | name         | phone           |
      | 1  | Peter Parker | +1 (555) 748432 |
      | 2  | Peter Parker | +1 (555) 234947 |
    When all contacts are requested
    Then 2 contacts are retrieved
    And the response contains a contact "Peter Parker" with phone "+1 (555) 748432"
    And the response contains a contact "Peter Parker" with phone "+1 (555) 234947"


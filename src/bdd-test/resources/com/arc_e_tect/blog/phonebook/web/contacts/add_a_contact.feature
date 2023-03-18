Feature: Add a contact to the phonebook

  Scenario: 01 - A new contact
    Given the contact with name "Peter Parker" is not listed in the phonebook
    When the contact is added to the phonebook
      | name         | phone           |
      | Peter Parker | +1 (555) 432748 |
    Then the phonebook contains the contact with name "Peter Parker"
    And the contact with name "Peter Parker" has phone number "+1 (555) 432748"
    And the response contains the contact with name "Peter Parker"
    And the response contains the contact with phone "+1 (555) 432748"

  Scenario: 02 - A contact with no name
    When the contact with no name is added to the phonebook
    Then the phonebook does not contain a contact with no name
    And the response is an error indicating that invalid contact data was provided

  Scenario: 03 - A contact with an empty string as name
    When the contact with name "" is added to the phonebook
    Then the phonebook does not contain a contact with no name
    And the response is an error indicating that invalid contact data was provided

  Scenario: 04 - An already listed contact
    Given the contact with name "John Smith" is listed in the phonebook
    When the contact with name "John Smith" is added to the phonebook
    Then the response is an error indicating that a contact with the same name already exists

  @ignore
  Scenario: 04 - An already listed contact
    Given the listed contact
      | name    | phone         |
      | Peter Parker | +1 (555) 748432 |
    When the contact is added to the phonebook
      | name         | phone        |
      | Peter Parker | +1 (555) 432748 |
    Then the phonebook contains the contact with name "Peter Parker"
    And the contact with name "Peter Parker" has phone number "+1 (555) 432748"
    And the response contains the contact with name "Peter Parker"

  Scenario: 05 - A new contact
    Given the contact with name "Peter Parker" is not listed in the phonebook
    When the contact is added to the phonebook
      | name         | phone |
      | Peter Parker |       |
    Then the phonebook contains the contact with name "Peter Parker"
    And the contact with name "Peter Parker" has no phone number
    And the response contains the contact with name "Peter Parker"


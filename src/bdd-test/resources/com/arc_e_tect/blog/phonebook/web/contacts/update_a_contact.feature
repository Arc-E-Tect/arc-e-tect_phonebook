Feature: Update a contact in the phonebook

  This feature-file specifies the scenarios related to storing a contact into the Arc-E-Tect Phonebook.

  Scenario: 01 - Update the contact's phone number
    Given the listed contact
      | name         | phone           |
      | John Smith | +1 (555) 748432 |
    When the phone number of contact "John Smith" is changed to "+1 (555) 432748"
    Then the contact with name "John Smith" has phone number "+1 (555) 432748"

  @ignore
  Scenario: 02 - Update the contact's name
    Given the listed contact
      | name         | phone           |
      | John Smith | +1 (555) 748432 |
    When the name of contact "John Smith" is changed to "John Stark"
    Then the contact with phone "+1 (555) 748432" has name "John Stark"

  Scenario: 02 - Update the contact's name
    Given the listed contact
      | name         | phone           |
      | John Smith | +1 (555) 748432 |
    When the name of contact "John Smith" is changed to "John Stark"
    Then the contact formerly known as "John Smith" now has name "John Stark"

  Scenario: 03 - Update a Contact in an empty phonebook
    Given the phonebook is empty
    When the phone number of contact "Peter Parker" is changed to "+1 (555) 432748"
    Then the contact cannot be found
    And the response contains no contact

  Scenario: 04 - Update an unlisted Contact
    Given the contact with name "John Doe" is not listed in the phonebook
    When the phone number of contact "John Doe" is changed to "+1 (555) 432748"
    Then the contact cannot be found
    And the response contains no contact

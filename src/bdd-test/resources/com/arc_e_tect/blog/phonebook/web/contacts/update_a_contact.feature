@ignore
Feature: Update a contact in the phonebook

  This feature-file specifies the scenarios related to storing a contact into the Arc-E-Tect Phonebook.

  Scenario: 01 - Update the contact's phone number
    Given the listed contact
      | id | name         | phone           |
      | 1  | Peter Parker | +1 (555) 748432 |
    When the phone number of contact 1 is changed to "+1 (555) 432748"
    Then the response contains the contact "Peter Parker" with phone "+1 (555) 432748"

  Scenario: 02 - Update the contact's name
    Given the listed contact
      | id | name         | phone           |
      | 1  | Peter Parker | +1 (555) 748432 |
    When the name of contact 1 is changed to "John Stark"
    Then the response contains the contact "John Stark" with phone "+1 (555) 748432"

  Scenario: 03 - Update the contact with a different id
    Given the listed contact
      | id | name         | phone           |
      | 1  | Peter Parker | +1 (555) 748432 |
    When contact 1 is changed with data of contact 2
    Then the response is an error indicating that the contact could not be patched

  @error
  Scenario: 04 - Update a Contact in an empty phonebook
    Given the phonebook is empty
    When the phone number of contact 1 is changed to "+1 (555) 432748"
    Then the response is an error indicating that the contact could not be found

  @error
  Scenario: 05 - Update an unlisted Contact
    Given the contact with id 1 is not listed in the phonebook
    When the phone number of contact 1 is changed to "+1 (555) 432748"
    Then the response is an error indicating that the contact could not be found

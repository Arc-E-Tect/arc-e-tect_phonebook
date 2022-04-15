Feature: Get all contacts available in the phonebook

  Scenario: 01 - Requests all contacts from an empty phonebook
    Given the phonebook is empty
    When all contacts are requested
    Then the response contains no contacts

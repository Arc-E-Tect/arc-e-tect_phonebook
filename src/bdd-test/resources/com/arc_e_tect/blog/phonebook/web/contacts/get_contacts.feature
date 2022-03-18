Feature: Get all contacts available in the phonebook

  Scenario: 01 - An API Consumer requests all contacts from an empty phonebook
    Given the phonebook has 0 contacts
    When the API consumer requests "/contacts"
    Then the response contains no contacts

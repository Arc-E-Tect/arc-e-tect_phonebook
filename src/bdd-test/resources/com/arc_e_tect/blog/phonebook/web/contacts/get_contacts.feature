Feature: Get all contacts available in the phonebook

  Scenario: 01 - An API Consumer requests all contacts from the phonebook
    Note that we are still implementing the phonebook application, as more scenarios cover its behavior we will need
    to revisit this scenario, because it will likely fail without implementing a Given clause.
    When the API consumer requests all contacts
    Then the response contains no contacts

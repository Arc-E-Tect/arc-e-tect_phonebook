Feature: Get all top-level endpoints of the Arc-E-Tect Phonebook application

  Scenario: 01 - An API Consumer requests the top-level endpoints
    When the API consumer requests "/"
    Then the response contains a link to "self"

  Scenario: 02 - An API Consumer requests the top-level endpoints through the \index endpoint
    When the API consumer requests "/index"
    Then the response contains a link to "self"

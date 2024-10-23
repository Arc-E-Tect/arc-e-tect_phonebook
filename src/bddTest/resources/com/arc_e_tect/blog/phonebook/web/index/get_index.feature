Feature: Get all top-level endpoints of the Arc-E-Tect Phonebook application

  Scenario: 01 - An API Consumer requests the top-level endpoints
    The application should behave the same whether the endpoint "/" is requested or the endpoint "/index"
    When the API consumer requests the root
    Then the response is the same as if requesting "/index"

  Scenario: 02 - An API Consumer requests the top-level endpoints through the /index endpoint
    When the API consumer requests the index
    Then the response contains a link to "self"
    And the relative path to "self" is ""
    And the response contains a link to "contacts"
    And the relative path to "contacts" is "/contacts"


Feature: Get the application index through the API

  Scenario: Client makes call to GET index page
    When the client calls index page
    Then the response contains a link to "version"
    And the response contains a link to "self"

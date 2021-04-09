Feature: Application - Index - Get the application index through the API

  Scenario: Application - Index - 01 - Client makes call to GET index page
    When the client calls index page
    Then the response contains links to "version"

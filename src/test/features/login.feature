Feature: Login Page Validation

  Scenario: Login with valid username and password
    Given provide valid url
    When provide valid username and password
    Then click on login button

  Scenario: Login with parameterized username and password
    Given provide valid url
    When provide valid username as "mercury" and password as "mercury"
    Then click on login button
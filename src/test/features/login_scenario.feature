Feature: Verify login with Scenario outline

  Scenario Outline: Verify login success message
    Given provide valid urls
    When enter username as "<username>"
    And enter password as "<password>"
    Then click on submit button
    And verify login success message as "<message>"

    Examples:
      | username |  | password  |  | message             |
      | Admin1   |  | admin1231 |  | Invalid credentials |
      | Admin    |  | admin123  |  | Dashboard           |

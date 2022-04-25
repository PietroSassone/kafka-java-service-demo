@getUser
Feature: Demo service getUser by username endpoint test scenarios
  Testing the getUser by username endpoint
  Endpoint: GET /api/user/{userName}/getUser
  Parameters: username path variable

  Scenario: The endpoint should return existing user
    Given a user exists in the database with name Stella and a balance of 1000
      And the username parameter for the request is set to Stella
    When the getUser endpoint is called
    Then the response status code should be 200
      And the response body should contain the correct user info

  Scenario Outline: The endpoint should return a user not found response on nonexistent user/wrong username
    Given the username parameter for the request is set to <userNameParameter>
    When the getUser endpoint is called
    Then the response status code should be 404
      And the response body should contain a user not found message

   Examples:
    | userNameParameter |
    | Medved            |
    | null              |

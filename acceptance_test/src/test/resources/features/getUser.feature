@getUser
Feature: Demo service getUser by username endpoint test scenarios
  Testing the getUser by username endpoint
  Endpoint: GET /{userName}/getUser
  Parameters: username string path variable

  Scenario: The endpoint should return existing user
    Given a user exists in the database with name Stella and a balance of 1000
      And I set the username parameter for the request to Stella
    When I call the getUser endpoint
    Then the response status code should be 200
      And the response body should contain the correct user

  Scenario: The endpoint should return a user not found response on nonexistent user
    Given I set the username parameter for the request to Medved
    When I call the getUser endpoint
    Then the response status code should be 404
      And the response body should contain a user not found message

  Scenario Outline: The endpoint should return a parameter validation error
    Given I set the username parameter for the request to <userNameParameter>
    When I call the getUser endpoint
    Then the response status code should be 400

   Examples:
    | userNameParameter |
    |                   |
    | null              |

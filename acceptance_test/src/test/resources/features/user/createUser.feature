@createUser @KafkaUserEvent
Feature: Demo service create user endpoint test scenarios
Testing the create user endpoint
  Endpoint: POST /api/user/createUser
  Request body:
    *username string, must be between 6 and 20 characters
    *balance number, must be at least 0, maximum 1000 000 000 000

  Background:
    Given a user request is created

  Scenario Outline: The endpoint should create new user
    Given the userName value for the request is set to <userName>
      And the balance value for the request is set to <balance>
    When the createUser endpoint is called
    Then the response status code should be 201
      And the response body should contain the new user
      And the user should be present in the database
      And a user notification event should be present on Kafka
      And the event should be correct with USER_CREATED reason

    Examples:
      | userName             | balance         |
      | Stella               | 0.0             |
      | exactly 20 chars str | 1000000000000.0 |

  Scenario: The endpoint should return not acceptable response if user already exists
    Given a user exists in the database with name Commander Shepard and a balance of 999
      And the userName value for the request is set to Commander Shepard
      And the balance value for the request is set to 1000.001
    When the createUser endpoint is called
    Then the response status code should be 406
      And the response body should contain a resource already exists error
      And a user notification event should not be present on Kafka

  Scenario Outline: The endpoint should return a bad request on too long/null/short user name
    Given the userName value for the request is set to <userName>
      And the balance value for the request is set to 100
    When the createUser endpoint is called
    Then the response status code should be 400
      And the response body should contain a user name out of bounds error
      And a user notification event should not be present on Kafka

    Examples:
      | userName              |
      | Johny                 |
      | null                  |
      | 1 char over max limit |

  Scenario Outline: The endpoint should return a bad request on invalid balance
    Given the userName value for the request is set to Sad Samurai
      And the balance value for the request is set to <balance>
    When the createUser endpoint is called
    Then the response status code should be 400
      And the response body should contain a <expectedError> error
      And a user notification event should not be present on Kafka

    Examples:
      | balance             | expectedError     |
      | -0.0                | negative balance  |
      | null                | parameter is null |
      | string              | wrong request     |
      | 1000000000000.001   | balance too big   |

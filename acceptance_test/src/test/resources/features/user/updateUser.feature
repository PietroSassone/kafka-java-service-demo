@updateUser @createUser @KafkaUserEvent @test
Feature: Demo service update user endpoint test scenarios
Testing the update user endpoint
  Endpoint: POST /api/user/users/{id}
  If the id with given id already exists, the endpoint updates it with the values from the request.
  Request body:
    *username string, must be between 6 and 20 characters
    *balance number, must be at least 0, maximum 1000 000 000 000

  Background:
    Given a user request is created
      And the change reason value for the request is set to USER_CREATED

  Scenario Outline: The endpoint should create new user if it does not exist
    Given the user id parameter for the request is set to 5000
      And the userName value for the request is set to <userName>
      And the balance value for the request is set to <balance>
    When the updateUser endpoint is called
    Then the response status code should be 201
      And the response body should contain the new user
      And the user should be present in the database
      And a user notification event should be present on Kafka
      And the event should be correct with USER_CREATED reason

    Examples:
      | userName             | balance         |
      | Summer               | 0.0             |
      | exactly 20 chars lon | 1000000000000.0 |

  @KafkaUserEvent
  Scenario: The endpoint should update user if it already exists with the given id
    Given a user exists in the database with name Gandalf the Grey and a balance of 100000
      And the userName value for the request is set to <newUserName>
      And the balance value for the request is set to <newBalance>
      And the change reason value for the request is set to <changeReason>
    When the updateUser endpoint is called with the id of the existing user
    Then the response status code should be 201
      And the response body should contain the correct user info
      And the user should be present in the database with the updated details
      And a user notification event should be present on Kafka
      And the event should be correct with <changeReason> reason

    Examples:
      | newUserName       | newBalance  | changeReason      |
      | Gandalf the White | 100000      | USER_NAME_CHANGE  |
      | Gandalf the Grey  | 1000000000  | BALANCE_INCREASED |
      | Gandalf the Blue  | 99999       | BALANCE_REDUCED   |

  Scenario: The endpoint should return not acceptable response instead of creating user if another user already exists with given name
    Given a user exists in the database with name Captain Kirrahe and a balance of 8888888
      And the user id parameter for the request is set to 5000
      And the userName value for the request is set to Captain Kirrahe
      And the balance value for the request is set to 99999999
    When the updateUser endpoint is called
    Then the response status code should be 406
      And the response body should contain a resource already exists error
      And a user notification event should not be present on Kafka

  Scenario Outline: The endpoint should return a bad request on too long/null/short user name
    Given the userName value for the request is set to <userName>
      And the balance value for the request is set to 100
    When the updateUser endpoint is called
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
    When the updateUser endpoint is called
    Then the response status code should be 400
      And the response body should contain a <expectedError> error
      And a user notification event should not be present on Kafka

    Examples:
      | balance           | expectedError     |
      | -0.0              | negative balance  |
      | null              | parameter is null |
      | string            | wrong request     |
      | 1000000000000.001 | balance too big   |

  Scenario Outline: The endpoint should return a bad request on invalid change reason
    Given the userName value for the request is set to Sad Samurai
      And the balance value for the request is set to 123
      And the change reason value for the request is set to <changeReason>
    When the updateUser endpoint is called
    Then the response status code should be 400
      And the response body should contain a <expectedError> error
      And a user notification event should not be present on Kafka

    Examples:
      | changeReason | expectedError     |
      |              | wrong request     |
      | null         | parameter is null |
      | updates      | wrong request     |

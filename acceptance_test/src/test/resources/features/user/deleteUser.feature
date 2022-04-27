@deleteUser
Feature: Demo service deleteUser by id endpoint test scenarios
  Testing the deleteUser by id endpoint
  Endpoint: DELETE /api/user/users/{id}
  Parameters: id path variable

  @Kafka
  Scenario: The endpoint should delete existing user
    Given a user exists in the database with name Sierra and a balance of 117
    When the deleteUser endpoint is called
    Then the response status code should be 204
      And the response body code should be empty
      And the user should not be present in the database
      And a user notification event should be present on Kafka
      And the event should be correct with USER_DELETED reason

  Scenario: The endpoint should return a user not found response for nonexistent user
    Given the user id parameter for the request is set to 9999
    When the deleteUser endpoint is called
    Then the response status code should be 404
      And the response body should contain a user not found by id message

   Scenario: The endpoint should return a bad request on wrong userId
     Given the user id parameter for the request is set to Harold
     When the deleteUser endpoint is called
     Then the response status code should be 400

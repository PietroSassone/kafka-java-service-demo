@purchase @KafkaPurchaseEvent
Feature: Demo service purchase event consuming from Kafka
  Testing the service consuming Kafka events that are published by the test

  Scenario: The service should consume purchase event from Kafka and process it, saving a purchase
    Given a user exists in the database with name Lola and a balance of 10000
      And a product exists in the database with name paper box and a price of 1200
      And a product exists in the database with name hammer and a price of 2000
      And a purchase event is prepared
      And the existing user id is added to the purchase event
      And the existing products are added to the purchase event
    When the purchase event is sent to Kafka
    Then the purchase should be present in the database

  Scenario: The service should consume purchase event from Kafka and process it, but purchase should not be saved due to nonexistent user
    Given a purchase event is prepared
    When the purchase event is sent to Kafka
    Then the purchase should not be present in the database

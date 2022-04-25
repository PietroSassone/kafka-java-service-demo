@getProduct
Feature: Demo service getProduct by id endpoint test scenarios
  Testing the getUser by username endpoint
  Endpoint: GET /api/product/{id}/getProduct
  Parameters: product id path variable

  Scenario: The endpoint should return existing product
    Given a product exists in the database with name Ukulele and a price of 750
    When the getProduct endpoint is called
    Then the response status code should be 200
      And the response body should contain the correct product

  Scenario: The endpoint should return a product not found response on nonexistent product
    Given the product id parameter for the request is set to 1
    When the getProduct endpoint is called
    Then the response status code should be 404
      And the response body should contain a product not found message

  Scenario Outline: The endpoint should return a bad request on wrong productId
    Given the product id parameter for the request is set to <productIdValue>
    When the getProduct endpoint is called
    Then the response status code should be 400

     Examples:
      | productIdValue |
      | Ukulele        |
      | null           |
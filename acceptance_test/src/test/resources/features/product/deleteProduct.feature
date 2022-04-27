@deleteProduct
Feature: Demo service deleteProduct by id endpoint test scenarios
  Testing the deleteProduct by id endpoint
  Endpoint: DELETE /api/product/products/{id}
  Parameters: id path variable

  Scenario: The endpoint should delete existing product
    Given a product exists in the database with name Bread and a price of 399
    When the deleteProduct endpoint is called
    Then the response status code should be 204
      And the response body code should be empty
      And the product should not be present in the database

  Scenario: The endpoint should return a product not found response for nonexistent product
    Given the product id parameter for the request is set to 9999
    When the deleteProduct endpoint is called
    Then the response status code should be 404
      And the response body should contain a product not found message

   Scenario: The endpoint should return a bad request on wrong productId
     Given the product id parameter for the request is set to Harold
     When the deleteProduct endpoint is called
     Then the response status code should be 400

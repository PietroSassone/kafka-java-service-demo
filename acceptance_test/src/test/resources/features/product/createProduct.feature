@createProduct
Feature: Demo service create product endpoint test scenarios
Testing the create product endpoint
  Endpoint: POST /api/product/createProduct
  Request body:
    *productName string, must be between 3 and 50 characters
    *price number, must be at least 0, maximum 100 000 000

  Scenario Outline: The endpoint should create new product
    Given the productName value for the request is set to <productName>
      And the price value for the request is set to <price>
    When the createProduct endpoint is called
    Then the response status code should be 201
      And the response body should contain the new product
      And the product should be present in the database

    Examples:
     | productName                                         | price         |
     | Deo                                                 | 0.00001       |
     | exactly the maximum 50 characters long name string  | 100000000     |

  Scenario: The endpoint should return not acceptable response if product already exists
    Given a product exists in the database with name shoe and a price of 999
      And the productName value for the request is set to shoe
      And the price value for the request is set to 0
    When the createProduct endpoint is called
    Then the response status code should be 406
      And the response body should contain a resource already exists error

  Scenario Outline: The endpoint should return a bad request on too long/null/short product name
    Given the productName value for the request is set to <productNameValue>
      And the price value for the request is set to 100
   When the createProduct endpoint is called
   Then the response status code should be 400
     And the response body should contain a product name out of bounds error

   Examples:
     | productNameValue                                    |
     | PC                                                  |
     | null                                                |
     | a_long_product_name_that_is_more_than_50_chars_by_1 |

  Scenario Outline: The endpoint should return a bad request on too long/short/null product name
    Given the productName value for the request is set to choccy moo moo juice
      And the price value for the request is set to <priceValue>
    When the createProduct endpoint is called
    Then the response status code should be 400
      And the response body should contain a <expectedError> error

    Examples:
      | priceValue      | expectedError     |
      | -0.0            | negative price    |
      | null            | parameter is null |
      | string          | wrong request     |
      | 100000000.00001 | price too big     |

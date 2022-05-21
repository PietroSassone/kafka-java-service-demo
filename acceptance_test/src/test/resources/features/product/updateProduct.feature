@updateProduct @createProduct
Feature: Demo service update product endpoint test scenarios
Testing the update product endpoint
  Endpoint: POST /api/product/products/{id}
  If the product with given id already exists, the endpoint updates it with the values from the request.
  Request body:
    *productName string, must be between 3 and 50 characters
    *price number, must be at least 0, maximum 100 000 000

  Background:
    Given a product request is created

  Scenario Outline: The endpoint should create new product if it does not exist
    Given the product id parameter for the request is set to 5000
      And the productName value for the request is set to <productName>
      And the price value for the request is set to <price>
    When the updateProduct endpoint is called
    Then the response status code should be 201
      And the response body should contain the new product
      And the product should be present in the database

    Examples:
     | productName                                         | price       |
     | Pen                                                 | 0.0         |
     | exactly the maximum 50 characters long user string  | 100000000.0 |

  Scenario: The endpoint should update product if it already exists with the given id
    Given a product exists in the database with name Book part 2 and a price of 999
      And the productName value for the request is set to <newProductName>
      And the price value for the request is set to <newPrice>
    When the updateProduct endpoint is called with the id of the existing product
    Then the response status code should be 201
      And the response body should contain the correct product
      And the product should be present in the database with the updated details

    Examples:
      | newProductName            | newPrice  |
      | Book part 2               | 999       |
      | Book part 2 with subtitle | 1500.0001 |

  Scenario: The endpoint should return not acceptable response instead of creating product if another product already exists with given name
    Given a product exists in the database with name keyboard and a price of 999
      And the product id parameter for the request is set to 5000
      And the productName value for the request is set to keyboard
      And the price value for the request is set to 0
    When the updateProduct endpoint is called
    Then the response status code should be 406
      And the response body should contain a resource already exists error

  Scenario Outline: The endpoint should return a bad request on too long/null/short product name
    Given the productName value for the request is set to <productName>
      And the price value for the request is set to 100
   When the updateProduct endpoint is called
   Then the response status code should be 400
     And the response body should contain a product name out of bounds error

   Examples:
     | productName                                         |
     | PC                                                  |
     | null                                                |
     | a_long_product_name_that_is_more_than_50_chars_by_1 |

  Scenario Outline: The endpoint should return a bad request on invalid price
    Given the productName value for the request is set to choccy moo moo juice
      And the price value for the request is set to <priceValue>
    When the updateProduct endpoint is called
    Then the response status code should be 400
      And the response body should contain a <expectedError> error

    Examples:
      | priceValue      | expectedError     |
      | -0.0            | negative price    |
      | null            | parameter is null |
      | string          | wrong request     |
      | 100000000.00001 | price too big     |

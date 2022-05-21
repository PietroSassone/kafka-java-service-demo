# kafka-java-rest-service-demo
A small Java backend service to demonstrate Spring-boot, REST API & Kafka usage & plus automated REST API & Kafka testing.  
* The goal of this project is to give a demo about acceptance test implementation of a REST API & Apache Kafka.  
* In order to do so, there's a simple Spring Boot REST API implementation included in the project.    
* The service has a local H2 DB connection, which is automatically set up and started along with starting the service.    
* The acceptance tests use Jersey REST client for calling the controllers of the service.  
* Also, there are unit tests for the controllers. Plus a few tests in the service module. They do not cover 100% functionality as I mostly focused on demonstrating higher level (acceptance) testing coverage with this project.

* The service supports CRUD operations for Users and Products via web controllers. Also, stores Purchase info received from Kafka.  
These entities have a limited dataset. In a real project they would store more data.  
Like currency and such. This is just a simple demo with limited data.

* Maven checkstyle is also configured for the service to enforce some coding rules.  

**1. What the service does**
- Through REST endpoints, one can create/update/query/delete users.
- Through REST endpoints, one can create/update/query/delete products.
- The users & products are stored in a H2 database, running in server mode.
- The name of the user/product to be created must be unique.  
A custom error response is returned by the controllers when trying to create a user/product with name identical to an existing one.
- If a user/product update request is received, the service checks whether a user/product with the provided id exists.  
If yes, it is updated. If no, it will be created and persisted instead.  
- If a user/product delete/get request is received, an error response is returned if the resource does not exist.
- The user/product details in the request bodies are validated according to length and value constraints.
- If a user is created or updated, the service sends a message about the event to Kafka.
- The service consumes Kafka events about purchase information. Validates them. If the user/products in the event are nonexistent, an error is logged.  
Otherwise, the purchase details are saved into the database. In real life these events would be sent by some other service.  
In this demo, they are sent from automated tests.
- All the functionality described here is tested in automated acceptance tests.  

**2. Technologies used**  
In the acceptance tests:  
- Cucumber 7 for Behaviour Specification and Data Driven Testing.
- Maven Failsafe plugin to run the acceptance test suite.
- TestNG for running the Cucumber tests.
- Hamcrest for asserts.
- Awaitility to wait dynamically for conditions in tests.
- Jersey REST client to send HTTP requests to the service, from the tests.
- Cluecumber plugin for visualization of test reports.
- Logback & Lombok for logging.
- Spring Core for dependency injection.
- Kafka Java client to test sending/consuming Kafka events.
- SkyScreamer JSONAssert for verifying JSON contents in tests.
- JDBC template to access the database used by the service.
- Jackson Object mapper for reading JSON files.

In the service:  
- Maven Surefire plugin to run the unit tests.
- TestNG, Hamcrest, Mockito for unit testing with mocking.
- Logback & Lombok for logging.
- Lombok to eliminate a lot of code.
- Spring boot for running the service, and creating REST controllers for the API.
- Spring boot web starter for controller exception handling via controller advices.
- Spring Core for dependency injection.
- Hateoas for REST representation models.
- Maven Checkstyle for enforcing coding conventions.
- Kafka Java client to create Kafka topics, publish and consume events.
- Java H2 client to run a H2 DB in persistent & server mode.
- Spring Data JPA for persistence implementation.
- Docker compose for starting the pre-requirements of the service.
- Swagger OpenAPI 3 for endpoint service documentation.

**3. Pre-requirements for running the application**
- Have Maven installed.
- Have Java installed, at least version 11.
- Have Docker with Compose OR Zookeper and Apache Kafka installed.
 
**4. How to start the application**
1. Open a terminal in the root directory of the repository.  
1. Build with ```mvn clean install```.  
1. Start Zookeeper and Apache Kafka. See notes below.  
1. The local H2 database server needs login credentials. These must be supplied in the ```application.properties``` of the web module.  
Also in the ```acceptance-test-dev.properties``` in the acceptance test module.  
The credentials must be the same in both property files. In my project, I have not pushed the credentials up to Github.  
The needed properties are there in both properties files. Only with no values.
1. Start the application from the terminal with ```mvn spring-boot:run -pl web```.

Start Zookeper and Kafka, can be done either by:
  * Installing and starting them separately based on the [official Apache Kafka guide](https://kafka.apache.org/quickstart).  
  * Or launching the docker compose file in the root folder of this repository. From a terminal, via ```docker-compose -f docker-compose.yml up```.    

**5. Swagger UI link:**  
The service has Swagger integration for easy interaction with the controllers from a browser.  
Open the swagger after the application has been started [here](http://localhost:8080/swagger-ui.html).

**6. Logging**  
The application uses Lombok and Logback for logging.  
Logs can be found in the ```target/logs``` directory of both the web, and the acceptance tests module.  

# Running the acceptance tests  
The data processing of the service is covered with automated tests in the acceptance test module.  
The tests cover both negative and positive cases. For the cases, Cucumber & Gherkin are used.  
Reading the Cucumber feature files, the test cases should be straightforward to understand.

The tests send HTTP requests, assert the responses, assert database content.  
Also publish & consume events from a locally running Apache Kafka. Then assert the event contents.
  
As a pre-requirement, the service with the local Kafka and Zookeeper should already be running.    

To start all the tests, use the following command from the repository root in a terminal:  
    ```
    mvn clean verify -P run-acceptance-tests -pl acceptance_test
    ```
To run only a subset of the test, add the ```-Dcucumber.feature.tags``` property to the runner command.  
The value of the property must be a tag that exists in at least one of the Cucumber feature files.

Example command to run only the get user endpoint's tests:
    ```
    mvn clean verify -P run-acceptance-tests -pl acceptance_test -Dcucumber.filter.tags=@getUser
    ```  
    
**Test reports**
The framework saves reports and logs in the target folder of the acceptance test module after a test run finishes.
1. Logs are saved in target/logs.
1. Cucumber reports are saved in target/cucumber-report.
1. More detailed fancy Cluecumber reports are saved in target/test-report.

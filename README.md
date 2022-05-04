# kafka-java-rest-service-demo
A small Java service to demonstrate Spring-boot, REST API & Kafka usage & plus automated REST API & Kafka testing.  
The goal of this project is to give a demo about acceptance test implementation of a REST API & Apache Kafka.  
In order to do so, there's a simple Spring Boot REST API implementation included in the project.    
The service has a local H2 DB connection, which is automatically set upd and started along with starting the service.    
The acceptance tests use Jersey REST client for calling the controllers of the service.  
At the moment there are no unit tests, as I wanted to demonstrate higher level testing. They may be added later.  
  
**1. Pre-requirements for running the application**
- Have Maven installed.
- Have Java installed, at least version 11.
- Have Docker with Compose OR Zookeper and Apache Kafka installed.
 
**2. How to start the application**
1. Open a terminal in the root directory of the repository.  
1. Build with ```mvn clean install```.  
1. Start Zookeeper and Apache Kafka. See notes below.  
1. Start the application from the terminal with ```mvn spring-boot:run -pl web```.

Start Zookeper and Kafka, can be done either by:
  * Installing and starting them separately based on the [official Apache Kafka guide](https://kafka.apache.org/quickstart).  
  * Or launching the docker compose file in the root folder of this repository. From a terminal, via ```docker-compose -f docker-compose.yml up```.    

**3. Swagger UI link:**  
The service has Swagger integration for easy interaction with the controllers from a browser.  
Open the swagger after the application has been started [here](http://localhost:8080/swagger-ui.html).

**4. Logging**  
The application uses Lombok and Logback for logging.  
Logs can be found in the ```target/logs``` directory of both the web, and the acceptance tests module.  

#Running the acceptance tests  
The tests send HTTP requests, assert the responses, assert database content.  
Also publish & consume events from a locally running Apache Kafka. Then assert the event contents.
  
As a pre-requirement, the service with the local Kafka and Zookeeper should already be running.    

To start the tests, use the following command from the repository root in a terminal:  
    ```
    mvn clean verify -P run-acceptance-tests -pl acceptance_test
    ```

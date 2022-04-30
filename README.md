# kafka-java-rest-service-demo
A small Java service to demonstrate Spring-boot, REST API & Kafka usage & plus automated REST API & Kafka testing.

**1. Pre-requirements for running the application**
- Have Maven installed.
- Have Java installed, at least version 11.
- Have Docker with Compose OR Zookeper and Apache Kafka installed.
 
**2.How to start the application**
1. Open a terminal in the root directory of the repository.  
1. Build with ```mvn clean install```.  
1. Start Zookeeper and Apache Kafka. See notes below.  
1. Start the application from the cmd with ```mvn spring-boot:run -pl web```.

Start Zookeper and Kafka, can be done either by:
  * Installing and starting them separately based on the official Apache Kafka guide.  
  * Or launching the docker compose file in the root folder of this repository. From cmd, via ```docker-compose.yaml up```.    

#Swagger UI link:  
http://localhost:8080/swagger-ui.html

#Running the acceptance tests  
As a pre-requirement, the service with the local Kafka and Zookeeper should already be running.  

To start the tests, use the following command from the acceptance test module ic a cmd:  
    ```
    mvn clean verify -P acceptance-tests -pl acceptance_tests
    ```

wsdl2rest-user-story
======================

Simple project created to evaluate the development process in VS Code and beyond
of a simple idea:

General idea - I am an integration developer (not a citizen integrator) using VS Code to create a project from scratch utilizing Camel and Spring-Boot.

General algorithm
* Input(s): Destination city and country (“Where are you traveling to?”)
* Outputs: Forecast for destination city for five days

Next step: Add a call to a SOAP service that retrieves a list of cities and their countries that can be used to feed the Destination

Resources:
* Get weather forecast from https://openweathermap.org/forecast16
* City list here - http://bulk.openweathermap.org/sample/city.list.json.gz
* Weather API docs - https://openweathermap.org/forecast5#XML 

Current Status
========================================
* Created simple Spring-boot project using Yeoman and camel-project-generator
* Updated Camel configuration to call weather service and get a report for a city/country pair
* Have simple SOAP service created that needs to be modified but runs 

Camel Project for Spring Boot
=========================================
To build this project use
```bash
    mvn install
```

To run the project you can execute the following Maven goal
```bash
    mvn spring-boot:run
```

To run the included simple web service, execute the following Maven goal
```bash
    mvn compile exec:java -Dexec.mainClass="com.simple.ws.SimpleWSPublisher"
```

To get the WSDL file from the web service: http://localhost:9999/ws/hello?wsdl

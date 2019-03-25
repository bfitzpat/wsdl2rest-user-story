# Simple wsdl2rest user story

This project is designed to create a simple setup for the following scenario:

1. I have a simple Camel-based service I want to integrate with a SOAP-based service.
2. My simple Camel-based service calls out to a weather service with a city/country pair to get a forecast.
3. I want to integrate a SOAP service that serves up a random city/country pair as a Restful process to feed the Camel service

## To run the SOAP-based JAX-WS RPC service

```bash
     mvn compile exec:java -Dexec.mainClass="com.simple.ws.SimpleWSPublisher"
```

The WSDL is then accesible at: http://localhost:9999/ws/random?wsdl

## To run the simple Camel service

```bash
     mvn clean spring-boot:run
```

## To use the wsdl2rest extension with the SOAP service running...

There are two main options for using the extension to generate your Camel configuration. To start the process, inside VS Code, press F1 or Ctrl+Shift+P to bring up the Command Palette, and type wsdl2rest.

To reference the sample SOAP WSDL with a URL:

* Select the 'wsdl2rest: Create Camel Rest DSL configuration from WSDL file URL' option in the list.
* In the drop-down that appears, type the URL to the WSDL you wish to access. In this case: http://localhost:9999/ws/random?wsdl

Then specify the following:

* which DSL to generate the Camel configuration for - Spring
* the output directory for generated CXF artifacts (defaults to src/main/java)
* provide no address for the running jaxws endpoint, it will default to http://localhost:9999/ws/random from the WSDL
* provide no address for the generated jaxrs endpoint (optional: defaults to http://localhost:8081/jaxrs)

### Once artifacts are generated

Once the wsdl2rest utility generates the artifacts (CXF wrappers for the SOAP service and a Camel Rest DSL configuration), you must integrate the new REST configuration into the existing spring-boot camel configuration.

Provide details here...

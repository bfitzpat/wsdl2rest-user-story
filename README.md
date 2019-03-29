# wsdl2rest-user-story

This project was created to evaluate how to develop a Camel project in VS Code that would integrate the work done with vscode-wsdl2rest and other VS Code extensions.

## General idea

I am an integration developer (not a citizen integrator) using VS Code to create a project from scratch utilizing Camel and Spring-Boot. Initially I want to use a city and country and get a simple weather forecast for them. And then I want to randomize the city/country and provide a rotating weather report for a number of Red Hat office locations.

## Resources

* Get weather forecast from <https://openweathermap.org/forecast16>
* City list here - <http://bulk.openweathermap.org/sample/city.list.json.gz>
* Weather API docs - <https://openweathermap.org/forecast5#XML>
* Red Hat office locations - <https://www.redhat.com/en/about/office-locations>

## Steps

* Created a simple Spring-boot project using Yeoman and camel-project-generator
* Updated Camel configuration to call weather service and get a report for a city/country pair
* Created a simple SOAP service created that serves up a random city and provides a method to retrieve the country for that city
* Spun the web service into a separate project <https://github.com/bfitzpat/wsdl2rest-user-story-ws>
* Used wsdl2rest to generate a CXF wrapper and associated Rest DSL configuration for the SOAP service
* Integrated the generated configuration into the spring-boot project 
* Tested the generated RESTful calls to see if I could get the data I desired (random city and country for that city)
* Completed the integration, using the RESTful calls to provide a random city and its country to the weather service on a rotating basis

## Weather service

To use the weather API, you will need to sign up for a free account at <https://home.openweathermap.org> and get your own application ID from <https://home.openweathermap.org/api_keys>. That can be put into the /src/main/resources/camel-context.xml file for use in the WEATHERAPIID constant.

Note: The weather API calls will fail unless you replace "WEATHERAPIID" with a valid API key from the service.

## Current status of checked-in code

Currently the project is in its "final" state, meaning fully integrated with the running SOAP service, the generated RESTful calls, and calling the weather service with a random city/country combination. However, you can return the project to any of three states:

* To reset the project to its simplest configuration, copy the contents of steps/1-initial-pom.xml to pom.xml, remove the src/main/java/ws folder and its contents, and copy the contents of steps/1-initial-camel-context.xml to src/main/resources/camel-context.xml
* To reset the project to it's interim configuration (after wsdl2rest artifacts have been created), copy the contents of steps/2-final-pom.xml to pom.xml, copy the ws folder and its contents to src/main/java, and copy the contents of steps/2-interim-camel-context.xml to src/main/resources/camel-context.xml
* To reset to the final state (after the RESTful calls have been integrated into the weather service route), copy the contents of steps/2-final-pom.xml to pom.xml, copy the ws folder and its contents to src/main/java, and copy the contents of steps/3-final-camel-context.xml to src/main/resources/camel-context.xml

### Camel Project for Spring Boot

Initially, I created a simple Camel project on spring-boot. I wanted to do this using the Project Initializer (<https://marketplace.visualstudio.com/items?itemName=redhat.project-initializer>) but none of the available options could be used to create a simple starter project, so instead I used the vscode-yeoman extension (<https://marketplace.visualstudio.com/items?itemName=camel-tooling.yo>) and the generator-camel-project Yeoman generator (<https://www.npmjs.com/package/generator-camel-project>). This blog post <https://developers.redhat.com/blog/2019/03/21/getting-started-with-the-updated-vs-code-yeoman-extension-for-camel-projects/> and this post <https://developers.redhat.com/blog/2019/01/07/using-the-yeoman-camel-project-generator-to-jump-start-a-project/> provide the details necessary for installing and running the generator.

In my case, I used the following values:

* Default project name
* Default Camel version
* Camel DSL - spring-boot
* Default package name

I then modified the generated project to run with Java 8 or above (copy the contents of steps/1-initial-pom.xml to pom.xml) and created a simple route:

```xml
        <route id="_weather">
            <from id="_fromTimer" uri="timer://simpleTimer?period=10000&amp;repeatCount=1"/>

            <!-- stash city -->
            <setHeader headerName="cityName">
                <constant>Raleigh</constant>
            </setHeader>
            <!-- stash country -->
            <setHeader headerName="countryName">
                <constant>us</constant>
            </setHeader>
            <log message="Retrieving weather for $simple{header.cityName}, $simple{header.countryName}"/>

            <!-- get application ID from https://home.openweathermap.org/api_keys when you sign up for a free account -->
            <setProperty propertyName="appId">
                <constant>WEATHERAPIID</constant> <!-- replace this with your API key -->
            </setProperty>
            <setProperty propertyName="weatherUri">
                <simple>q=$simple{header.cityName},$simple{header.countryName}%26APPID=${property.appId}%26mode=xml</simple>
            </setProperty>
            <toD id="_toWeather" uri="http://api.openweathermap.org/data/2.5/weather?${property.weatherUri}"/>

            <!-- output the response -->
            <setBody id="_setWeatherBody">
                <simple>Received a weather response: ${body}</simple>
            </setBody>
            <to id="_toStream" uri="stream:out"/>
        </route>
```

Once that is done, I could build the project

```bash
    mvn install
```

And then I ran the project with the following Maven goal

```bash
    mvn spring-boot:run
```

You can also use the script:

```bash
    ./runcamel.sh
```

Eventually in the Camel log you should see a weather report for Raleigh, NC (city = Raleigh, country = us) appear.

To kill the running service, hit Ctrl-C in the console to stop the running Spring Boot instance.

## Sample Web Service

I then created a simple JAX-WS SOAP service to serve up a random entry from a list of cities and then get the country code for the random city. I had issues developing a SOAP call that would return a structure (an array of two items) that wsdl2rest and Spring-boot would actually work with. So I broke it into two calls.

I spun the service into its own project to avoid any collisions between wsdl2rest generated artifacts and the base SOAP service it was calling.

To run this simple web service, clone <https://github.com/bfitzpat/wsdl2rest-user-story-ws> and then execute the following Maven goal in that project directory

```bash
    mvn compile exec:java -Dexec.mainClass="com.simple.ws.SimpleWSPublisher"
```

You can also use the script:

```bash
    ./runwebservice.sh
```

To access the WSDL file from the web service, use this URL: <http://localhost:9999/ws/random?wsdl>

## To use the wsdl2rest extension with the SOAP service running

There are two main options for using the extension to generate your Camel configuration. To start the process, inside VS Code, press F1 or Ctrl+Shift+P to bring up the Command Palette, and type wsdl2rest.

To reference the sample SOAP WSDL with a URL:

* Select the 'wsdl2rest: Create Camel Rest DSL configuration from WSDL file URL' option in the list.
* In the drop-down that appears, type the URL to the WSDL you wish to access. In this case: <http://localhost:9999/ws/random?wsdl>

Then specify the following:

* which DSL to generate the Camel configuration for - Spring
* the output directory for generated CXF artifacts - src/generated/java (defaults to src/main/java)
* provide no address for the running jaxws endpoint, it will default to <http://localhost:9999/ws/random> from the WSDL
* provide no address for the generated jaxrs endpoint (optional: defaults to <http://localhost:8081/jaxrs)>

Note: You can simulate having run this task by copying the steps/ws directory to src/main/java, copying the contents of steps/2-final-pom.xml into the pom.xml file, and the contents of steps/2-interim-camel-context.xml into the src/main/resources/camel-context.xml file.

### Once wsdl2rest artifacts are generated

Once the wsdl2rest utility generates the artifacts (CXF wrappers for the SOAP service and a Camel Rest DSL configuration), you must integrate the new REST configuration into the existing spring-boot camel configuration.

You can copy the generated Rest DSL from the wsdl2rest-generated configuration into your src/main/resources/camel-context.xml file. This will look similar to steps/2-interim-camel-context.xml.

You will also need to update the POM with additional dependencies so that the Rest DSL and CXF artifacts run properly in Spring Boot. Your pom.xml file will look similar to steps/2-final-pom.xml.

With those changes in place, you should be able to run the Spring Boot camel project again and see the data for Raleigh/us.

You should also be able to test the following RESTful calls to your new running Rest DSL-wrapped service:

```xml
    http://localhost:8081/jaxrs/randomcity
    http://localhost:8081/jaxrs/countryforcity/"insert city here"
```

For example, you should get "us" for:

```xml
    http://localhost:8081/jaxrs/countryforcity/"Raleigh" should return "us"
    http://localhost:8081/jaxrs/countryforcity/"Boston" should return "us"
    http://localhost:8081/jaxrs/countryforcity/"Denver" should return "us"
    http://localhost:8081/jaxrs/countryforcity/"Dublin" should return "ie"
    http://localhost:8081/jaxrs/countryforcity/"London" should return "gb"
```

And so on.

With our RESTful service in place, we can then work to integrate it with our weather service.

## Integrating our RESTful calls

When designing this Camel configuration, I initially tried including everything in a single route, but it turned out to be much easier to split things up.

To call our "randomcity" RESTful get, I created a new route (this will look similar to the changes in steps/3-final-camel-context.xml.):

```xml
        <route id="_city">
            <!-- every few seconds, kick this off with a random city/country pair -->
            <from id="_fromTimer" uri="timer://simpleTimer?period=10000&amp;repeatCount=3"/>
            <!-- get the random city name -->
            <log message="Retrieving random city"/>
            <toD id="_toCity" uri="http://localhost:8081/jaxrs/randomcity"/>

            <!-- pass along city name -->
            <setHeader headerName="cityName">
                <simple resultType="String">${body}</simple>
            </setHeader>
            <log message="Random city passed: $simple{header.cityName}"/>
            <to uri="direct:getCountry" />
        </route>
```

I then passed the city along to "countryforcity"...

```xml
        <route id="_country">
            <!-- get the country code for the random city -->
            <from uri="direct:getCountry"/>
            <log message="Retrieving country code for: $simple{in.header.cityName}"/>
            <setHeader headerName="CamelHttpMethod">
                <constant>GET</constant>
            </setHeader>
            <!-- stash city -->
            <setProperty propertyName="stashCity">
                <simple resultType="String">$simple{in.header.cityName}</simple>
            </setProperty>
            <toD id="_toCountry" uri="http://localhost:8081/jaxrs/countryforcity/%22$simple{in.header.cityName}%22"/>

            <!-- set city in header-->
            <setHeader headerName="cityName">
                <simple resultType="String">$simple{property.stashCity}</simple>
            </setHeader>
            <!-- set country in header -->
            <setHeader headerName="countryName">
                <simple resultType="String">${body}</simple>
            </setHeader>
            <log message="Country for random city $simple{header.cityName} = $simple{header.countryName}"/>
            <to uri="direct:getWeather" />
        </route>
```

And I revised the "_weather" route to use the city and country values we stashed as headers:

```xml
        <route id="_weather">
            <from uri="direct:getWeather"/>
            <!-- get application ID from https://home.openweathermap.org/api_keys when you sign up for a free account -->
            <setProperty propertyName="appId">
                <constant>WEATHERAPIID</constant> <!-- replace this with your API key -->
            </setProperty>
            <setProperty propertyName="weatherUri">
                <simple>q=$simple{in.header.cityName},$simple{in.header.countryName}%26APPID=${property.appId}%26mode=xml</simple>
            </setProperty>
            <toD id="_toWeather" uri="http://api.openweathermap.org/data/2.5/weather?${property.weatherUri}"/>

            <!-- output the response -->
            <setBody id="_setWeatherBody">
                <simple>Received a weather response: ${body}</simple>
            </setBody>
            <to id="_toStream" uri="stream:out"/>
        </route>
```

Now when we run our Camel Spring Boot service again, it will run through our random city/country process three times, retrieving three different city/country combinations and getting a weather report for each.

## What I learned along the way

This took a while to figure out, but I eventually managed to get a working service together. That included:

* Starting a base Camel project on spring-boot that uses a http call to get weather data for a city/country pair (using vscode-yeoman (<https://marketplace.visualstudio.com/items?itemName=camel-tooling.yo>) and the generator-camel-project Yeoman generator (<https://www.npmjs.com/package/generator-camel-project>) until the Project Initializer (<https://marketplace.visualstudio.com/items?itemName=redhat.project-initializer>) includes more or easier Fuse/Camel-specific options to kick off a new project)
* Building a simple SOAP service that loads a JSON file and serves up a random city and enables searching for its country code
* Using vscode-wsdl2rest (<https://marketplace.visualstudio.com/items?itemName=camel-tooling.vscode-wsdl2rest)> to generate a CXF wrapper and Camel Rest DSL configuration to offer the SOAP methods as RESTful calls
* Updating the base Camel project dependencies (pom.xml) and integrated the generated wsdl2rest artifacts (camel-context.xml)
* Testing the generated RESTful calls
* Writing a series of Camel routes to call the RESTful service, get the city and country, and feed those into the weather data call

Interesting challenges going forward:

* Updating Maven configuration in an existing project to introduce additional (and correct) dependencies - was especially interesting with some of the spring boot issues vs. standard spring or blueprint
* Figuring out if there’s a way to inject new content into an existing camel configuration XML file (perhaps do an import into an existing one and keep the rest dsl config separate?)
* Better debugging of Camel routes -- was painful to debug into a running XML route
* Initially creating the project (I used the Yeoman work I did previously, not the Project Initializer)
* Would be good to have some structured way to scaffold some tests based on a Camel project

Other things I found:

* Camel LSP was VERY useful in VS Code
* Had to resort to going back to Eclipse to write/test my SOAP service because I was not getting the kind of tooling help I was looking for in VS Code. This is likely just not knowing how best to set things up for Java in VS Code

However, this was an EXTREMELY educational process on many fronts and we will revisit this as we move forward.

Spring Boot (Actuator) Health Plugin for Jenkins
================================================

Installation
------------
    Install the Actuator Health Plugin.

Job based configuration
-----------------------
    Add your Actuator Health Plugin installation in Manage Jenkins -> Configure System. 

Pipeline based configuration
----------------------------

#### Sample Jenkins pipeline plugin groovy script:

```
node {
    step([$class: 'ActuatorBuilder', url: 'http://localhost:8082/health'])
}
```

Replace url above with your spring boot health endpoint. 

#### Example test endpoint:

If you need an example spring boot health check endpoint to test with you can:
```
cd example/
python -m SimpleHTTPServer 8082
```

Development
-----------

You can run the application locally for development testing using:
```mvn hpi:run```

http://localhost:8080/jenkins
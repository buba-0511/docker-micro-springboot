# Taller3-AREP — Mini Spring Boot HTTP Server

## Project Overview
A custom HTTP server framework in Java with IoC, reflection, and annotation-based routing (inspired by Spring Boot). Listens on port **35000**.

## Stack
- Java 17, Maven
- No external frameworks — pure Java with reflection
- JUnit 5 for testing

## Key Commands
```bash
mvn clean install   # compile + test
mvn exec:java       # run the server
mvn test            # run tests only
```

Alternative run:
```bash
java -cp target/classes com.mycompany.httpserver.MicroSpringBoot.MicroSpringBoot
```

## Project Structure
```
src/main/java/com/mycompany/httpserver/
  MicroSpringBoot/MicroSpringBoot.java  # entry point, scans & loads controllers
  HttpServer.java                        # core HTTP server (port 35000)
  HttpRequest.java / HttpResponse.java   # request/response abstractions
  RestController.java                    # @RestController annotation
  GetMapping.java                        # @GetMapping annotation
  RequestParam.java                      # @RequestParam annotation
  Service.java                           # service interface
  examples/
    HelloController.java                 # GET /app/hello
    GreetingController.java              # GET /app/greeting?name=...

src/test/java/Tests.java                 # JUnit 5 integration tests
www/webroot/                             # static files (HTML, PNG)
```

## Architecture
- `MicroSpringBoot` scans a package for `@RestController` classes and registers `@GetMapping` methods in `HttpServer.services` map
- `HttpServer` handles incoming requests: static files from `www/webroot/`, or routes `/app/*` to registered controllers
- Controllers use `@RequestParam` for query parameter injection

## Test Endpoints
- `http://localhost:35000/app/hello`
- `http://localhost:35000/app/greeting?name=Camilo`

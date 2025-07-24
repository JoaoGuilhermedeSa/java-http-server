# Java HTTP Server

This project is a lightweight, annotation-based HTTP server built in Java. It allows you to create REST endpoints easily using custom annotations.

## Technologies Used

*   **Java:** The core programming language.
*   **Maven:** For project build and dependency management.
*   **SLF4J & Logback:** For logging.

## How to Run the Application

1.  **Prerequisites:**
    *   Java 23 or higher
    *   Maven

2.  **Running the server:**

    Open your terminal and run the following Maven command:

    ```bash
    mvn exec:java
    ```

    The server will start on port 8080.

## Creating New Endpoints

To create new endpoints, you can use the `@Controller` and `@Get` annotations.

1.  **Create a Controller:**

    A controller is a simple Java class annotated with `@Controller`.

    ```java
    package org.example.controllers;

    import org.example.annotations.Controller;
    import org.example.annotations.Get;

    @Controller
    public class MyNewController {

        @Get("/my-new-endpoint")
        public String handleMyNewEndpoint() {
            return "This is a new endpoint!";
        }
    }
    ```

2.  **Add `@Get` Endpoints:**

    Inside your controller, create public methods that return a `String` and annotate them with `@Get`. The value of the `@Get` annotation is the path of your endpoint.

    The server will automatically scan for classes annotated with `@Controller` in the `org.example` package and register the methods annotated with `@Get` as HTTP GET endpoints.

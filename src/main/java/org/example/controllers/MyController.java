package org.example.controllers;

import org.example.annotations.Controller;
import org.example.annotations.Get;

@Controller
public class MyController {

    @Get("/")
    public String handleRoot() {
        return "Hello, World!";
    }

    @Get("/hello")
    public String handleHello() {
        return "Hello, from MyController!";
    }
}
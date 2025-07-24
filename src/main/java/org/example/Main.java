package org.example;

import org.example.server.CustomHttpServer;

public class Main {
    public static void main(String[] args) throws Exception {
        CustomHttpServer.create(8080)
                .scanControllers("org.example")
                .start();
        }
    }
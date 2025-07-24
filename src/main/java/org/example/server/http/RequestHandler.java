package org.example.server.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Map;

public class RequestHandler implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);
    private final Socket socket;
    private final Router router;

    public RequestHandler(Socket socket, Router router) {
        this.socket = socket;
        this.router = router;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isBlank()) {
                return; // No request
            }

            String[] requestParts = requestLine.split(" ");
            if (requestParts.length != 3) {
                sendErrorResponse(out, HttpStatus.BAD_REQUEST);
                return;
            }

            String method = requestParts[0];
            String path = requestParts[1];

            if (!"GET".equals(method)) {
                sendErrorResponse(out, HttpStatus.METHOD_NOT_ALLOWED);
                return;
            }

            Map.Entry<Object, Method> route = router.getRoute(path);
            if (route == null) {
                sendErrorResponse(out, HttpStatus.NOT_FOUND);
                return;
            }

            try {
                String responseBody = (String) route.getValue().invoke(route.getKey());
                sendSuccessResponse(out, responseBody);
            } catch (Exception e) {
                LOGGER.error("Error invoking method for path: {}", path, e);
                sendErrorResponse(out, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (IOException e) {
            LOGGER.error("Error handling request", e);
        }
    }

    private void sendSuccessResponse(PrintWriter out, String body) {
        out.println("HTTP/1.1 " + HttpStatus.OK);
        out.println("Content-Type: text/plain");
        out.println("Content-Length: " + body.length());
        out.println();
        out.println(body);
    }

    private void sendErrorResponse(PrintWriter out, HttpStatus status) {
        out.println("HTTP/1.1 " + status);
        out.println("Content-Type: text/plain");
        out.println("Content-Length: " + status.getMessage().length());
        out.println();
        out.println(status.getMessage());
    }
}

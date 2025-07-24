package org.example.server;

import org.example.server.http.RequestHandler;
import org.example.server.http.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CustomHttpServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomHttpServer.class);
    private final int port;
    private final Router router;
    private final ExecutorService executorService;

    public CustomHttpServer(int port) {
        this.port = port;
        this.router = new Router();
        this.executorService = Executors.newFixedThreadPool(10); // A pool of 10 threads
    }

    public static CustomHttpServer create(int port) {
        return new CustomHttpServer(port);
    }

    public CustomHttpServer scanControllers(String basePackage) throws Exception {
        List<Class<?>> controllerClasses = ClassPathScanner.findControllerClasses(basePackage);
        router.registerRoutes(controllerClasses);
        return this;
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            LOGGER.info("Server started on port {}", port);
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    executorService.submit(new RequestHandler(clientSocket, router));
                } catch (IOException e) {
                    LOGGER.error("Error accepting client connection", e);
                }
            }
        } finally {
            executorService.shutdown();
        }
    }
}

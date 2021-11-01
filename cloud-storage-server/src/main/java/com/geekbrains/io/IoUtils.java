package com.geekbrains.io;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentLinkedDeque;

public class IoUtils {
    private ConcurrentLinkedDeque<ClientHandler> clients;

    public IoUtils() {
        init();
    }

    private void init() {
        Path root = Path.of("root");
        try {
            if (!Files.exists(root)) {
                Files.createDirectory(root);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        clients = new ConcurrentLinkedDeque<>();
        try (ServerSocket server = new ServerSocket(8189)) {
            System.out.println("Server started.");
            while (true) {
                Socket socket = server.accept();
                System.out.println("Client accepted");
                ClientHandler handler = new ClientHandler(socket, this);
                clients.add(handler);
                new Thread(handler).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

package com.geekbrains.io;

import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Server {


    public Server() {
        new IoUtils();
    }


    public static void main(String[] args) {
        new Server();
    }
}

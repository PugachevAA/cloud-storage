package com.geekbrains.io;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ClientHandler implements Runnable{

    private static int cnt = 0;
    private String userName;
    private Path root;
    private Path clientDir;

    private final IoUtils io;
    private Socket client;
    private DataInputStream dis;
    private DataOutputStream dos;

    public ClientHandler(Socket socket, IoUtils io) throws Exception {
        root = Path.of("root");
        if (!Files.exists(root)) {
            Files.createDirectory(root);
        }
        cnt++;
        userName = "User_" + cnt;
        clientDir = root.resolve(userName);
        if (!Files.exists(clientDir)) {
            Files.createDirectory(clientDir);
        }

        this.client = socket;
        this.io = io;
    }



    @Override
    public void run() {
        try {
            dis = new DataInputStream(client.getInputStream());
            dos = new DataOutputStream(client.getOutputStream());
            System.out.println("открыли входящий поток");
            byte[] buffer = new byte[8129];
            while (true) {
                String fileName = "";
                long fileSize = 0;
                int readBytes = 0;
                System.out.println("Вошли в вечный цикл");

                fileName = dis.readUTF();
                System.out.println("Получили имя файла:" + fileName);
                 FileOutputStream fos = new FileOutputStream(clientDir.resolve(fileName).toString());
                System.out.println("Создали выходной поток");
                fileSize = dis.readLong();
                System.out.println("Размер файла " + fileSize + " байт");

                while (true) {
                    readBytes = dis.read(buffer);
                    fos.write(buffer,0,readBytes);
                    fileSize -= readBytes;

                    if (fileSize < 1) {
                        System.out.println("Данные кончились");
                        fos.close();
                        responseOk();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Connection was broken");
            e.printStackTrace();
        }
    }

    private void responseOk() throws IOException {
        dos.writeUTF("Файл отправлен");
        System.out.println("отправили ответ ОК");
        dos.flush();
    }


}

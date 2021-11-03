package com.geekbrains.nio;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Set;


// ls -> вернуть список                          - выполнено
// cat file - вывести на экран содержание файла  - выполнено
// cd path - перейти в каталог                   - выполнено
// touch file - создать пустой файл              - выполнено
public class NioServer {

    private ServerSocketChannel server;
    private Selector selector;
    private ByteBuffer buffer;
    private SocketChannel channel;
    private File directory;

    public NioServer() throws Exception {
        buffer = ByteBuffer.allocate(256);
        server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(8989));
        selector = Selector.open();
        server.configureBlocking(false);
        server.register(selector, SelectionKey.OP_ACCEPT);
        directory = new File("root").getAbsoluteFile();


        while (server.isOpen()) {

            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isAcceptable()) {
                    handleAccept(key);
                }
                if (key.isReadable()) {

                    handleRear(key);
                }
                iterator.remove();
            }
        }
    }

    private void handleRear(SelectionKey key) throws Exception {
        StringBuilder sb = new StringBuilder();

        channel = (SocketChannel) key.channel();

        while (true) {
            int read = channel.read(buffer);
            if (read == -1) {
                channel.close();
                return;
            }
            if (read == 0) {
                break;
            }
            buffer.flip();
            while (buffer.hasRemaining()) {
                sb.append((char) buffer.get());
            }
            buffer.clear();
        }

        String result = sb.toString();
        System.out.println(result);
        checkCommand(result);
    }

    //Проверка команд
    private void checkCommand(String result) throws Exception {
        result = result.replaceAll("[\r\n]", "");
        String[] command = result.split(" ");

        if (command.length == 1) {

        }
        switch (command[0]) {
            case "ls":
                ls();
                break;
            case "cat":
                if (command.length > 1) {
                    cat(command[1]);
                } else {
                    writeReceive("Не указан файл");
                }
                break;
            case "cd":
                if (command.length > 1) {
                    cd(command[1]);
                } else {
                    writeReceive("Не указан путь");
                }
                break;
            case "touch":
                if (command.length > 1) {
                    touch(command[1]);
                } else {
                    writeReceive("Не указан файл");
                }
                break;
            default:
                writeReceive("Команды не существует");
                break;
        }
    }

    //ответ клиенту
    private void writeReceive(String s) throws IOException {
        channel.write(ByteBuffer.wrap((s+"\r\n").getBytes(StandardCharsets.UTF_8)));
        //выводим текущую директорию
        channel.write(ByteBuffer.wrap((directory + "> ").getBytes(StandardCharsets.UTF_8)));
    }

    //ls
    private void ls() throws IOException {
        File[] files = directory.listFiles();
        StringBuilder sb = new StringBuilder();
        if (files.length > 0) {
            sb.append(" "+files[0].getName());
            for (int i = 1; i < files.length; i++) {
                sb.append("\r\n "+files[i].getName());
            }
        }
        writeReceive(sb.toString());
    }

    //cat
    private void cat(String file) throws IOException {
        File readableFile = new File(Path.of(directory.toString(),file).toString());
        if (readableFile.exists()) {
            writeReceive(Files.readString(readableFile.toPath()));
        } else {
            writeReceive("Файл не существует");
        }
    }

    //cd
    private void cd(String path) throws IOException {
        File destDirectory;
        if (!path.contains(directory.getPath()) && !directory.getPath().contains(path)) {
            destDirectory = Path.of(directory.toString(),path).toFile();
        } else {
            destDirectory = new File(path);
        }
        if(destDirectory.exists()) {
            directory=destDirectory;
            writeReceive("ok");
        } else {
            writeReceive("Путь не найден");
        }
    }

    //touch
    private void touch(String file) throws IOException {
        File readableFile = new File(Path.of(directory.toString(),file).toString());
        if (readableFile.exists()) {
            writeReceive("Файл уже существует");
        } else {
            Files.createFile(readableFile.toPath());
            writeReceive("Файл создан");
        }
    }


    private void handleAccept(SelectionKey key) throws Exception {
        SocketChannel channel = server.accept();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ, "Hello world");
    }

    public static void main(String[] args) throws Exception {
        new NioServer();
    }

}

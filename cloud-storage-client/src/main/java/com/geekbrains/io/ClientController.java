package com.geekbrains.io;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    private Socket server;
    private DataInputStream dis;
    private DataOutputStream dos;
    private FileInputStream fis;

    private File[] clientFiles;
    private File outputFile;
    private File path;

    public ListView<String> listView;
    public TextField input;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        path = new File("clientroot");

        if (!path.exists()) {
            path.mkdir();
        }
        clientFiles = path.listFiles();
        for (File file : clientFiles) {
            listView.getItems().add(file.getName());
        }

        try {
            server = new Socket("localhost", 8189);
            dis = new DataInputStream(server.getInputStream());
            dos = new DataOutputStream(server.getOutputStream());

            Thread readThread = new Thread(() -> {
                try {
                    while (true) {
                        String message = dis.readUTF();
                        Platform.runLater(() -> input.setText(message));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            readThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void selectFile(MouseEvent mouseEvent) {
        input.clear();
        input.setText(listView.getSelectionModel().getSelectedItem());
    }


    public void sendFile(ActionEvent actionEvent) throws Exception {
        String fileName = input.getText();
        outputFile = new File(path.getName() + "\\" + fileName);
        dos.writeUTF(fileName);
        fis = new FileInputStream(outputFile);
        dos.writeLong(outputFile.length());

        byte[] buffer = new byte[8129];
        int readBytes = 0;
        while(true) {
            readBytes = fis.read(buffer);
            if (readBytes == -1) {
                input.clear();
                fis.close();
                break;
            }
            dos.write(buffer,0,readBytes);
        }
        dos.flush();
    }
}

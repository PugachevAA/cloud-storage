package com.geekbrains.io;

import com.geekbrains.model.AbstractMessage;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
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
    private ObjectDecoderInputStream dis;
    private ObjectEncoderOutputStream dos;
    private FileInputStream fis;

    private File[] clientFiles;
    private File outputFile;
    private File path;

    public ListView<String> listView;
    public TextField input;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            server = new Socket("localhost", 8189);
            dos = new ObjectEncoderOutputStream(server.getOutputStream());
            dis = new ObjectDecoderInputStream(server.getInputStream());

            Thread readThread = new Thread(() -> {
                try {
                    while (true) {
                        AbstractMessage message = (AbstractMessage) dis.readObject();
                        Platform.runLater(() -> listView.getItems().add(message.getMessage()));
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


    public void sendMessage(ActionEvent actionEvent) throws Exception {
        String str = input.getText();
        input.clear();
        dos.writeObject(new AbstractMessage(str));
        dos.flush();

//        outputFile = new File(path.getName() + "\\" + fileName);
//        dos.writeUTF(fileName);
//        fis = new FileInputStream(outputFile);
//        dos.writeLong(outputFile.length());
//
//        byte[] buffer = new byte[8129];
//        int readBytes = 0;
//        while(true) {
//            readBytes = fis.read(buffer);
//            if (readBytes == -1) {
//                input.clear();
//                fis.close();
//                break;
//            }
//            dos.write(buffer,0,readBytes);
//        }
//        dos.flush();
    }
}

package com.myhome.rpgkeyboard.keyboardview;

import android.os.AsyncTask;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import org.json.JSONObject;

public class SocketClient extends AsyncTask<Void, Void, Void> {
    private String address;
    private int port;
    private JSONObject message;

    public SocketClient(String address, int port, JSONObject message) {
        this.address = address;
        this.port = port;
        this.message = message;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try (Socket socket = new Socket(address, port);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             DataInputStream dis = new DataInputStream(socket.getInputStream())) {

            // Convert JSON object to string and send it to the server
            dos.writeUTF(message.toString());

            // Receive response from server
            String response = dis.readUTF();
            System.out.println("Server response: " + response);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
package com.myhome.rpgkeyboard.keyboardview;


import static com.myhome.rpgkeyboard.keyboardview.ConstantsKt.LOCAL_PORT;
import static com.myhome.rpgkeyboard.keyboardview.ConstantsKt.getEMOTION;
import static com.myhome.rpgkeyboard.keyboardview.ConstantsKt.getTextEmpty;
import static com.myhome.rpgkeyboard.keyboardview.ConstantsKt.setEMOTION;
import static com.myhome.rpgkeyboard.keyboardview.KeyboardNumpad.inputConnection;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import org.json.JSONObject;
import org.json.JSONException;

public class SocketClient extends AsyncTask<Void, Void, JSONObject> {
    private final boolean empty;
    private String address;
    private int port;
    private int localPort;
    private JSONObject message;

    public SocketClient(String address, int port, JSONObject message) {
        this.address = address;
        this.port = port;
        this.localPort = LOCAL_PORT;
        this.message = message;
        this.empty = getTextEmpty();
    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
        JSONObject jsonResponse = null;
        Socket socket = new Socket();
        DataOutputStream dos = null;
        DataInputStream dis = null;



        try {
            Log.d("SocketClient", "Attempting to create socket...");
            socket.bind(new InetSocketAddress(localPort));
            socket.connect(new InetSocketAddress(address, port), 5000); // 5 seconds timeout
            Log.d("SocketClient", "Socket created: " + socket);

            dos = new DataOutputStream(socket.getOutputStream());
            Log.d("SocketClient", "DataOutputStream created: " + dos);

            // Convert JSON object to string and send it to the server
            dos.writeUTF(message.toString());
            dos.flush();  // Ensure data is sent out
            Log.d("SocketClient", "Message sent: " + message.toString());

            dis = new DataInputStream(socket.getInputStream());
            while (dis.available() == 0) {
                // Wait for data to be available
            }
            Log.d("SocketClient", "Data available: " + dis.available());

            // Read the data into a byte array
            byte[] responseBytes = new byte[dis.available()];
            dis.readFully(responseBytes);

            // Convert byte array to string
            String response = new String(responseBytes, "UTF-8");
            Log.d("socket_response", "Response received: " + response);

            jsonResponse = new JSONObject(response);
            if (jsonResponse != null) {
                setEMOTION(jsonResponse.getString("emotion"));
                Log.d("EMOTION", "EMOTION: " + jsonResponse.getString("emotion"));
                Log.d("EMOTION", getEMOTION());
            } else {
                Log.d("SocketClient", "No response or incomplete data received");
            }
        } catch (IOException | JSONException e) {
            Log.e("SocketClient", "Exception occurred: " + e.getMessage(), e);
        } finally {
            try {
                if (dos != null) dos.close();
                if (dis != null) dis.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                Log.e("SocketClient", "Error closing streams or socket: " + e.getMessage(), e);
            }
        }
        return jsonResponse;
    }


}
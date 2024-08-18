package com.myhome.rpgkeyboard.keyboardview;

import org.json.JSONObject;

public interface SocketClientCallback {
    void onResponse(JSONObject response);
}
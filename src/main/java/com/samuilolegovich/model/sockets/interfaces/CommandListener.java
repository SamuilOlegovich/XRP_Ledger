package com.samuilolegovich.model.sockets.interfaces;

import org.json.JSONObject;

public interface CommandListener {
    void onResponse(JSONObject response);
}

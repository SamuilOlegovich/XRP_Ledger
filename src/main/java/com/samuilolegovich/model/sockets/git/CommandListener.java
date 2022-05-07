package com.samuilolegovich.model.sockets.git;

import org.json.JSONObject;

public interface CommandListener {
    void onResponse(JSONObject response);
}

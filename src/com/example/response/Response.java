package com.example.response;

import org.json.JSONObject;

import javax.validation.constraints.NotNull;

public class Response {
    public static JSONObject newResponseObject (ResponseType responseType, String message) {
        return newResponseObject(responseType, message, null, null);
    }

    public static JSONObject newResponseObject (ResponseType responseType, String message, Integer code) {
        return newResponseObject(responseType, message, code, null);
    }

    public static JSONObject newResponseObject (ResponseType responseType, String message, JSONObject moreInfoObject) {
        return newResponseObject(responseType, message, null, moreInfoObject);
    }

    public static JSONObject newResponseObject (@NotNull ResponseType responseType, String message, Integer code, JSONObject moreInfoObject) {
        JSONObject response = new JSONObject();
        JSONObject object = new JSONObject();

        object.put("message", message);
        if (code != null) object.put("code", code);

        if (moreInfoObject != null) {
            for (String key : moreInfoObject.keySet()) {
                object.put(key, moreInfoObject.get(key));
            }
        }

        if (responseType == ResponseType.SUCCESS) {
            response.put("success", object);
        } else if (responseType == ResponseType.ERROR) {
            response.put("error", object);
        }

        return response;
    }
}

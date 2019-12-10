package com.example.response;

import org.json.JSONObject;

public class Error {

    public static void serverError () {

    }

    public static void notFound () {

    }

    public static JSONObject badRequest (String message, String schema) {
        JSONObject moreInfoObject = new JSONObject();
        moreInfoObject.put("schema", schema);
        return Response.newResponseObject(ResponseType.ERROR, message, 400, moreInfoObject);
    }

    public static JSONObject wrongUserSchema () {
        return badRequest("Wrong user schema", "{name[STRING], age[INT], about[STRING]}");
    }

}

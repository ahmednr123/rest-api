package com.example.response;

import org.json.JSONObject;

enum DocType {
    USER("User"), APP("App"), ACTIVITY("Activity");

    private String type;

    String getType () {
        return type;
    }

    DocType(String type) {
        this.type = type;
    }
}

public class Success {
    private static JSONObject added (DocType docType, Integer id, JSONObject doc) {
        JSONObject moreInfoObject = new JSONObject();
        moreInfoObject.put("doc_id", id);
        moreInfoObject.put("doc", doc);
        return Response.newResponseObject(ResponseType.SUCCESS,docType.getType() + " added successfully", moreInfoObject);
    }

    private static JSONObject deleted (DocType docType, Integer id, JSONObject doc) {
        JSONObject moreInfoObject = new JSONObject();
        moreInfoObject.put("doc_id", id);
        moreInfoObject.put("doc", doc);
        return Response.newResponseObject(ResponseType.SUCCESS,docType.getType() + " deleted successfully", moreInfoObject);
    }

    private static JSONObject updated (DocType docType, Integer id, JSONObject doc) {
        JSONObject moreInfoObject = new JSONObject();
        moreInfoObject.put("doc_id", id);
        moreInfoObject.put("doc", doc);
        return Response.newResponseObject(ResponseType.SUCCESS,docType.getType() + " updated successfully", moreInfoObject);
    }

    // =============================================================== //

    public static JSONObject userAdded (Integer id, JSONObject userObject) {
        return added(DocType.USER, id, userObject);
    }

    public static JSONObject appAdded (Integer id, JSONObject appObject) {
        return added(DocType.APP, id, appObject);
    }

    public static JSONObject activityAdded (Integer id, JSONObject activityObject) {
        return added(DocType.ACTIVITY, id, activityObject);
    }

    // =============================================================== //

    public static JSONObject userDeleted (Integer id, JSONObject userObject) {
        return deleted(DocType.USER, id, userObject);
    }

    public static JSONObject appDeleted (Integer id, JSONObject appObject) {
        return deleted(DocType.APP, id, appObject);
    }

    public static JSONObject activityDeleted (Integer id, JSONObject activityObject) {
        return deleted(DocType.ACTIVITY, id, activityObject);
    }

    // =============================================================== //

    public static JSONObject userUpdated (Integer id, JSONObject userObject) {
        return updated(DocType.USER, id, userObject);
    }

    public static JSONObject appUpdated (Integer id, JSONObject appObject) {
        return updated(DocType.APP, id, appObject);
    }

    public static JSONObject activityUpdated (Integer id, JSONObject activityObject) {
        return updated(DocType.ACTIVITY, id, activityObject);
    }
}

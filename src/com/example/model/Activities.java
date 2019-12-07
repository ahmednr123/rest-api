package com.example.model;

import com.example.exception.InternalServerException;
import com.example.exception.NotFoundException;
import com.example.util.SQLQuery;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.logging.Logger;

public class Activities {
    private static Logger LOGGER = Logger.getLogger(Activities.class.getName());

    private Activities () { }

    public static String getAllActivities (Integer user_id, Integer app_id, Integer offset, Integer limit) {
        if (limit > 10)
            limit = 10;

        JSONObject respObject = new JSONObject();
        JSONArray appList = new JSONArray();

        respObject.put("list", appList);

        SQLQuery query = null;
        try {
            query = new SQLQuery("SELECT id, name, description FROM activities WHERE app_id=? LIMIT ?,?");
            query.setInt(1, app_id);
            query.setInt(2, offset);
            query.setInt(3, limit);

            Integer finalLimit = limit;
            query.execute((ResultSet resultSet) -> {
                Integer count = offset;
                while (resultSet.next()) {
                    count++;
                    appList.put(
                            getJSON(
                                    resultSet.getInt("id"),
                                    resultSet.getString("name"),
                                    resultSet.getString("description")
                            )
                    );
                }

                if (count - offset == finalLimit) {
                    respObject.put("next_offset", count);
                }
            });
        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
            throw new InternalServerException("Server error. Could not fetch activities.");
        } finally {
            if (query != null)
                query.close();
        }

        return respObject.toString();
    }

    public static String getActivity (Integer user_id, Integer app_id, Integer id) {
        JSONObject respObject = new JSONObject();
        SQLQuery query = null;
        try {
            query = new SQLQuery("SELECT id, name, description FROM activities WHERE id=? AND app_id=?");
            query.setInt(1, id);
            query.setInt(2, app_id);

            query.execute((ResultSet resultSet) -> {
                if (resultSet.next()) {
                    respObject.put(
                        "doc",
                        getJSON(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("description")
                        )
                    );
                } else {
                    throw new NotFoundException("Activity not found");
                }
            });
        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
            throw new InternalServerException("Server error. Could not fetch activity.");
        } finally {
            if (query != null)
                query.close();
        }

        return respObject.toString();
    }

    public static String addActivity (Integer user_id, Integer app_id, String name, String description) {
        JSONObject respObject = new JSONObject();
        SQLQuery query = null;
        try {
            query = new SQLQuery("INSERT INTO activities (app_id, name, description) SELECT apps.id, ?, ? FROM apps WHERE apps.id=? AND apps.user_id=?", true);
            query.setString(1, name);
            query.setString(2, description);
            query.setInt(3, app_id);
            query.setInt(4, user_id);

            query.execute((ResultSet resultSet) -> {
                if (resultSet.next()) {
                    Integer id = resultSet.getInt(1);
                    JSONObject successObject = new JSONObject();
                    successObject.put("message", "Activity added successfully");
                    successObject.put("id", id);
                    respObject.put("success", successObject);
                } else {
                    throw new NotFoundException("App/User doesn't exist. Invalid app_id/user_id!");
                }
            });
        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
            throw new InternalServerException("Server error. Activity was not added");
        } finally {
            if (query != null)
                query.close();
        }

        return respObject.toString();
    }

    private static JSONObject getJSON (Integer id, String name,  String description) {
        JSONObject userObject = new JSONObject();
        userObject.put("id", id);
        userObject.put("name", name);
        userObject.put("description", description);
        return userObject;
    }
}

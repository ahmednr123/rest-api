package com.example.model;

import com.example.exception.BadRequestException;
import com.example.exception.InternalServerException;
import com.example.exception.NotFoundException;
import com.example.utils.SQLQuery;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.logging.Logger;

public class Apps {
    private static Logger LOGGER = Logger.getLogger(Apps.class.getName());

    private Apps () { }

    public static String getAllApps (Integer user_id, Integer offset, Integer limit) {
        if (offset == null) offset = 0;
        if (limit == null) limit = 10;
        if (limit > 10)
            limit = 10;

        JSONObject respObject = new JSONObject();
        JSONArray appList = new JSONArray();

        respObject.put("list", appList);
        SQLQuery query = null;
        try {
            query = new SQLQuery("SELECT id, name, description FROM apps WHERE user_id=? LIMIT ?,?");
            query.setInt(1, user_id);
            query.setInt(2, offset);
            query.setInt(3, limit);

            Integer finalLimit = limit;
            Integer finalOffset = offset;
            query.execute((ResultSet resultSet) -> {
                Integer count = finalOffset;
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

                if (count - finalOffset == finalLimit) {
                    respObject.put("next_offset", count);
                }
            });
        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
            throw new InternalServerException("Server error. Could not fetch apps.");
        } finally {
            if (query != null)
                query.close();
        }

        return respObject.toString();
    }

    public static String getApp (Integer user_id, Integer id) {
        JSONObject respObject = new JSONObject();
        SQLQuery query = null;
        try {
            query = new SQLQuery("SELECT id, name, description FROM apps WHERE id=? AND user_id=?");
            query.setInt(1, id);
            query.setInt(2, user_id);

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
                    throw new NotFoundException("App not found");
                }
            });
        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
            throw new InternalServerException("Server error. Could not fetch app.");
        } finally {
            if (query != null)
                query.close();
        }

        return respObject.toString();
    }

    public static String addApp (Integer user_id, String name, String description) {
        JSONObject respObject = new JSONObject();
        SQLQuery query = null;
        try {
            query = new SQLQuery("INSERT INTO apps (user_id, name, description) VALUES (?,?,?)", true);
            query.setInt(1, user_id);
            query.setString(2, name);
            query.setString(3, description);

            query.execute((ResultSet resultSet) -> {
                if (resultSet.next()) {
                    Integer id = resultSet.getInt(1);
                    JSONObject successObject = new JSONObject();
                    successObject.put("message", "App added successfully");
                    successObject.put("id", id);
                    respObject.put("success", successObject);
                }
            });
        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
            // 1452 = Foreign Key failure on inserting record
            if (e.getErrorCode() == 1452) {
                throw new NotFoundException("User doesn't exist. Invalid user_id.");
            } else {
                throw new InternalServerException("Server error. App was not added.");
            }
        } finally {
            if (query != null)
                query.close();
        }

        return respObject.toString();
    }

    public static String deleteApp (Integer user_id, Integer app_id) {
        JSONObject respObject = new JSONObject();

        SQLQuery query = null;
        try {
            query = new SQLQuery("DELETE FROM apps WHERE id=? AND user_id=?");
            query.setInt(1, app_id);
            query.setInt(2, user_id);
            Integer res = query.execute();

            if (res >= 1) {
                JSONObject successObject = new JSONObject();
                successObject.put("message", "App deleted successfully");
                respObject.put("success", successObject);
            } else {
                throw new NotFoundException("App not found");
            }
        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
            // 1452 = Foreign Key failure on inserting record
            if (e.getErrorCode() == 1452) {
                throw new NotFoundException("User doesn't exist. Invalid user_id.");
            } else {
                throw new InternalServerException("Server error. App was not deleted.");
            }
        } finally {
            if (query != null)
                query.close();
        }

        return respObject.toString();
    }

    public static String updateApp (Integer user_id, Integer id, String name, String description) {
        int least = 0;
        String queryString = "UPDATE apps SET ";
        if (name != null){ queryString += "name=?,"; least++;}
        if (description != null){ queryString += "description=?,"; least++;}

        if (least == 0)
            throw new
                    BadRequestException("Wrong app update schema. At least one field must be present.",
                    "{name[STRING], description[STRING]}");

        queryString = queryString.substring(0, queryString.length()-1) + " WHERE id=? && user_id=?";
        System.out.println(queryString);

        JSONObject respObject = new JSONObject();

        SQLQuery query = null;
        Integer index = 1;
        try {
            query = new SQLQuery(queryString);
            if (name != null) { query.setString(index++, name); }
            if (description != null) { query.setString(index++, description); }
            query.setInt(index++, id);
            query.setInt(index, user_id);

            Integer res = query.execute();
            if (res >= 1) {
                JSONObject successObject = new JSONObject();
                successObject.put("message", "App updated successfully");
                respObject.put("success", successObject);
            } else {
                throw new NotFoundException("App not found");
            }
        } catch (SQLException e) {
            throw new InternalServerException("Server error.");
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

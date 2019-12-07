package com.example.model;

import com.example.exception.BadRequestException;
import com.example.exception.InternalServerException;
import com.example.exception.NotFoundException;
import com.example.utils.SQLQuery;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.logging.Logger;

public class Activities {
    private static Logger LOGGER = Logger.getLogger(Activities.class.getName());

    private Activities () { }

    public static String getAllActivities (Integer user_id, Integer app_id, Integer offset, Integer limit) {
        if (offset == null) offset = 0;
        if (limit == null) limit = 10;
        if (limit > 10)
            limit = 10;

        JSONObject respObject = new JSONObject();
        JSONArray appList = new JSONArray();

        respObject.put("list", appList);

        SQLQuery query = null;
        try {
            query = new SQLQuery("SELECT id, name, description FROM activities WHERE app_id=(SELECT id FROM apps WHERE id=? AND user_id=?) LIMIT ?,?");
            query.setInt(1, app_id);
            query.setInt(2, user_id);
            query.setInt(3, offset);
            query.setInt(4, limit);

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
            // 1452 = Foreign Key failure on inserting record
            if (e.getErrorCode() == 1452) {
                throw new NotFoundException("User/App doesn't exist. Invalid user_id/app_id.");
            } else {
                throw new InternalServerException("Server error. Could not fetch activities.");
            }
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
            query = new SQLQuery("SELECT id, name, description FROM activities WHERE id=? AND app_id=(SELECT id FROM apps WHERE id=? AND user_id=?)");
            query.setInt(1, id);
            query.setInt(2, app_id);
            query.setInt(3, user_id);

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
            // 1452 = Foreign Key failure on inserting record
            if (e.getErrorCode() == 1452) {
                throw new NotFoundException("User/App doesn't exist. Invalid user_id/app_id.");
            } else {
                throw new InternalServerException("Server error. Could not fetch activity.");
            }
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

    public static String deleteActivity (Integer user_id, Integer app_id, Integer activity_id) {
        JSONObject respObject = new JSONObject();

        SQLQuery query = null;
        try {
            query = new SQLQuery("DELETE FROM activities WHERE id=? AND app_id=(SELECT id FROM apps WHERE id=? AND user_id=?)");
            query.setInt(1, activity_id);
            query.setInt(2, app_id);
            query.setInt(3, user_id);
            Integer res = query.execute();

            if (res >= 1) {
                JSONObject successObject = new JSONObject();
                successObject.put("message", "Activity deleted successfully");
                respObject.put("success", successObject);
            } else {
                throw new NotFoundException("Activity not found");
            }
        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
            throw new InternalServerException("Server error. Activity was not deleted.");
        } finally {
            if (query != null)
                query.close();
        }

        return respObject.toString();
    }

    public static String updateActivity (Integer user_id, Integer app_id, Integer id, String name, String description) {
        int least = 0;
        String queryString = "UPDATE activities SET ";
        if (name != null){ queryString += "name=?,"; least++;}
        if (description != null){ queryString += "description=?,"; least++;}

        if (least == 0)
            throw new
                    BadRequestException("Wrong activity update schema. At least one field must be present.",
                    "{name[STRING], description[STRING]}");

        queryString = queryString.substring(0, queryString.length()-1) + " WHERE id=? && app_id=(SELECT id FROM apps WHERE id=? AND user_id=?)";
        System.out.println(queryString);

        JSONObject respObject = new JSONObject();

        SQLQuery query = null;
        Integer index = 1;
        try {
            query = new SQLQuery(queryString);
            if (name != null) { query.setString(index++, name); }
            if (description != null) { query.setString(index++, description); }
            query.setInt(index++, id);
            query.setInt(index++, app_id);
            query.setInt(index, user_id);

            Integer res = query.execute();
            if (res >= 1) {
                JSONObject successObject = new JSONObject();
                successObject.put("message", "Activity updated successfully");
                respObject.put("success", successObject);
            } else {
                throw new NotFoundException("Activity not found");
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

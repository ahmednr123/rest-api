package com.example.model;

import com.example.exception.BadRequestException;
import com.example.exception.InternalServerException;
import com.example.exception.NotFoundException;
import com.example.util.SQLQuery;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.management.QueryEval;
import java.sql.*;
import java.util.logging.Logger;

public class Users {
    private static Logger LOGGER = Logger.getLogger(Users.class.getName());

    private Users () { }

    public static String getAllUsers (Integer offset, Integer limit) {
        if (limit > 10)
            limit = 10;

        JSONObject respObject = new JSONObject();
        JSONArray userList = new JSONArray();
        respObject.put("list", userList);

        SQLQuery query = null;
        try {
            query = new SQLQuery("SELECT id, name, age, about FROM users LIMIT ?,?");
            query.setInt(1, offset);
            query.setInt(2, limit);
            Integer finalLimit = limit;
            query.execute((ResultSet resultSet) -> {
                Integer count = offset;
                while (resultSet.next()) {
                    count++;
                    userList.put(
                        getJSON(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getInt("age"),
                            resultSet.getString("about")
                        )
                    );
                }

                if (count - offset == finalLimit) { respObject.put("next_offset", count); }
            });
        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
            throw new InternalServerException("Server error. Could not fetch users.");
        } finally {
            if (query != null)
                query.close();
        }

        return respObject.toString();
    }

    public static String getUser (Integer id) {
        JSONObject respObject = new JSONObject();

        SQLQuery query = null;
        try {
            query = new SQLQuery("SELECT id, name, age, about FROM users WHERE id=?");
            query.setInt(1, id);
            query.execute((ResultSet resultSet) -> {
                if (resultSet.next()) {
                    respObject.put("doc",
                            getJSON(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getInt("age"),
                            resultSet.getString("about")
                        )
                    );
                } else {
                    throw new NotFoundException("User not found");
                }
            });
        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
            throw new InternalServerException("Server error. Could not fetch user.");
        } finally {
            if (query != null)
                query.close();
        }

        return respObject.toString();
    }

    public static String addUser (String name, Integer age, String about) {
        JSONObject respObject = new JSONObject();

        SQLQuery query = null;
        try {
            query = new SQLQuery("INSERT INTO users (name, age, about) VALUES (?,?,?)", true);
            query.setString(1, name);
            query.setInt(2, age);
            query.setString(3, about);
            query.execute((ResultSet resultSet) -> {
                if (resultSet.next()) {
                    Integer id = resultSet.getInt(1);
                    JSONObject successObject = new JSONObject();
                    successObject.put("message", "User added successfully");
                    successObject.put("id", id);
                    respObject.put("success", successObject);
                }
            });
        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
            throw new InternalServerException("Server error. User was not added.");
        } finally {
            if (query != null)
                query.close();
        }

        return respObject.toString();
    }

    public static String removeUser (Integer user_id) {
        JSONObject respObject = new JSONObject();

        SQLQuery query = null;
        try {
            query = new SQLQuery("DELETE FROM users WHERE id=?");
            query.setInt(1, user_id);
            Integer res = query.execute();

            if (res >= 1) {
                JSONObject successObject = new JSONObject();
                successObject.put("message", "User deleted successfully");
                respObject.put("success", successObject);
            } else {
                throw new NotFoundException("User not found");
            }
        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
            throw new InternalServerException("Server error. User was not added.");
        } finally {
            if (query != null)
                query.close();
        }

        return respObject.toString();
    }

    public static String updateUser (Integer id, String name, Integer age, String about) {
        int least = 0;
        String queryString = "UPDATE users SET ";
        if (name != null){ queryString += "name=?,"; least++;}
        if (age != null){ queryString += "age=?,"; least++;}
        if (about != null){ queryString += "about=?,"; least++;}

        if (least == 0)
            throw new
            BadRequestException("Wrong user update schema. At least one field must be present.",
                    "{name[STRING], age[INT], about[STRING]}");

        queryString = queryString.substring(0, queryString.length()-1) + " WHERE id=?";
        System.out.println(queryString);

        JSONObject respObject = new JSONObject();

        SQLQuery query = null;
        Integer index = 1;
        try {
            query = new SQLQuery(queryString);
            if (name != null) { query.setString(index++, name); }
            if (age != null) { query.setInt(index++, age); }
            if (about != null) { query.setString(index++, about); }
            query.setInt(index, id);

            Integer res = query.execute();
            if (res >= 1) {
                JSONObject successObject = new JSONObject();
                successObject.put("message", "User updated successfully");
                respObject.put("success", successObject);
            } else {
                throw new NotFoundException("User not found");
            }
        } catch (SQLException e) {
            throw new InternalServerException("Server error.");
        } finally {
            if (query != null)
                query.close();
        }

        return respObject.toString();
    }

    private static JSONObject getJSON (Integer id, String name, Integer age, String about) {
        JSONObject userObject = new JSONObject();
        userObject.put("id", id);
        userObject.put("name", name);
        userObject.put("age", age);
        userObject.put("about", about);
        return userObject;
    }
}
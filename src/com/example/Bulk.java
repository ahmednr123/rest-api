package com.example;

import com.example.exception.BadRequestException;
import com.example.exception.NotFoundException;
import com.example.model.Users;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.*;

@Path("/bulk")
public class Bulk {

    // add {"user": {"doc": {}}}
    // add {"app": {"user_id":2, "doc": {}}}
    // add {"activity": {"user_id":2, "app_id":2, "doc": {}}}

    // delete {"user": {"id"}}
    // delete {"app": {"user_id":2, "id":}}
    // delete {"activity": {"user_id":2, "app_id":2, "id":}}

    // update {"user": {"doc": {}}}
    // update {"app": {"user_id":2, "doc": {}}}
    // update {"activity": {"user_id":2, "app_id":2, "doc": {}}}

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public String bulkPostApp(@Context final HttpServletRequest request, @Context final HttpServletResponse response, String body) throws IOException {
        InputStream inputStream = request.getInputStream();

        JSONArray replyArray = new JSONArray();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            try {
                JSONObject actionObject = new JSONObject(line);
                if (actionObject.has("add")) {

                } else if (actionObject.has("delete")) {

                } else if (actionObject.has("update")) {

                } else {
                    JSONObject errorObject = new JSONObject();
                    errorObject.put("code", 400);
                    errorObject.put("message", "Bad request");
                    replyArray.put(errorObject);
                }
            } catch (Exception e) {
                throw new BadRequestException("Wrong input");
            }
        }

        return "abc";
    }

    private JSONObject addUser(JSONObject userObject) {
        try {
            String name = userObject.getString("name");
            Integer age = userObject.getInt("age");
            String about = userObject.getString("about");
            return Users.addUser(name, age, about);
        } catch (NotFoundException e) {
            return new JSONObject(e.getMessage());
        } catch (JSONException e) {
            return new JSONObject();
        }
    }

    private JSONObject User(JSONObject userObject) {
        try {
            String name = userObject.getString("name");
            Integer age = userObject.getInt("age");
            String about = userObject.getString("about");
            return Users.addUser(name, age, about);
        } catch (NotFoundException e) {
            return new JSONObject(e.getMessage());
        } catch (JSONException e) {
            return new JSONObject();
        }
    }

    private JSONObject addUser(JSONObject userObject) {
        try {
            String name = userObject.getString("name");
            Integer age = userObject.getInt("age");
            String about = userObject.getString("about");
            return Users.addUser(name, age, about);
        } catch (NotFoundException e) {
            return new JSONObject(e.getMessage());
        } catch (JSONException e) {
            return new JSONObject();
        }
    }
}

package com.example;

import com.example.exception.BadRequestException;
import com.example.exception.InternalServerException;
import com.example.exception.NotFoundException;
import com.example.model.Activities;
import com.example.model.Apps;
import com.example.model.Users;
import com.example.response.Error;
import com.sun.javafx.geom.transform.Identity;
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
    public String bulkPostApp(@Context final HttpServletRequest request) throws IOException {
        InputStream inputStream = request.getInputStream();

        JSONArray replyArray = new JSONArray();
        JSONObject responseObject = new JSONObject();
        responseObject.put("response", replyArray);

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            try {
                JSONObject actionObject = new JSONObject(line);
                if (actionObject.has("add")) {
                    JSONObject docObject = actionObject.getJSONObject("add");
                    if (docObject.has("user")) {
                        replyArray.put(addUser(docObject.getJSONObject("user").getJSONObject("doc")));
                    } else if (docObject.has("app")) {
                        try {
                            Integer user_id = docObject.getJSONObject("app").getInt("user_id");
                            JSONObject doc = docObject.getJSONObject("app").getJSONObject("doc");
                            replyArray.put(addApp(user_id, doc));
                        } catch (JSONException e) {
                            replyArray.put(new JSONObject());
                        }
                    } else if (docObject.has("activity")) {
                        try {
                            Integer user_id = docObject.getJSONObject("activity").getInt("user_id");
                            Integer app_id = docObject.getJSONObject("activity").getInt("app_id");
                            JSONObject doc = docObject.getJSONObject("activity").getJSONObject("doc");
                            replyArray.put(addActivity(user_id, app_id, doc));
                        } catch (JSONException e) {
                            replyArray.put(new JSONObject());
                        }
                    }
                } else if (actionObject.has("delete")) {
                    JSONObject docObject = actionObject.getJSONObject("delete");
                    if (docObject.has("user")) {
                        Integer id = docObject.getJSONObject("user").getInt("id");
                        replyArray.put(deleteUser(id));
                    } else if (docObject.has("app")) {
                        try {
                            Integer user_id = docObject.getJSONObject("app").getInt("user_id");
                            Integer id = docObject.getJSONObject("app").getInt("id");
                            replyArray.put(deleteApp(user_id, id));
                        } catch (JSONException e) {
                            replyArray.put(new JSONObject());
                        }
                    } else if (docObject.has("activity")) {
                        try {
                            Integer user_id = docObject.getJSONObject("activity").getInt("user_id");
                            Integer app_id = docObject.getJSONObject("activity").getInt("app_id");
                            Integer id = docObject.getJSONObject("activity").getInt("id");
                            replyArray.put(deleteActivity(user_id, app_id, id));
                        } catch (JSONException e) {
                            replyArray.put(new JSONObject());
                        }
                    }
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

        return responseObject.toString();
    }

    private JSONObject addUser(JSONObject userObject) {
        try {
            String name = userObject.getString("name");
            Integer age = userObject.getInt("age");
            String about = userObject.getString("about");
            return Users.addUser(name, age, about);
        } catch (InternalServerException e) {
            return new JSONObject(e.getMessage());
        } catch (JSONException e) {
            return Error.wrongUserSchema();
        }
    }

    private JSONObject addApp(Integer user_id, JSONObject appObject) {
        try {
            String name = appObject.getString("name");
            String description = appObject.getString("description");
            return Apps.addApp(user_id, name, description);
        } catch (InternalServerException | NotFoundException e) {
            return new JSONObject(e.getMessage());
        } catch (JSONException e) {
            return Error.wrongUserSchema();

        }
    }

    private JSONObject addActivity(Integer user_id, Integer app_id, JSONObject activityObject) {
        try {
            String name = activityObject.getString("name");
            String description = activityObject.getString("description");
            return Activities.addActivity(user_id, app_id, name, description);
        } catch (InternalServerException | NotFoundException e) {
            return new JSONObject(e.getMessage());
        } catch (JSONException e) {
            return Error.wrongUserSchema();
        }
    }

    private JSONObject deleteUser (Integer id) {
        try {
            return Users.deleteUser(id);
        } catch (InternalServerException | NotFoundException e) {
            return new JSONObject(e.getMessage());
        }
    }

    private JSONObject deleteApp (Integer user_id, Integer id) {
        try {
            return Apps.deleteApp(user_id, id);
        } catch (InternalServerException | NotFoundException e) {
            return new JSONObject(e.getMessage());
        }
    }

    private JSONObject deleteActivity (Integer user_id, Integer app_id, Integer id) {
        try {
            return Activities.deleteActivity(user_id, app_id, id);
        } catch (InternalServerException | NotFoundException e) {
            return new JSONObject(e.getMessage());
        }
    }
}

package com.example;

import com.example.exception.BadRequestException;
import com.example.exception.UnAuthorizedException;
import com.example.model.Activities;
import com.example.model.Apps;
import com.example.model.Users;
import com.example.utils.JWTAuthenticator;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/users")
public class Rest {

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public String allUsers(@QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit) {
        if (offset == null) offset = 0;
        if (limit == null) limit = 10;

        return Users.getAllUsers(offset, limit);
    }

    @GET
    @Path("/{user_id}/apps")
    @Produces(MediaType.APPLICATION_JSON)
    public String allApps(@PathParam("user_id") Integer user_id, @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit) {
        if (offset == null) offset = 0;
        if (limit == null) limit = 10;

        return Apps.getAllApps(user_id, offset, limit);
    }

    @GET
    @Path("/{user_id}/apps/{app_id}/activities")
    @Produces(MediaType.APPLICATION_JSON)
    public String allActivities(@PathParam("user_id") Integer user_id, @PathParam("app_id") Integer app_id, @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit) {
        if (offset == null) offset = 0;
        if (limit == null) limit = 10;

        return Activities.getAllActivities(user_id, app_id, offset, limit);
    }

    // ================================================================================================ //

    @GET
    @Path("/{user_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String userInfo(@PathParam("user_id") Integer user_id) {
        return Users.getUser(user_id);
    }

    @GET
    @Path("/{user_id}/apps/{app_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String appInfo(@PathParam("user_id") Integer user_id, @PathParam("app_id") Integer app_id) {
        return Apps.getApp(user_id, app_id);
    }

    @GET
    @Path("/{user_id}/apps/{app_id}/activities/{activity_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String activityInfo(@PathParam("user_id") Integer user_id, @PathParam("app_id") Integer app_id, @PathParam("activity_id") Integer activity_id) {
        return Activities.getActivity(user_id, app_id, activity_id);
    }


    // POST
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addUser(@HeaderParam("Authorization") String authToken, String body) {
        boolean isTokenValid = JWTAuthenticator.validateJWT(authToken);
        if (!isTokenValid){
            throw new UnAuthorizedException("Unauthorized access");
        }

        JSONObject userObject = new JSONObject(body);
        System.out.println(userObject.toString());

        if (!userObject.has("name") || !userObject.has("age") || !userObject.has("about") || userObject.length() != 3) {
            throw new BadRequestException("Wrong user data schema", "{name[STRING], age[INT], about[STRING]}");
        }

        return Users.addUser(userObject.getString("name"), userObject.getInt("age"), userObject.getString("about"));
    }

    @POST
    @Path("/{user_id}/apps")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addApp(@HeaderParam("Authorization") String authToken, @PathParam("user_id") Integer user_id, String body) {
        boolean isTokenValid = JWTAuthenticator.validateJWT(authToken);
        if (!isTokenValid){
            throw new UnAuthorizedException("Unauthorized access");
        }

        JSONObject appObject = new JSONObject(body);
        System.out.println(appObject.toString());

        if (!appObject.has("name") || !appObject.has("description") || appObject.length() != 2) {
            throw new BadRequestException("Wrong app data schema", "{name[STRING], description[STRING]}");
        }

        return Apps.addApp(user_id, appObject.getString("name"), appObject.getString("description"));
    }

    @POST
    @Path("/{user_id}/apps/{app_id}/activities")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addActivity(@HeaderParam("Authorization") String authToken, @PathParam("user_id") Integer user_id, @PathParam("app_id") Integer app_id, String body) {
        boolean isTokenValid = JWTAuthenticator.validateJWT(authToken);
        if (!isTokenValid) {
            throw new UnAuthorizedException("Unauthorized access");
        }

        JSONObject activityObject = new JSONObject(body);
        System.out.println(activityObject.toString());

        if (!activityObject.has("name") || !activityObject.has("description") || activityObject.length() != 2) {
            throw new BadRequestException("Wrong activity data schema", "{name[STRING], description[STRING]}");
        }

        return Activities.addActivity(user_id, app_id, activityObject.getString("name"), activityObject.getString("description"));
    }

    // DELETE
    @DELETE
    @Path("/{user_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteUser(@HeaderParam("Authorization") String authToken, @PathParam("user_id") Integer user_id) {
        boolean isTokenValid = JWTAuthenticator.validateJWT(authToken);
        if (!isTokenValid) {
            throw new UnAuthorizedException("Unauthorized access");
        }

        return Users.deleteUser(user_id);
    }

    @DELETE
    @Path("/{user_id}/apps/{app_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteApp(@HeaderParam("Authorization") String authToken, @PathParam("user_id") Integer user_id, @PathParam("app_id") Integer app_id) {
        boolean isTokenValid = JWTAuthenticator.validateJWT(authToken);
        if (!isTokenValid) {
            throw new UnAuthorizedException("Unauthorized access");
        }

        return Apps.deleteApp(user_id, app_id);
    }

    @DELETE
    @Path("/{user_id}/apps/{app_id}/activities/{activity_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteActivity(@HeaderParam("Authorization") String authToken, @PathParam("user_id") Integer user_id, @PathParam("app_id") Integer app_id, @PathParam("activity_id") Integer activity_id) {
        boolean isTokenValid = JWTAuthenticator.validateJWT(authToken);
        if (!isTokenValid) {
            throw new UnAuthorizedException("Unauthorized access");
        }

        return Activities.deleteActivity(user_id, app_id, activity_id);
    }

    // PUT
    @PUT
    @Path("/{user_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String updateUser(@HeaderParam("Authorization") String authToken, @PathParam("user_id") Integer user_id, String body) {
        boolean isTokenValid = JWTAuthenticator.validateJWT(authToken);
        if (!isTokenValid) {
            throw new UnAuthorizedException("Unauthorized access");
        }

        JSONObject userObject = new JSONObject(body);

        String name = null;
        Integer age = null;
        String about = null;

        if (userObject.has("name")) name = userObject.getString("name");
        if (userObject.has("age")) age = userObject.getInt("age");
        if (userObject.has("about")) about = userObject.getString("about");

        return Users.updateUser(user_id, name, age, about);
    }

    @PUT
    @Path("/{user_id}/apps/{app_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String updateApp(@HeaderParam("Authorization") String authToken, @PathParam("user_id") Integer user_id, @PathParam("app_id") Integer app_id, String body) {
        boolean isTokenValid = JWTAuthenticator.validateJWT(authToken);
        if (!isTokenValid) {
            throw new UnAuthorizedException("Unauthorized access");
        }

        JSONObject userObject;
        try {
            userObject = new JSONObject(body);
        } catch (Exception e) {
            throw new BadRequestException("Wrong app data schema", "{name[STRING], description[STRING]}");
        }

        String name = null;
        String description = null;

        if (userObject.has("name")) name = userObject.getString("name");
        if (userObject.has("description")) description = userObject.getString("description");

        return Apps.updateApp(user_id, app_id, name, description);
    }

    @PUT
    @Path("/{user_id}/apps/{app_id}/activities/{activity_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String updateActivity(@HeaderParam("Authorization") String authToken, @PathParam("user_id") Integer user_id, @PathParam("app_id") Integer app_id, @PathParam("activity_id") Integer activity_id, String body) {
        boolean isTokenValid = JWTAuthenticator.validateJWT(authToken);
        if (!isTokenValid) {
            throw new UnAuthorizedException("Unauthorized access");
        }

        JSONObject userObject;
        try {
            userObject = new JSONObject(body);
        } catch (Exception e) {
            throw new BadRequestException("Wrong activity data schema", "{name[STRING], description[STRING]}");
        }

        String name = null;
        String description = null;

        if (userObject.has("name")) name = userObject.getString("name");
        if (userObject.has("description")) description = userObject.getString("description");

        return Activities.updateActivity(user_id, app_id, activity_id, name, description);
    }
}
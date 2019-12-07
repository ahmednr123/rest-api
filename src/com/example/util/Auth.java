package com.example.util;

import com.example.exception.BadRequestException;
import com.example.exception.NotFoundException;
import com.example.exception.UnAuthorizedException;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/auth")
public class Auth {
    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public String authUser(String body) {
        JSONObject userCredentials = new JSONObject(body);
        JSONObject respObject = new JSONObject();
        JSONObject statusObject = new JSONObject();

        if (!userCredentials.has("username") || !userCredentials.has("password") || userCredentials.length() != 2) {
            throw new BadRequestException("Invalid auth body format");
        }

        String username = userCredentials.getString("username");
        String password = userCredentials.getString("password");

        String jwtToken = JWTAuthenticator.generateJWT(username, password);
        if (jwtToken == null) {
            throw new UnAuthorizedException("Authorization failed");
        } else {
            statusObject.put("message","Authorization successful");
            statusObject.put("token", jwtToken);
            respObject.put("success", statusObject);
        }

        return respObject.toString();
    }
}
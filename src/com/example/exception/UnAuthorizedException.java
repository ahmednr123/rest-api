package com.example.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class UnAuthorizedException extends WebApplicationException {
    public UnAuthorizedException(String message) {
        super(Response.status(Response.Status.UNAUTHORIZED)
                .entity("{\"error\":{\"message\":\""+message+"\",\"code\":"+Response.Status.UNAUTHORIZED.getStatusCode()+"}}")
                .type(MediaType.APPLICATION_JSON).build());
    }
}
package com.example.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class NotFoundException extends WebApplicationException {
    public NotFoundException(String message) {
        super(Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\":{\"message\":\""+message+"\",\"code\":"+Response.Status.NOT_FOUND.getStatusCode()+"}}")
                .type(MediaType.APPLICATION_JSON).build());
    }
}
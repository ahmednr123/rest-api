package com.example.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class InternalServerException extends WebApplicationException {
    public InternalServerException(String message) {
        super(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":{\"message\":\""+message+"\",\"code\":"+Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()+"}}")
                .type(MediaType.APPLICATION_JSON).build());
    }
}

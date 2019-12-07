package com.example.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class BadRequestException extends WebApplicationException {
    public BadRequestException(String message) {
        super(Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":{\"message\":\""+message+"\", \"code\":"+Response.Status.BAD_REQUEST.getStatusCode()+"}}")
                .type(MediaType.APPLICATION_JSON).build());
    }
    public BadRequestException(String message, String schema) {
        super(Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":{\"message\":\""+message+"\", \"code\":"+Response.Status.BAD_REQUEST.getStatusCode()+", \"schema\":\""+schema+"\"}}")
                .type(MediaType.APPLICATION_JSON).build());
    }
}

package com.example.exception.mapper;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
    public Response toResponse(NotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\":{\"message\":\"Resource not found\",\"code\":"+Response.Status.NOT_FOUND.getStatusCode()+"}}")
                .type(MediaType.APPLICATION_JSON).build();
    }
}
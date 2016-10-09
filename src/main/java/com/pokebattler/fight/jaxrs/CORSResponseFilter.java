package com.pokebattler.fight.jaxrs;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
@PreMatching
@Priority(Priorities.AUTHORIZATION)
public class CORSResponseFilter implements ContainerResponseFilter, ExceptionMapper<Exception> {
    Logger log = LoggerFactory.getLogger(getClass());

    public CORSResponseFilter() {
        log.info("Registered");
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {

        final MultivaluedMap<String, Object> headers = responseContext.getHeaders();
        // if
        // (requestContext.getHeaderString("Host").endsWith("pokebattler.com"))
        // {
        //
        // if (requestContext.getHeaderString("Origin") == null) {
        // headers.putSingle("Access-Control-Allow-Origin",
        // (requestContext.getSecurityContext().isSecure()?"https":"http") +
        // "://www.pokebattler.com");
        // } else if
        // (requestContext.getHeaderString("Origin").endsWith("pokebattler.com"))
        // {
        // headers.putSingle("Access-Control-Allow-Origin",
        // requestContext.getHeaderString("Origin"));
        // headers.putSingle("Access-Control-Allow-Origin", "*");
        // }
        // } else {
        headers.putSingle("Access-Control-Allow-Origin", "*");
        // }

        // headers.add("Access-Control-Allow-Origin",
        // "http://podcastpedia.org"); //allows CORS requests only coming from
        // podcastpedia.org
        headers.putSingle("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
        headers.putSingle("Access-Control-Allow-Headers", "X-Requested-With");
        headers.putSingle("Allow", "OPTIONS");
    }

    @Override
    public Response toResponse(Exception ex) {
        if (ex instanceof javax.ws.rs.NotFoundException) {
            log.trace("404");
        } else {
            log.error("Unexpected error", ex);
        }
        return Response.serverError().header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .header("Access-Control-Allow-Headers", "X-Requested-With").header("Allow", "OPTIONS").build();
    }
}
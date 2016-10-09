package com.pokebattler.fight.jaxrs;

import java.io.IOException;
import java.util.Arrays;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

@CacheControl
public class CacheControlResponseFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        Arrays.asList(responseContext.getEntityAnnotations()).stream().filter(a -> a instanceof CacheControl)
                .map(a -> ((CacheControl) a).value()).forEach(value -> {
                    responseContext.getHeaders().add("Cache-Control", value);

                });

    }
}
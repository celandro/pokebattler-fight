package com.pokebattler.fight;

import org.glassfish.jersey.message.GZipEncoder;
import org.glassfish.jersey.message.filtering.EntityFilteringFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.EncodingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.leandronunes85.etag.responsefilter.ETagResponseFilter;
import com.pokebattler.fight.jaxrs.CORSResponseFilter;
import com.pokebattler.fight.jaxrs.CacheControlResponseFilter;
import com.pokebattler.fight.jaxrs.ProtobufProvider;

@Component
public class JerseyConfig extends ResourceConfig {
    private Logger log = LoggerFactory.getLogger(getClass());

    public JerseyConfig() {
        log.info("Registering classes");
        packages("com.pokebattler.fight.data");
        packages("com.pokebattler.fight.resources");
        register(ProtobufProvider.class);
        // need to be in order
        register(ETagResponseFilter.class);
        register(CORSResponseFilter.class);
        register(CacheControlResponseFilter.class);

        register(EntityFilteringFeature.class);
		EncodingFilter.enableFor(this, GZipEncoder.class);
    }

}
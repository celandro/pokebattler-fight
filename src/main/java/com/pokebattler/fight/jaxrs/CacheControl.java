package com.pokebattler.fight.jaxrs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.ws.rs.NameBinding;

@NameBinding
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(value = RetentionPolicy.RUNTIME)
public @interface CacheControl {
    String value() default "public, must-revalidate";
    public final static String CACHE_ONE_HOUR = "public, max-age=3600";

}

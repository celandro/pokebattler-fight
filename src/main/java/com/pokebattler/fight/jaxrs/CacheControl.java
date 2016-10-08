package com.pokebattler.fight.jaxrs;

import java.lang.annotation.*;
import javax.ws.rs.NameBinding;

@NameBinding
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(value = RetentionPolicy.RUNTIME)
public @interface CacheControl {
    String value() default "public, must-revalidate";
}

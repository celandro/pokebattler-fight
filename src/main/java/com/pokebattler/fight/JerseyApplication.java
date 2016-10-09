package com.pokebattler.fight;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

@SpringBootApplication
public class JerseyApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        new JerseyApplication().configure(new SpringApplicationBuilder(JerseyApplication.class)).run(args);
    }

}

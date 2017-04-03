# Spring Boot based Pokebattler.com Fight back end server

[Pokebattler](https://www.pokebattler.com) is powered by this open source server. Licensing is MIT which 
basically means you can do whatever you want with it.  I would of course appreciation attribution and links
to the site but its totally up to you.

This server is a spring-boot application with the jax-rs setup, see com.pokebattler.fight.resources for all supported urls.

## Run the application locally from the console
1. `mvn spring-boot:run`
1. `curl http://localhost:8080/moves` to make sure it works

## Run the application locally in Eclipse

1. Import this project directory under File->Import->Maven->Existing Maven project
1. Right click on project -> Run as->Maven generate sources
1.  src/main/java -> com.pokebattler.fight, Right click on JerseyApplication, run/debug as Java Application


## Deploy the application to google cloud

1. `mvn clean package gcloud:stage`
1. `cd target\appengine-staging`
1. `gcloud app deploy`
1. Visit `http://YOUR_PROJECT.appspot.com`.






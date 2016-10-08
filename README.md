# Spring Boot based Pokebattler.com Fight back end server

This is a standard jax-rs setup, see 

## Run the application locally in Eclipse

1. Import this project directory under File->Import->Maven->Existing Maven project
1. Right click on project -> Run as->Maven generate sources
1.  src/main/java -> com.pokebattler.fight, Right click on JerseyApplication, run/debug as Java Application



1. `mvn clean package gcloud:stage`
1. `cd target\appengine-staging`
1. `gcloud app deploy`
1. Visit `http://YOUR_PROJECT.appspot.com`.


Java is a registered trademark of Oracle Corporation and/or its affiliates.

"# pokebattler-fight" 

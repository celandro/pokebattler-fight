package com.pokebattler.fight.resources;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.leandronunes85.etag.ETag;
import com.pokebattler.fight.data.MoveRepository;
import com.pokebattler.fight.data.proto.MoveOuterClass.Move;
import com.pokebattler.fight.data.proto.MoveOuterClass.Moves;
import com.pokebattler.fight.jaxrs.CacheControl;

@Component
@Path("/moves")
public class MoveResource {

    @Resource
    MoveRepository repository;
    Logger log = LoggerFactory.getLogger(getClass());

    public MoveResource() {
        log.info("Registered");
    }

    @GET
    @Produces("application/json")
    @ETag
    @CacheControl("max-age=86000")
    public Moves getAll() throws Exception {
        return repository.getAll();
    }

    @GET
    @Path("ids")
    @Produces("application/json")
    @ETag
    @CacheControl("max-age=86000")
    public Map<String, Map<String, String>> getIds(@PathParam("pokemonId") int pokemonId) throws Exception {
        final Map<String, Map<String, String>> idMap = new TreeMap<>();
        idMap.put("ids", repository.getIdToNameMap());
        return idMap;
    }

    @GET
    @Path("ids/{moveId}")
    @Produces("application/json")
    @ETag
    @CacheControl("max-age=86000")
    public Move getById(@PathParam("moveId") int moveId) throws Exception {
        return repository.getByNumber(moveId);
    }

    @GET
    @Path("names")
    @Produces("application/json")
    @ETag
    @CacheControl("max-age=86000")
    public Map<String, Map<String, Integer>> getNames(@PathParam("pokemonId") int pokemonId) throws Exception {
        final Map<String, Map<String, Integer>> nameMap = new TreeMap<>();
        nameMap.put("name", repository.getNameToIdMap());
        return nameMap;
    }

    @GET
    @Path("names/{moveName}")
    @Produces("application/json")
    @ETag
    @CacheControl("max-age=86000")
    public Move getByName(@PathParam("moveName") String moveName) throws Exception {
        return repository.getByName(moveName);
    }

}

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

import com.google.protobuf.util.JsonFormat;
import com.leandronunes85.etag.ETag;
import com.pokebattler.fight.data.PokemonRepository;
import com.pokebattler.fight.data.proto.PokemonOuterClass.Pokemon;
import com.pokebattler.fight.data.proto.PokemonOuterClass.Pokemons;
import com.pokebattler.fight.jaxrs.CacheControl;

@Component
@Path("/pokemon")
public class PokemonResource {
    @Resource
    PokemonRepository repository;
    JsonFormat.Printer printer = JsonFormat.printer();
    Logger log = LoggerFactory.getLogger(getClass());

    public PokemonResource() {
        log.info("Registered");
    }

    @GET
    @Produces("application/json")
    @ETag
    @CacheControl("max-age=86000")
    public Pokemons getAll() throws Exception {
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
    @Path("ids/{pokemonId}")
    @Produces("application/json")
    @ETag
    @CacheControl("max-age=86000")
    public Pokemon getById(@PathParam("pokemonId") int pokemonId) throws Exception {
        return repository.getByNumber(pokemonId);
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
    @Path("names/{pokemonName}")
    @Produces("application/json")
    @ETag
    @CacheControl("max-age=86000")
    public Pokemon getByName(@PathParam("pokemonName") String pokemonName) throws Exception {
        return repository.getByName(pokemonName);
    }

}

package com.pokebattler.fight.resources;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.leandronunes85.etag.ETag;
import com.pokebattler.fight.data.ResistRepository;
import com.pokebattler.fight.data.proto.PokemonTypeOuterClass.PokemonType;
import com.pokebattler.fight.jaxrs.CacheControl;

@Component
@Path("/resists")
public class ResistResource {
    @Resource
    ResistRepository repository;
    Logger log = LoggerFactory.getLogger(getClass());

    public ResistResource() {
        log.info("Registered");
    }

    @GET
    @Produces("application/json")
    @ETag
    @CacheControl("max-age=86000")
    public Map<PokemonType, Map<PokemonType, Float>> getAll() throws Exception {
        return repository.getAll();
    }

    @GET
    @Path("attackers/{attackerType}")
    @Produces("application/json")
    @ETag
    @CacheControl("max-age=86000")
    public Map<String, Object> getByAttackerType(@PathParam("attackerType") PokemonType attackerType) throws Exception {
        final Map<String, Object> map = new HashMap<>();
        map.put("attackerType", attackerType);
        map.put("resists", repository.getResists(attackerType));
        return map;
    }

    @GET
    @Path("attackers/{attackerType}/defenders/{defenderType}")
    @Produces("application/json")
    @ETag
    @CacheControl("max-age=86000")
    public Map<String, Object> getByAttackerAndDefenderTypes(@PathParam("attackerType") PokemonType attackerType,
            @PathParam("defenderType") PokemonType defenderType) throws Exception {
        final Map<String, Object> map = new HashMap<>();
        map.put("attackerType", attackerType);
        map.put("defenderType", defenderType);
        map.put("resist", repository.getResist(attackerType, defenderType));
        return map;
    }

}

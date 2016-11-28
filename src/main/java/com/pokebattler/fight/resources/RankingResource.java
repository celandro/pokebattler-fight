package com.pokebattler.fight.resources;

import javax.annotation.Resource;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.leandronunes85.etag.ETag;
import com.pokebattler.fight.calculator.Formulas;
import com.pokebattler.fight.data.PokemonDataCreator;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.Ranking.FilterType;
import com.pokebattler.fight.data.proto.Ranking.SortType;
import com.pokebattler.fight.ranking.CachingRankingSimulator;

@Component
@Path("/rankings")
public class RankingResource {

    @Resource
    CachingRankingSimulator simulator;
    @Resource
    Formulas formulas;
    @Resource
    PokemonDataCreator creator;

    public static final String MAX_LEVEL = "40";
    public static final int MAX_INDIVIDUAL_STAT = 15;
    public Logger log = LoggerFactory.getLogger(getClass());

    @GET
    @Path("attackers/levels/{attackerLevel}/defenders/levels/{defenderLevel}/strategies/{attackStrategy}/{defenseStrategy}")
    @Produces("application/json")
    @ETag
    public Response rankAttacker(@PathParam("attackerLevel") String attackerLevel,
            @PathParam("defenderLevel") String defenderLevel,
            @PathParam("attackStrategy") AttackStrategyType attackStrategy,
            @PathParam("defenseStrategy") AttackStrategyType defenseStrategy,
            @DefaultValue("POWER") @QueryParam("sort") SortType sortType,
            @DefaultValue("NO_FILTER") @QueryParam("filterType") FilterType filterType,
            @QueryParam("filterValue") String filterValue) {
        log.debug("Calculating attacker rankings for attackerLevel {}, defenderLevel {}, attackStrategy {}, defenseStrategy {}, sortType {}", attackerLevel, defenderLevel, attackStrategy,
                defenseStrategy, sortType);
        // set caching based on wether the result is random
        // TODO: refactor this to strategy pattern or change to a parameter?
        // maybe a query parameter to seed the rng?
        final javax.ws.rs.core.CacheControl cacheControl = new javax.ws.rs.core.CacheControl();
        cacheControl.setMaxAge(isRandom(attackStrategy, defenseStrategy) ? 0 : 86000);
        return Response.ok(simulator.rankAttacker(attackerLevel, defenderLevel, attackStrategy, defenseStrategy, sortType, filterType, filterValue)).cacheControl(cacheControl).build();

    }
    @GET
    @Path("defenders/levels/{defenderLevel}/attackers/levels/{attackerLevel}/strategies/{defenseStrategy}/{attackStrategy}")
    @Produces("application/json")
    @ETag
    public Response rankDefender(@PathParam("attackerLevel") String attackerLevel,
            @PathParam("defenderLevel") String defenderLevel,
            @PathParam("attackStrategy") AttackStrategyType attackStrategy,
            @PathParam("defenseStrategy") AttackStrategyType defenseStrategy,
            @DefaultValue("POWER") @QueryParam("sort") SortType sortType,
            @DefaultValue("COUNTERS") @QueryParam("filterType") FilterType filterType,
            @DefaultValue("5") @QueryParam("filterValue") String filterValue) {
        log.debug("Calculating defender rankings for attackerLevel {}, defenderLevel {}, attackStrategy {}, defenseStrategy {}, sortType {}", attackerLevel, defenderLevel, attackStrategy,
                defenseStrategy, sortType);
        // set caching based on wether the result is random
        // TODO: refactor this to strategy pattern or change to a parameter?
        // maybe a query parameter to seed the rng?
        final javax.ws.rs.core.CacheControl cacheControl = new javax.ws.rs.core.CacheControl();
        cacheControl.setMaxAge(isRandom(attackStrategy, defenseStrategy) ? 0 : 86000);
        return Response.ok(simulator.rankDefender(attackerLevel, defenderLevel, attackStrategy, defenseStrategy, sortType,filterType, filterValue)).cacheControl(cacheControl).build();

    }
    

    private boolean isRandom(AttackStrategyType attackStrategy, AttackStrategyType defenseStrategy) {
        return defenseStrategy == AttackStrategyType.DEFENSE_RANDOM;

    }

}

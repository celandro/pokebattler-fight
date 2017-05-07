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
import com.pokebattler.fight.calculator.AttackSimulator;
import com.pokebattler.fight.calculator.Formulas;
import com.pokebattler.fight.data.PokemonDataCreator;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.FightOuterClass.DodgeStrategyType;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.MiniPokemonData;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;
import com.pokebattler.fight.data.proto.PokemonIdOuterClass.PokemonId;
import com.pokebattler.fight.data.proto.PokemonMoveOuterClass.PokemonMove;
import com.pokebattler.fight.data.proto.Ranking.FilterType;
import com.pokebattler.fight.data.proto.Ranking.SortType;
import com.pokebattler.fight.ranking.CPPokemonCreator;
import com.pokebattler.fight.ranking.CachingRankingSimulator;
import com.pokebattler.fight.ranking.ExactStatPokemonCreator;
import com.pokebattler.fight.resources.FightResource.IVWrapper;

@Component
@Path("/prestige")
public class PrestigingResource {

    @Resource
    CachingRankingSimulator simulator;
    @Resource
    Formulas formulas;
    @Resource
    PokemonDataCreator creator;

    public static final String MAX_LEVEL = "40";
    public static final int MAX_INDIVIDUAL_STAT = 15;
    public Logger log = LoggerFactory.getLogger(getClass());
    public static final int ONE_HOUR = 3600;

    @GET
    @Path("defenders/{defenderId}/levels/{defenderLevel}/ivs/{defenderIV}"
    		+ "/prestige/{prestigeTarget}/strategies/{attackStrategy}/{defenseStrategy}")
    @Produces("application/json")
    @ETag
    public Response prestigeByLevel( @PathParam("defenderId") PokemonId defenderId,
    		@PathParam("defenderLevel") String defenderLevel,
            @PathParam("defenderIV") IVWrapper defenderIV,
            @PathParam("prestigeTarget") int prestigeTarget,
            @PathParam("attackStrategy") AttackStrategyType attackStrategy,
            @PathParam("defenseStrategy") AttackStrategyType defenseStrategy,
            @DefaultValue("OVERALL") @QueryParam("sort") SortType sortType,
            @DefaultValue("DODGE_100") @QueryParam("dodgeStrategy") DodgeStrategyType dodgeStrategy,
            @QueryParam("seed") @DefaultValue("-1") long seed) {
    	if (seed == -1 && AttackSimulator.isRandom(attackStrategy, defenseStrategy, dodgeStrategy)) {
    		seed = (int) System.currentTimeMillis();
    	}
        log.debug("Calculating prestige rankings for defenderId {} defenderLevel {}, defenderIV()"
        		+ "attackStrategy {}, defenseStrategy {}, sortType {}", defenderId, defenderLevel, defenderIV, attackStrategy,
                defenseStrategy, sortType);
        // set caching based on wether the result is random
        // TODO: refactor this to strategy pattern or change to a parameter?
        // maybe a query parameter to seed the rng?
        final javax.ws.rs.core.CacheControl cacheControl = new javax.ws.rs.core.CacheControl();
        cacheControl.setMaxAge(isRandom(attackStrategy, defenseStrategy) ? 0 : ONE_HOUR);
        cacheControl.setPrivate(false);
        cacheControl.setNoTransform(false);
        ExactStatPokemonCreator attackerCreator = new ExactStatPokemonCreator(creator, defenderLevel, defenderIV.getAttack()
        		, defenderIV.getDefense(), defenderIV.getStamina());
        PokemonData fakeDefender = attackerCreator.createPokemon(defenderId, PokemonMove.SPLASH_FAST, PokemonMove.STRUGGLE);
        int attackerCp = formulas.getCPForPrestigeTarget(fakeDefender.getCp(), prestigeTarget);
        CPPokemonCreator defenderCreator = new CPPokemonCreator(creator, attackerCp);
        return Response.ok(simulator.rankDefender(attackStrategy, defenseStrategy, sortType, 
        		FilterType.PRESTIGE, defenderId.name(), defenderCreator, attackerCreator, dodgeStrategy, seed))
        		.cacheControl(cacheControl).build();
        

    }
    @GET
    @Path("defenders/{defenderId}/cp/{defenderCP}"
    		+ "/prestige/{prestigeTarget}/strategies/{attackStrategy}/{defenseStrategy}")
    @Produces("application/json")
    @ETag
    public Response prestigeByCp( @PathParam("defenderId") PokemonId defenderId,
    		@PathParam("defenderCP") int defenderCP,
            @PathParam("prestigeTarget") int prestigeTarget,
            @PathParam("attackStrategy") AttackStrategyType attackStrategy,
            @PathParam("defenseStrategy") AttackStrategyType defenseStrategy,
            @DefaultValue("OVERALL") @QueryParam("sort") SortType sortType,
    		@DefaultValue("DODGE_100") @QueryParam("dodgeStrategy") DodgeStrategyType dodgeStrategy,
            @QueryParam("seed") @DefaultValue("-1") long seed) {
    	if (seed == -1 && AttackSimulator.isRandom(attackStrategy, defenseStrategy, dodgeStrategy)) {
    		seed = (int) System.currentTimeMillis();
    	}
    		
        log.debug("Calculating prestige rankings for defenderId {} defenderCP {}"
        		+ "attackStrategy {}, defenseStrategy {}, sortType {}", defenderId, defenderCP, attackStrategy,
                defenseStrategy, sortType);
        MiniPokemonData data = creator.findPokemonStats(defenderId,  defenderCP);
        IVWrapper defenderIV = new IVWrapper(data.getAttack(), data.getDefense(), data.getStamina());
        return prestigeByLevel(defenderId, data.getLevel(), defenderIV, prestigeTarget, attackStrategy, defenseStrategy, sortType, dodgeStrategy, seed);
        

    }
    
    

    private boolean isRandom(AttackStrategyType attackStrategy, AttackStrategyType defenseStrategy) {
        return defenseStrategy == AttackStrategyType.DEFENSE_RANDOM;

    }

}

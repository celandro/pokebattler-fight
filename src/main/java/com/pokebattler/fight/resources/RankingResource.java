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
import com.pokebattler.fight.data.proto.Ranking.FilterType;
import com.pokebattler.fight.data.proto.Ranking.SortType;
import com.pokebattler.fight.jaxrs.ProtobufBinaryProvider;
import com.pokebattler.fight.ranking.CachingRankingSimulator;
import com.pokebattler.fight.ranking.CloudStorageRankingSimulator;
import com.pokebattler.fight.ranking.ExactStatPokemonCreator;

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
	public static final int CACHE_TIME = 86400;
	@GET
	@Path("attackers/levels/{attackerLevel}/defenders/levels/{defenderLevel}/strategies/{attackStrategy}/{defenseStrategy}/{sort}-{dodgeStrategy}-{filterType}-{filterValue}.bin")
	@Produces(ProtobufBinaryProvider.TYPE_APPLICATION_X_PROTOBUF)
	@ETag
	public Response rankAttackerBin(@PathParam("attackerLevel") String attackerLevel,
			@PathParam("defenderLevel") String defenderLevel,
			@PathParam("attackStrategy") AttackStrategyType attackStrategy,
			@PathParam("defenseStrategy") AttackStrategyType defenseStrategy,
			@PathParam("sort") SortType sortType,
			@PathParam("filterType") FilterType filterType,
			@PathParam("filterValue") String filterValue,
            @PathParam("dodgeStrategy") DodgeStrategyType dodgeStrategy,
            @QueryParam("seed") @DefaultValue("-1") long seed) {

		return rankAttacker(attackerLevel, defenderLevel, attackStrategy, defenseStrategy, sortType, filterType, filterValue, dodgeStrategy, seed);
	}

	@GET
	@Path("attackers/levels/{attackerLevel}/defenders/levels/{defenderLevel}/strategies/{attackStrategy}/{defenseStrategy}")
	@Produces("application/json")
	@ETag
	public Response rankAttacker(@PathParam("attackerLevel") String attackerLevel,
			@PathParam("defenderLevel") String defenderLevel,
			@PathParam("attackStrategy") AttackStrategyType attackStrategy,
			@PathParam("defenseStrategy") AttackStrategyType defenseStrategy,
			@DefaultValue("OVERALL") @QueryParam("sort") SortType sortType,
			@DefaultValue("NO_FILTER") @QueryParam("filterType") FilterType filterType,
			@DefaultValue("NONE") @QueryParam("filterValue") String filterValue,
            @DefaultValue("DODGE_100") @QueryParam("dodgeStrategy") DodgeStrategyType dodgeStrategy,
            @QueryParam("seed") @DefaultValue("1") long seed) {
    	if (seed == -1 && AttackSimulator.isRandom(attackStrategy, defenseStrategy, dodgeStrategy)) {
    		seed = System.currentTimeMillis();
    	}

		log.debug(
				"Calculating attacker rankings for attackerLevel {}, defenderLevel {}, attackStrategy {}, defenseStrategy {}, sortType {}",
				attackerLevel, defenderLevel, attackStrategy, defenseStrategy, sortType);
		// Always cache since we pregenerate
		final javax.ws.rs.core.CacheControl cacheControl = new javax.ws.rs.core.CacheControl();
		cacheControl.setMaxAge(CACHE_TIME);
		cacheControl.setPrivate(false);
		cacheControl.setNoTransform(false);
		return Response.ok(simulator.rankAttacker(attackStrategy, defenseStrategy, sortType, filterType, filterValue,
				new ExactStatPokemonCreator(creator, attackerLevel), new ExactStatPokemonCreator(creator, defenderLevel), dodgeStrategy, seed))
				.cacheControl(cacheControl).build();

	}
	@GET
	@Path("defenders/levels/{defenderLevel}/attackers/levels/{attackerLevel}/strategies/{defenseStrategy}/{attackStrategy}/{sort}-{dodgeStrategy}-{filterType}-{filterValue}.bin")
	@Produces(ProtobufBinaryProvider.TYPE_APPLICATION_X_PROTOBUF)
	@ETag
	public Response rankDefenderBin(@PathParam("attackerLevel") String attackerLevel,
			@PathParam("defenderLevel") String defenderLevel,
			@PathParam("attackStrategy") AttackStrategyType attackStrategy,
			@PathParam("defenseStrategy") AttackStrategyType defenseStrategy,
			@PathParam("sort") SortType sortType,
			@PathParam("filterType") FilterType filterType,
			@PathParam("filterValue") String filterValue,
            @PathParam("dodgeStrategy") DodgeStrategyType dodgeStrategy,
            @QueryParam("seed") @DefaultValue("-1") long seed) {

		return rankDefender(attackerLevel, defenderLevel, attackStrategy, defenseStrategy, sortType, filterType, filterValue, dodgeStrategy, seed);
	}
	@GET
	@Path("defenders/levels/{defenderLevel}/attackers/levels/{attackerLevel}/strategies/{defenseStrategy}/{attackStrategy}")
	@Produces("application/json")
	@ETag
	public Response rankDefender(@PathParam("attackerLevel") String attackerLevel,
			@PathParam("defenderLevel") String defenderLevel,
			@PathParam("attackStrategy") AttackStrategyType attackStrategy,
			@PathParam("defenseStrategy") AttackStrategyType defenseStrategy,
			@DefaultValue("OVERALL") @QueryParam("sort") SortType sortType,
			@DefaultValue("COUNTERS") @QueryParam("filterType") FilterType filterType,
			@DefaultValue("5") @QueryParam("filterValue") String filterValue,
            @DefaultValue("DODGE_100") @QueryParam("dodgeStrategy") DodgeStrategyType dodgeStrategy,
            @QueryParam("seed") @DefaultValue("1") long seed) {
    	if (seed == -1 && AttackSimulator.isRandom(attackStrategy, defenseStrategy, dodgeStrategy)) {
    		seed = System.currentTimeMillis();
    	}

		log.debug(
				"Calculating defender rankings for attackerLevel {}, defenderLevel {}, attackStrategy {}, defenseStrategy {}, sortType {}",
				attackerLevel, defenderLevel, attackStrategy, defenseStrategy, sortType);
		// always cache since we pregenerate now
		final javax.ws.rs.core.CacheControl cacheControl = new javax.ws.rs.core.CacheControl();
		cacheControl.setMaxAge(CACHE_TIME);
		cacheControl.setPrivate(false);
		cacheControl.setNoTransform(false);
		return Response.ok(simulator.rankDefender(attackStrategy, defenseStrategy, sortType, filterType, filterValue,
				new ExactStatPokemonCreator(creator, attackerLevel), new ExactStatPokemonCreator(creator, defenderLevel)
				,dodgeStrategy, seed)).cacheControl(cacheControl).build();

	}

	private boolean isRandom(AttackStrategyType attackStrategy, AttackStrategyType defenseStrategy) {
		return defenseStrategy == AttackStrategyType.DEFENSE_RANDOM;

	}

}

package com.pokebattler.fight.calculator;

import java.util.Random;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.pokebattler.fight.data.PokemonDataCreator;
import com.pokebattler.fight.data.PokemonRepository;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.FightOuterClass.Fight;
import com.pokebattler.fight.data.proto.FightOuterClass.FightResult;


@Service("DelegatingSimulator")
public class DelegatingSimulator implements AttackSimulator {
    private final PokemonDataCreator creator;
    private final PokemonRepository pokemonRepository;
    private final IndividualSimulator simulator;
    private final MonteCarloSimulator mcSimulator;
    
    private final LoadingCache<Fight, FightResult> rankCache;
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    @Autowired
    public DelegatingSimulator(PokemonDataCreator creator, PokemonRepository pokemonRepository,
    		IndividualSimulator simulator,  MonteCarloSimulator mcSimulator, @Value("${FIGHT_CACHE_SPEC}") String fightCacheSpec) {
    	this.creator = creator;
    	this.pokemonRepository = pokemonRepository;
    	this.simulator = simulator;
    	this.mcSimulator = mcSimulator;
		// big options cache
		this.rankCache = CacheBuilder.from(fightCacheSpec)
				.build(new CacheLoader<Fight, FightResult>() {
					public FightResult load(Fight fight) {
						if (fight.getDefenseStrategy() == AttackStrategyType.DEFENSE_RANDOM_MC ||
								fight.getStrategy() == AttackStrategyType.DEFENSE_RANDOM_MC)
						{
							return mcSimulator.fight(fight);
						}
						return simulator.fight(fight);
					}
				});
    }
    
	@Override
	public FightResult fight(Fight fight) {
		try {
			return rankCache.get(fight);
		} catch (ExecutionException e) {
			throw new RuntimeException("Could not run simulation?", e);
		}
	}

	@Override
	public PokemonRepository getPokemonRepository() {
		return pokemonRepository;
	}

	@Override
	public PokemonDataCreator getCreator() {
		return creator;
	}

	@Override
	public FightResult fight(Fight fight, Random r) {
		log.warn("Should not call DelegatingSimulator with a random value, ignoring");
		return fight(fight);
	}

}

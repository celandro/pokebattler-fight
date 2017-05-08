package com.pokebattler.fight.calculator;

import java.util.ArrayList;
import java.util.Random;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pokebattler.fight.data.PokemonDataCreator;
import com.pokebattler.fight.data.PokemonRepository;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.FightOuterClass.CombatantResult;
import com.pokebattler.fight.data.proto.FightOuterClass.Fight;
import com.pokebattler.fight.data.proto.FightOuterClass.FightResult;
import com.pokebattler.fight.data.proto.FightOuterClass.FightResult.Builder;
import com.pokebattler.fight.ranking.sort.OverallRankingsSort;

@Service("MonteCarloSimulator")
public class MonteCarloSimulator implements AttackSimulator {
    @Resource
    private PokemonDataCreator creator;
    @Resource
    private PokemonRepository pokemonRepository;
    @Resource
    private IndividualSimulator simulator;
    @Value("${monteCarlo.numRounds}")
    private int numRounds;
    @Resource
    OverallRankingsSort sort;
    private final Logger log = LoggerFactory.getLogger(getClass());
  

	@Override
	public FightResult fight(Fight fight) {
		ArrayList<FightResult> results = new ArrayList<>(numRounds);
		Random r = new Random(fight.getSeed());
		for (int i=0; i< numRounds; i++) {
			results.add(simulator.fight(fight, r));
		}
		Builder result = FightResult.newBuilder();
		// sort the results
		results.sort(sort.getFightResultComparator());
		FightResult median = results.get((numRounds+1)/2);

		result.setFightParameters(median.getFightParameters())
				.setWin(median.getWin())
				.setPrestige(median.getPrestige());
		if (fight.getIncludeDetails()) {
			median.getCombatResultList().stream().forEach(builder -> result.addCombatResult(builder));
		}
		result.addAllCombatants(median.getCombatantsList());
		
		// update the overall results based on mean
		results.stream().forEach(fightResult -> {
			result.setEffectiveCombatTime(result.getEffectiveCombatTime() + fightResult.getEffectiveCombatTime());
			result.setTotalCombatTime(result.getTotalCombatTime() + fightResult.getTotalCombatTime());
			result.setPowerLog(result.getPowerLog() + fightResult.getPowerLog());
			result.setPotions(result.getPotions() + fightResult.getPotions());
			result.setOverallRating(result.getOverallRating() + Math.log10(fightResult.getOverallRating()));
			result.setNumSims(result.getNumSims() + fightResult.getNumSims());
		});
		result.setEffectiveCombatTime(result.getEffectiveCombatTime() / numRounds);
		result.setTotalCombatTime(result.getTotalCombatTime() / numRounds);
		result.setPowerLog(result.getPowerLog() / numRounds);
		result.setPower(Math.pow(10.0,result.getPowerLog()));
		result.setOverallRating(Math.pow(10.0,(result.getOverallRating()/numRounds)));
		result.setPotions(result.getPotions() / numRounds);
		return result.build();
	}
	@Override
	public FightResult fight(Fight fight, Random r) {
		log.warn("Should not call MonteCarloSimulator with a random value, ignoring");
		return fight(fight);
	}

	@Override
	public PokemonRepository getPokemonRepository() {
		return pokemonRepository;
	}

	@Override
	public PokemonDataCreator getCreator() {
		return creator;
	}

}

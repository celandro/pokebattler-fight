package com.pokebattler.fight.calculator;

import java.util.ArrayList;

import javax.annotation.Resource;

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

@Service
public class MonteCarloSimulator implements AttackSimulator {
    @Resource
    private PokemonDataCreator creator;
    @Resource
    private PokemonRepository pokemonRepository;
    @Resource
    private IndividualSimulator simulator;
    @Value("${monteCarlo.numRounds}")
    private int numRounds=999;
    @Resource
    OverallRankingsSort sort;
    

	@Override
	public Builder fight(Fight fight, boolean includeDetails) {
		ArrayList<Builder> results = new ArrayList<>(numRounds);
		for (int i=0; i< numRounds; i++) {
			results.add(simulator.fight(fight, includeDetails));
		}
		Builder result = FightResult.newBuilder();
		if (includeDetails) {
			// sort the results
			results.sort(sort.getFightResultComparator());
		}
		Builder median = results.get((numRounds+1)/2);

		result.setFightParameters(median.getFightParametersBuilder())
				.setWin(median.getWin())
				.setPrestige(median.getPrestige());
		if (includeDetails) {
			median.getCombatResultBuilderList().stream().forEach(builder -> result.addCombatResult(builder));
		}
		median.getCombatantsBuilderList().stream().forEach(combatant -> {
			CombatantResult.Builder builder = CombatantResult.newBuilder().setStrategy(combatant.getStrategy())
					.setStartHp(combatant.getStartHp()).setCombatant(combatant.getCombatant()).setId(combatant.getId())
					.setCp(combatant.getCp()).setDodgeStrategy(combatant.getDodgeStrategy()).setPokemon(combatant.getPokemon());
			result.addCombatants(builder);
		});
		
		results.stream().forEach(fightResult -> {
			result.setEffectiveCombatTime(result.getEffectiveCombatTime() + fightResult.getEffectiveCombatTime());
			result.setTotalCombatTime(result.getTotalCombatTime() + fightResult.getTotalCombatTime());
			result.setPowerLog(result.getPowerLog() + fightResult.getPowerLog());
			result.setPotions(result.getPotions() + fightResult.getPotions());
			result.setOverallRating(result.getOverallRating() + Math.log10(fightResult.getOverallRating()));
			for (int i=0; i< fightResult.getCombatantsBuilderList().size(); i++) {
				CombatantResult.Builder combatant = fightResult.getCombatantsBuilderList().get(i);
				CombatantResult.Builder resultCombatant = result.getCombatantsBuilderList().get(i);
				resultCombatant.setCombatTime(resultCombatant.getCombatTime() + combatant.getCombatTime());
				resultCombatant.setDamageDealt(resultCombatant.getDamageDealt() + combatant.getDamageDealt());
				// store damage here
				resultCombatant.setEndHp(resultCombatant.getEndHp() + (combatant.getStartHp() - combatant.getEndHp()));
				resultCombatant.setEnergy(resultCombatant.getEnergy() + combatant.getEnergy());
			}
		});
		result.setEffectiveCombatTime(result.getEffectiveCombatTime() / numRounds);
		result.setTotalCombatTime(result.getTotalCombatTime() / numRounds);
		result.setPowerLog(result.getPowerLog() / numRounds);
		result.setPower(Math.pow(10.0,result.getPowerLog()));
		result.setOverallRating(Math.pow(10.0,(result.getOverallRating()/numRounds)));
		result.setPotions(result.getPotions() / numRounds);
		for (int i=0; i< result.getCombatantsBuilderList().size(); i++) {
			CombatantResult.Builder resultCombatant = result.getCombatantsBuilderList().get(i);
			resultCombatant.setCombatTime(resultCombatant.getCombatTime() / numRounds);
			resultCombatant.setDamageDealt(Math.round(resultCombatant.getDamageDealt() / numRounds));
			resultCombatant.setEnergy(resultCombatant.getEnergy() / numRounds);
			resultCombatant.setDps(1000.0f * resultCombatant.getDamageDealt() / (float) resultCombatant.getCombatTime());
			resultCombatant.setEndHp(resultCombatant.getStartHp() - (resultCombatant.getEndHp() / numRounds));

		}			
		return result;
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

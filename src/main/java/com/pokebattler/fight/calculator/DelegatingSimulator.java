package com.pokebattler.fight.calculator;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.pokebattler.fight.data.PokemonDataCreator;
import com.pokebattler.fight.data.PokemonRepository;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.FightOuterClass.Fight;
import com.pokebattler.fight.data.proto.FightOuterClass.FightResult.Builder;


@Service
public class DelegatingSimulator implements AttackSimulator {
    @Resource
    private PokemonDataCreator creator;
    @Resource
    private PokemonRepository pokemonRepository;
    @Resource
    private IndividualSimulator simulator;
    @Resource
    private MonteCarloSimulator mcSimulator;

	@Override
	public Builder fight(Fight fight, boolean includeDetails) {
		if (fight.getDefenseStrategy() == AttackStrategyType.DEFENSE_RANDOM_MC) {
			return mcSimulator.fight(fight, includeDetails);
		}
		return simulator.fight(fight, includeDetails);
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

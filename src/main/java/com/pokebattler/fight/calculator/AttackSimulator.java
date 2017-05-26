package com.pokebattler.fight.calculator;

import java.util.Random;

import com.pokebattler.fight.data.PokemonDataCreator;
import com.pokebattler.fight.data.PokemonRepository;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.FightOuterClass.DodgeStrategyType;
import com.pokebattler.fight.data.proto.FightOuterClass.Fight;
import com.pokebattler.fight.data.proto.FightOuterClass.FightResult;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;
import com.pokebattler.fight.data.proto.PokemonIdOuterClass.PokemonId;
import com.pokebattler.fight.data.proto.PokemonMoveOuterClass.PokemonMove;
import com.pokebattler.fight.data.proto.PokemonOuterClass.Pokemon;

public interface AttackSimulator {

	FightResult fight(Fight fight, Random r);

	default FightResult fight(Fight fight) {
		return fight(fight, (fight.getSeed() == -1)?null:new Random(fight.getSeed()));
	}

	PokemonRepository getPokemonRepository();

	PokemonDataCreator getCreator();

	default public FightResult calculateMaxAttackDPS(PokemonId attackerId, PokemonId defenderId, PokemonMove move1,
			PokemonMove move2, AttackStrategyType strategy, boolean includeDetails) {
		final String level = Integer.toString(Formulas.MAX_LEVEL);
		return calculateMaxAttackDPS(attackerId, defenderId, move1, move2, strategy, level, level, includeDetails);

	}

	default public FightResult calculateMaxAttackDPS(PokemonId attackerId, PokemonId defenderId, PokemonMove move1,
			PokemonMove move2, AttackStrategyType strategy, String attackerLevel, String defenderLevel,
			boolean includeDetails) {
		final PokemonData attacker = getCreator().createMaxStatPokemon(attackerId, attackerLevel, move1, move2);
		final Pokemon d = getPokemonRepository().getById(defenderId);
		final PokemonData defender = getCreator().createMaxStatPokemon(defenderId, defenderLevel, d.getQuickMoves(0),
				d.getCinematicMoves(0));
		return calculateAttackDPS(attacker, defender, strategy, includeDetails);
	}

	default public FightResult calculateAttackDPS(PokemonData attacker, PokemonData defender,
			AttackStrategyType attackerStrategy, boolean includeDetails) {
		return calculateAttackDPS(attacker, defender, attackerStrategy, AttackStrategyType.DEFENSE,
				DodgeStrategyType.DODGE_100, includeDetails, 0);
	}

	default public FightResult calculateAttackDPS(PokemonData attacker, PokemonData defender,
			AttackStrategyType attackerStrategy, AttackStrategyType defenseStrategy, DodgeStrategyType dodgeStrategy,
			boolean includeDetails, long seed) {
		return fight(Fight.newBuilder().setAttacker1(attacker).setDefender(defender).setStrategy(attackerStrategy)
				.setDefenseStrategy(defenseStrategy).setDodgeStrategy(dodgeStrategy).setIncludeDetails(includeDetails)
				.setSeed(seed).build());
	}

	public static boolean isRandom(AttackStrategyType attackStrategy, AttackStrategyType defenseStrategy,
			DodgeStrategyType dodgeStrategy) {
		return attackStrategy == AttackStrategyType.DEFENSE_RANDOM
				|| attackStrategy == AttackStrategyType.DEFENSE_RANDOM_MC
				|| defenseStrategy == AttackStrategyType.DEFENSE_RANDOM
				|| defenseStrategy == AttackStrategyType.DEFENSE_RANDOM_MC;

	}

}
package com.pokebattler.fight.calculator;

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

	FightResult.Builder fight(Fight fight, boolean includeDetails);

	PokemonRepository getPokemonRepository();

	PokemonDataCreator getCreator();

    default public FightResult calculateMaxAttackDPS(PokemonId attackerId, PokemonId defenderId, PokemonMove move1,
            PokemonMove move2, AttackStrategyType strategy) {
        final String level = Integer.toString(Formulas.MAX_LEVEL);
        return calculateMaxAttackDPS(attackerId, defenderId, move1, move2, strategy, level, level);

    }

    default public FightResult calculateMaxAttackDPS(PokemonId attackerId, PokemonId defenderId, PokemonMove move1,
            PokemonMove move2, AttackStrategyType strategy, String attackerLevel, String defenderLevel) {
        final PokemonData attacker = getCreator().createMaxStatPokemon(attackerId, attackerLevel, move1, move2);
        final Pokemon d = getPokemonRepository().getById(defenderId);
        final PokemonData defender = getCreator().createMaxStatPokemon(defenderId, defenderLevel, d.getQuickMoves(0),
                d.getCinematicMoves(0));
        return calculateAttackDPS(attacker, defender, strategy);
    }

    default public FightResult calculateAttackDPS(PokemonData attacker, PokemonData defender,
            AttackStrategyType attackerStrategy) {
        return calculateAttackDPS(attacker, defender, attackerStrategy, AttackStrategyType.DEFENSE, DodgeStrategyType.DODGE_100);
    }

    default public FightResult calculateAttackDPS(PokemonData attacker, PokemonData defender,
            AttackStrategyType attackerStrategy, AttackStrategyType defenseStrategy, DodgeStrategyType dodgeStrategy) {
        return fight(Fight.newBuilder().setAttacker1(attacker).setDefender(defender).setStrategy(attackerStrategy)
                .setDefenseStrategy(defenseStrategy).setDodgeStrategy(dodgeStrategy).build(), true).build();
    }

}
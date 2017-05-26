package com.pokebattler.fight.strategies;

import java.util.Random;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pokebattler.fight.calculator.AttackDamage;
import com.pokebattler.fight.calculator.CombatantState;
import com.pokebattler.fight.calculator.Formulas;
import com.pokebattler.fight.calculator.dodge.DodgeStrategy;
import com.pokebattler.fight.data.MoveRepository;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.MoveOuterClass.Move;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;

public class DodgeAll implements AttackStrategy {
	private final PokemonData pokemon;
	private final int extraDelay;
	private final Move move1;
	private final Move move2;
	private final DodgeStrategy dodgeStrategy;
	private final AttackDamage move1Damage;
	private final AttackDamage move2Damage;
	private static final Logger log = LoggerFactory.getLogger(DodgeAll.class);

	@Override
	public AttackStrategyType getType() {
		return AttackStrategyType.DODGE_ALL;
	}

	public DodgeAll(PokemonData pokemon, Move move1, Move move2, int extraDelay, DodgeStrategy dodgeStrategy, AttackDamage move1Damage, AttackDamage move2Damage) {
		this.pokemon = pokemon;
		this.extraDelay = extraDelay;
		this.move1 = move1;
		this.move2 = move2;
		this.dodgeStrategy = dodgeStrategy;
		this.move1Damage = move1Damage;
		this.move2Damage = move2Damage;
	}

	@Override
	public PokemonAttack nextAttack(CombatantState attackerState, CombatantState defenderState) {
		// dodge special if we can
		if (defenderState.getNextMove() != null && defenderState.getTimeToNextDamage() > 0
				&& !defenderState.isDodged()) {
			if (defenderState.getTimeToNextDamage() <= Formulas.DODGE_WINDOW + extraDelay) {
				if (dodgeStrategy.tryToDodge(attackerState, defenderState)) {
					return getDodge(extraDelay, dodgeStrategy.chanceToDodge(attackerState, defenderState));
				}
			} else if (defenderState.getTimeToNextDamage() > move2.getDurationMs() + extraDelay + CAST_TIME
					&& attackerState.getCurrentEnergy() >= -1 * move2.getEnergyDelta()) {
				// we can sneak in a special attack
				return getMove2Attack(extraDelay + CAST_TIME);
			} else if (defenderState.getTimeToNextDamage() > move1.getDurationMs() + extraDelay) {
				// we can sneak in a normal attack
				return getMove1Attack(extraDelay);
			} else {
				// dodge perfect
				if (dodgeStrategy.tryToDodge(attackerState, defenderState)) {
					return getDodge(Math.max(0, defenderState.getTimeToNextDamage() - Formulas.DODGE_WINDOW),
							dodgeStrategy.chanceToDodge(attackerState, defenderState));

				}
			}
		}
		if (attackerState.getCurrentEnergy() >= -1 * move2.getEnergyDelta()) {
			return getMove2Attack(extraDelay + CAST_TIME);
		} else {
			return getMove1Attack(extraDelay);
		}

	}

	public int getDelay() {
		return getDelay();
	}

	@Component
	public static class DodgeAllBuilder implements AttackStrategy.AttackStrategyBuilder<DodgeAll> {
		@Resource
		private MoveRepository move;

		@Override
		public DodgeAll build(PokemonData pokemon, DodgeStrategy dodgeStrategy, AttackDamage move1Damage, AttackDamage move2Damage, Random r) {
			return new DodgeAll(pokemon, move.getById(pokemon.getMove1()), move.getById(pokemon.getMove2()), 0,
					dodgeStrategy,  move1Damage,  move2Damage);
		}
	}

	public PokemonData getPokemon() {
		return pokemon;
	}

	public int getExtraDelay() {
		return extraDelay;
	}

	public Move getMove1() {
		return move1;
	}

	public Move getMove2() {
		return move2;
	}

	public DodgeStrategy getDodgeStrategy() {
		return dodgeStrategy;
	}

	public AttackDamage getMove1Damage() {
		return move1Damage;
	}

	public AttackDamage getMove2Damage() {
		return move2Damage;
	}

}

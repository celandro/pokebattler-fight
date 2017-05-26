package com.pokebattler.fight.strategies;

import java.util.Random;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.calculator.AttackDamage;
import com.pokebattler.fight.calculator.CombatantState;
import com.pokebattler.fight.calculator.Formulas;
import com.pokebattler.fight.calculator.dodge.DodgeStrategy;
import com.pokebattler.fight.data.MoveRepository;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.MoveOuterClass.Move;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;

public class QuickAttackDodgeAll implements AttackStrategy {
	private final PokemonData pokemon;
	private final int extraDelay;
	private final Move move1;
	private final Move move2;
	private final DodgeStrategy dodgeStrategy;
	private final AttackDamage move1Damage;
	private final AttackDamage move2Damage;

	@Override
	public AttackStrategyType getType() {
		return AttackStrategyType.DODGE_ALL3;
	}

	public QuickAttackDodgeAll(PokemonData pokemon, Move move1, int extraDelay, DodgeStrategy dodgeStrategy, AttackDamage move1Damage, AttackDamage move2Damage) {
		this.pokemon = pokemon;
		this.extraDelay = extraDelay;
		this.move1 = move1;
		this.move2 = null;
		this.dodgeStrategy = dodgeStrategy;
		this.move1Damage = move1Damage;
		this.move2Damage = move2Damage;
	}

	@Override
	public PokemonAttack nextAttack(CombatantState attackerState, CombatantState defenderState) {
		// dodge special if we can
		if (defenderState.getNextMove() != null && defenderState.getTimeToNextDamage() > 0
				&& !defenderState.isDodged()) {

			if (defenderState.getTimeToNextDamage() < Formulas.DODGE_WINDOW + extraDelay) {
				if (dodgeStrategy.tryToDodge(attackerState, defenderState)) {
			        return getDodge(extraDelay, dodgeStrategy.chanceToDodge(attackerState, defenderState));
				}
			} else if (defenderState.getTimeToNextDamage() > move1.getDurationMs() + extraDelay) {
				// we can sneak in a normal attack
		        return getMove1Attack(extraDelay);
			} else {
				if (dodgeStrategy.tryToDodge(attackerState, defenderState)) {
					// dodge perfect
			        return getDodge(Math.max(0, defenderState.getTimeToNextDamage() - Formulas.DODGE_WINDOW),
			        		dodgeStrategy.chanceToDodge(attackerState, defenderState));
					
				}
			}
			// fall through
		}
        return getMove1Attack(extraDelay);

	}

	public int getDelay() {
		return getDelay();
	}

	@Component
	public static class QuickAttackDodgeAllBuilder
			implements AttackStrategy.AttackStrategyBuilder<QuickAttackDodgeAll> {
		@Resource
		private MoveRepository move;

		@Override
		public QuickAttackDodgeAll build(PokemonData pokemon, DodgeStrategy dodgeStrategy, AttackDamage move1Damage, AttackDamage move2Damage, Random r) {
			return new QuickAttackDodgeAll(pokemon, move.getById(pokemon.getMove1()), 0, dodgeStrategy, move1Damage, move2Damage);
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

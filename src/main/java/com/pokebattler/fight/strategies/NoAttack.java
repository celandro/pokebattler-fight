package com.pokebattler.fight.strategies;

import static com.pokebattler.fight.data.MoveRepository.DODGE_MOVE;

import java.util.Random;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.calculator.AttackDamage;
import com.pokebattler.fight.calculator.CombatantState;
import com.pokebattler.fight.calculator.Formulas;
import com.pokebattler.fight.calculator.dodge.DodgeStrategy;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.MoveOuterClass.Move;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;
import com.pokebattler.fight.data.proto.PokemonMoveOuterClass.PokemonMove;
import com.pokebattler.fight.strategies.AttackStrategy.PokemonAttack;

public class NoAttack implements AttackStrategy {
    PokemonData pokemon;
    int extraDelay;

    @Override
    public AttackStrategyType getType() {
        return AttackStrategyType.NONE;
    }

    public NoAttack(PokemonData pokemon, int extraDelay) {
        this.pokemon = pokemon;
        this.extraDelay = extraDelay;
    }

    @Override
    public PokemonAttack nextAttack(CombatantState attackerState, CombatantState defenderState) {
        if (defenderState.getNextMove() != null && defenderState.getTimeToNextDamage() > 0
                && !defenderState.isDodged()) {
                // dodge perfect
            return getDodge( Math.max(0, defenderState.getTimeToNextDamage() - Formulas.DODGE_WINDOW), 1000);
        }        
        return getDodge(extraDelay, 1000);

    }

    public int getDelay() {
        return getDelay();
    }

    @Component
    public static class NoAttackBuilder implements AttackStrategy.AttackStrategyBuilder<NoAttack> {
        @Override
        public NoAttack build(PokemonData pokemon, DodgeStrategy dodgeStrategy, AttackDamage move1Damage, AttackDamage move2Damage, Random r) {
        	// ignore dodgeStrategy
            return new NoAttack(pokemon, DODGE_COOLDOWN);
        }
    }

	@Override
	public AttackDamage getMove1Damage() {
		return null;
	}

	@Override
	public AttackDamage getMove2Damage() {
		return null;
	}


}

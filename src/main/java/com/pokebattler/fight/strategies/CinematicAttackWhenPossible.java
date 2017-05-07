package com.pokebattler.fight.strategies;

import java.util.Random;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.calculator.AttackDamage;
import com.pokebattler.fight.calculator.CombatantState;
import com.pokebattler.fight.calculator.dodge.DodgeStrategy;
import com.pokebattler.fight.data.MoveRepository;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.MoveOuterClass.Move;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;

public class CinematicAttackWhenPossible implements AttackStrategy {
    private PokemonData pokemon;
    private int extraDelay;
    private Move move1;
    private Move move2;
    private AttackDamage move1Damage;
    private AttackDamage move2Damage;
    @Override
    public AttackStrategyType getType() {
        return AttackStrategyType.CINEMATIC_ATTACK_WHEN_POSSIBLE;
    }

    public CinematicAttackWhenPossible(PokemonData pokemon, Move move1, Move move2, int extraDelay, AttackDamage move1Damage, AttackDamage move2Damage) {
        this.pokemon = pokemon;
        this.extraDelay = extraDelay;
        this.move1 = move1;
        this.move2 = move2;
        this.move1Damage = move1Damage;
        this.move2Damage = move2Damage;
    }

    @Override
    public PokemonAttack nextAttack(CombatantState attackerState, CombatantState defenderState) {
        if (attackerState.getCurrentEnergy() >= -1 * move2.getEnergyDelta()) {
            return getMove1Attack(extraDelay + CAST_TIME);
        } else {
            return getMove2Attack(extraDelay);
        }

    }

    public int getDelay() {
        return getDelay();
    }

    @Component
    public static class CinematicAttackWhenPossibleBuilder
            implements AttackStrategy.AttackStrategyBuilder<CinematicAttackWhenPossible> {
        @Resource
        private MoveRepository move;

        @Override
        public CinematicAttackWhenPossible build(PokemonData pokemon, DodgeStrategy dodgeStrategy, AttackDamage move1Damage, AttackDamage move2Damage, Random r) {
        	// ignore dodgeStrategy
            return new CinematicAttackWhenPossible(pokemon, move.getById(pokemon.getMove1()),
                    move.getById(pokemon.getMove2()), 0,  move1Damage, move2Damage);
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

	public AttackDamage getMove1Damage() {
		return move1Damage;
	}

	public AttackDamage getMove2Damage() {
		return move2Damage;
	}

}

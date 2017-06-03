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

public class DefenderAttack implements AttackStrategy {
    private int extraDelay;
    private PokemonData pokemon;
    private Move move1;
    private Move move2;
    private int nextSpecialMove = -1;
	private AttackDamage move1Damage;
	private AttackDamage move2Damage;
    public static int SECOND_ATTACK_DELAY = 1000;
    public static int FIRST_ATTACK_TIME = 1600 - Formulas.START_COMBAT_TIME;

    @Override
    public AttackStrategyType getType() {
        return AttackStrategyType.DEFENSE;
    }
    @Override
    public boolean isDodgeStrategy() {
    	return false;
    }

    public DefenderAttack(PokemonData pokemon, Move move1, Move move2, int extraDelay, AttackDamage move1Damage, AttackDamage move2Damage) {
        this.pokemon = pokemon;
        this.extraDelay = extraDelay;
        this.move1 = move1;
        this.move2 = move2;
        this.move1Damage = move1Damage;
        this.move2Damage = move2Damage;
    }

    @Override
    public PokemonAttack nextAttack(CombatantState attackerState, CombatantState defenderState) {
        // Statistically speaking with a 50% chance of winning a coin flip, you
        // average out at at attack coming out 1 attack later
        if (nextSpecialMove == -1 && attackerState.getCurrentEnergy() >= -1 * move2.getEnergyDelta()) {
            nextSpecialMove = attackerState.getNumAttacks() + 1;
        }
        if (nextSpecialMove == attackerState.getNumAttacks()) {
            nextSpecialMove = -1;
            return getMove2Attack(extraDelay);
        }
        switch (attackerState.getNumAttacks()) {
        case 0:
            return getMove1Attack(FIRST_ATTACK_TIME);
        case 1:
            return getMove1Attack(SECOND_ATTACK_DELAY - move1.getDurationMs());
        case 2:
            return getMove1Attack(extraDelay - move1.getDurationMs());
        default:
            return getMove1Attack(extraDelay);
        }

    }

    @Component
    public static class DefenderAttackBuilder implements AttackStrategy.AttackStrategyBuilder<DefenderAttack> {
        @Resource
        private MoveRepository move;
        public static int DEFENDER_DELAY = 2000;

        @Override
        public DefenderAttack build(PokemonData pokemon, DodgeStrategy dodgeStrategy, AttackDamage move1Damage, AttackDamage move2Damage, Random r) {
        	// defenders never dodge
            return new DefenderAttack(pokemon, move.getById(pokemon.getMove1()), move.getById(pokemon.getMove2()),
                    DEFENDER_DELAY,  move1Damage,  move2Damage);
        }
    }

	public int getExtraDelay() {
		return extraDelay;
	}

	public PokemonData getPokemon() {
		return pokemon;
	}

	public Move getMove1() {
		return move1;
	}

	public Move getMove2() {
		return move2;
	}

	public int getNextSpecialMove() {
		return nextSpecialMove;
	}

	public AttackDamage getMove1Damage() {
		return move1Damage;
	}

	public AttackDamage getMove2Damage() {
		return move2Damage;
	}

}

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
import com.pokebattler.fight.data.proto.PokemonMoveOuterClass.PokemonMove;

public class QuickAttackOnly implements AttackStrategy {
    private final int extraDelay;
    private final Move move1;
    private final Move move2;
    private final AttackDamage move1Damage;
    private final AttackDamage move2Damage;
    
    @Override
    public AttackStrategyType getType() {
        return AttackStrategyType.QUICK_ATTACK_ONLY;
    }

    public QuickAttackOnly(Move move1, Move move2, int extraDelay, AttackDamage move1Damage, AttackDamage move2Damage) {
        this.move1 = move1;
        this.move2 = move2;
        this.extraDelay = extraDelay;
        this.move1Damage = move1Damage;
        this.move2Damage = move2Damage;
    }

    @Override
    public PokemonAttack nextAttack(CombatantState attackerState, CombatantState defenderState) {
        return getMove1Attack(extraDelay);
    }

    public int getDelay() {
        return getDelay();
    }

    @Component
    public static class QuickAttackOnlyBuilder implements AttackStrategy.AttackStrategyBuilder<QuickAttackOnly> {
		@Resource
		private MoveRepository move;
        @Override
        public QuickAttackOnly build(PokemonData pokemon, DodgeStrategy dodgeStrategy, AttackDamage move1Damage, AttackDamage move2Damage, Random r) {
        	// ignore dodge strategy
            return new QuickAttackOnly(move.getById(pokemon.getMove1()), move.getById(pokemon.getMove2()), 0, move1Damage, move2Damage);
        }
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

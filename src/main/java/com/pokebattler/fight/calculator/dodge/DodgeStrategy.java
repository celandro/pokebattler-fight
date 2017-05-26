package com.pokebattler.fight.calculator.dodge;

import java.util.Random;

import com.pokebattler.fight.calculator.CombatantState;
import com.pokebattler.fight.data.proto.FightOuterClass.DodgeStrategyType;
import com.pokebattler.fight.data.proto.MoveOuterClass.Move;

public interface DodgeStrategy {
	
	default boolean tryToDodge(CombatantState attackerState, CombatantState defenderState) {
		double nextRand = getRandom() == null?1E-6:getRandom().nextDouble();
		return nextRand <= chanceToDodge(attackerState,defenderState);
	}
	double chanceToDodge(CombatantState attackerState, CombatantState defenderState);
	Random getRandom();
	DodgeStrategyType getType();

    public static interface DodgeStrategyBuilder<S extends DodgeStrategy> {
        public default DodgeStrategyType getType() {
            return build(new Random()).getType();
        }

        S build(Random r);
    }

	
}

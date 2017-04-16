package com.pokebattler.fight.calculator.dodge;

import com.pokebattler.fight.calculator.CombatantState;
import com.pokebattler.fight.data.proto.FightOuterClass.DodgeStrategyType;

public interface DodgeStrategy {
	
	boolean tryToDodge(CombatantState attackerState, CombatantState defenderState);
	DodgeStrategyType getType();

    public static interface DodgeStrategyBuilder<S extends DodgeStrategy> {
        public default DodgeStrategyType getType() {
            return build().getType();
        }

        S build();
    }

	
}

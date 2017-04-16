package com.pokebattler.fight.calculator.dodge;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.calculator.CombatantState;
import com.pokebattler.fight.data.proto.FightOuterClass.DodgeStrategyType;

public class NeverDodge implements DodgeStrategy {

	@Override
	public boolean tryToDodge(CombatantState attackerState, CombatantState defenderState) {
		return false;
	}
	@Override
	public DodgeStrategyType getType() {
		return DodgeStrategyType.DODGE_0;
	}
    @Component
    public static class Builder implements DodgeStrategy.DodgeStrategyBuilder<NeverDodge> {
        @Override
        public NeverDodge build() {
            return new NeverDodge();
        }
    }
	

}

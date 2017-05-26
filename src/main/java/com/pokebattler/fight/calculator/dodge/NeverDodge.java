package com.pokebattler.fight.calculator.dodge;

import java.util.Random;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.calculator.CombatantState;
import com.pokebattler.fight.data.proto.FightOuterClass.DodgeStrategyType;

public class NeverDodge implements DodgeStrategy {

	@Override
	public double chanceToDodge(CombatantState attackerState, CombatantState defenderState) {
		return 0.0;
	}
	@Override
	public DodgeStrategyType getType() {
		return DodgeStrategyType.DODGE_0;
	}
    @Component
    public static class Builder implements DodgeStrategy.DodgeStrategyBuilder<NeverDodge> {
        @Override
        public NeverDodge build(Random r) {
            return new NeverDodge();
        }
    }
	@Override
	public Random getRandom() {
		return null;
	}
	

}

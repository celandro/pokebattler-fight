package com.pokebattler.fight.calculator.dodge;

import java.util.Random;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.calculator.CombatantState;
import com.pokebattler.fight.data.proto.FightOuterClass.DodgeStrategyType;

public class AlwaysDodge implements DodgeStrategy {

	@Override
	public double chanceToDodge(CombatantState attackerState, CombatantState defenderState) {
		return 1.0;
	}

	@Override
	public DodgeStrategyType getType() {
		return DodgeStrategyType.DODGE_100;
	}
	
    @Component
    public static class Builder implements DodgeStrategy.DodgeStrategyBuilder<AlwaysDodge> {
        @Override
        public AlwaysDodge build(Random r) {
            return new AlwaysDodge();
        }
    }

	@Override
	public Random getRandom() {
		return null;
	}

}

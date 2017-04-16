package com.pokebattler.fight.calculator.dodge;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.data.proto.FightOuterClass.DodgeStrategyType;

public class AlwaysDodge implements DodgeStrategy {

	@Override
	public boolean tryToDodge() {
		return true;
	}

	@Override
	public DodgeStrategyType getType() {
		return DodgeStrategyType.DODGE_100;
	}
	
    @Component
    public static class Builder implements DodgeStrategy.DodgeStrategyBuilder<AlwaysDodge> {
        @Override
        public AlwaysDodge build() {
            return new AlwaysDodge();
        }
    }

}

package com.pokebattler.fight.calculator.dodge;

import java.util.Random;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.data.proto.FightOuterClass.DodgeStrategyType;

public class DodgeReactionTime implements DodgeStrategy {
	private final Random random;
	private final double minDodgePercent;
	private final double maxDodgePercent; 
	private final int minSpeed; 
	private final int maxSpeed;

	public DodgeReactionTime(Random random, double minDodgePercent, double maxDodgePercent, int minSpeed, int maxSpeed) {
		this.random = random;
		this.minDodgePercent = minDodgePercent;
		this.maxDodgePercent = maxDodgePercent;
		this.minSpeed = minSpeed;
		this.maxSpeed = maxSpeed;
	}
	

	@Override
	public boolean tryToDodge() {
		return false;
	}
	@Override
	public DodgeStrategyType getType() {
		return DodgeStrategyType.DODGE_REACTION_TIME;
	}
	
    @Component
    public static class Builder implements DodgeStrategy.DodgeStrategyBuilder<DodgeReactionTime> {
        @Override
        public DodgeReactionTime build() {
            return new DodgeReactionTime(new Random(), 0.5, 0.75, 1000, 3500);
        }
    }

}

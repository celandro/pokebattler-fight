package com.pokebattler.fight.calculator.dodge;

import java.util.Random;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.calculator.CombatantState;
import com.pokebattler.fight.data.proto.FightOuterClass.DodgeStrategyType;

public class RandomDodge implements DodgeStrategy {
	private final Random random;
	private final double dodgePercent;
	private DodgeStrategyType type;

	public RandomDodge(Random random, double dodgePercent, DodgeStrategyType type) {
		this.random = random;
		this.dodgePercent = dodgePercent;
		this.type = type;
	}

	@Override
	public boolean tryToDodge(CombatantState attackerState, CombatantState defenderState) {
		return random.nextDouble() <= dodgePercent;
	}
	@Override
	public DodgeStrategyType getType() {
		return type;
	}
    @Component
    public static class Builder_25 implements DodgeStrategy.DodgeStrategyBuilder<RandomDodge> {
        @Override
        public RandomDodge build() {
            return new RandomDodge(new Random(), 0.25, DodgeStrategyType.DODGE_25);
        }
    }
	
    @Component
    public static class Builder_50 implements DodgeStrategy.DodgeStrategyBuilder<RandomDodge> {
        @Override
        public RandomDodge build() {
            return new RandomDodge(new Random(), 0.50, DodgeStrategyType.DODGE_50);
        }
    }
	
    @Component
    public static class Builder_75 implements DodgeStrategy.DodgeStrategyBuilder<RandomDodge> {
        @Override
        public RandomDodge build() {
            return new RandomDodge(new Random(), 0.75, DodgeStrategyType.DODGE_75);
        }
    }
	
    @Component
    public static class Builder_90 implements DodgeStrategy.DodgeStrategyBuilder<RandomDodge> {
        @Override
        public RandomDodge build() {
            return new RandomDodge(new Random(), 0.90, DodgeStrategyType.DODGE_90);
        }
    }
	

}

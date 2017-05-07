package com.pokebattler.fight.calculator.dodge;

import java.util.Random;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pokebattler.fight.calculator.CombatantState;
import com.pokebattler.fight.calculator.Formulas;
import com.pokebattler.fight.data.proto.FightOuterClass.DodgeStrategyType;

public class DodgeReactionTime implements DodgeStrategy {
	private final Random random;
	private final double minDodgePercent;
	private final double maxDodgePercent;
	private final int minSpeed;
	private final int maxSpeed;
	private final double effectiveAdjustment;
	private final Formulas f;
	private final static Logger log = LoggerFactory.getLogger(DodgeReactionTime.class); 

	public DodgeReactionTime(Random random, double minDodgePercent, double maxDodgePercent, int minSpeed, int maxSpeed,
			double effectiveAdjustment, Formulas f) {
		this.random = random;
		this.minDodgePercent = minDodgePercent;
		this.maxDodgePercent = maxDodgePercent;
		this.minSpeed = minSpeed;
		this.maxSpeed = maxSpeed;
		this.f = f;
		this.effectiveAdjustment = effectiveAdjustment;
	}

	@Override
	public boolean tryToDodge(CombatantState attackerState, CombatantState defenderState) {
		double chanceToDodge = maxDodgePercent;
		int damageWindow = defenderState.getNextMove().getDamageWindowStartMs();
		if (damageWindow <= minSpeed) {
			chanceToDodge = minDodgePercent;
		} else if (damageWindow < maxSpeed) {
			double ratio = (damageWindow - minSpeed) / ((double) (maxSpeed - minSpeed));
			chanceToDodge = (maxDodgePercent - minDodgePercent) * ratio + minDodgePercent;
		}
		if (defenderState.isNextMoveSpecial()) {
			// super effective text actually makes it harder to dodge!
			if (attackerState.getPreviousMove() != null && f.calculateModifier(attackerState.getPreviousMove(),
					attackerState.getPokemon(), defenderState.getPokemon()) != 1.0) {
				log.debug("Adjusting for super effective");
				chanceToDodge *= effectiveAdjustment;
			}
		}
		return random.nextDouble() <= chanceToDodge;
	}

	@Override
	public DodgeStrategyType getType() {
		return DodgeStrategyType.DODGE_REACTION_TIME;
	}

	@Component
	public static class Builder implements DodgeStrategy.DodgeStrategyBuilder<DodgeReactionTime> {
		@Resource
		Formulas f;
		public static final double MIN_DODGE_PERCENT = 0.5;
		public static final double MAX_DODGE_PERCENT = 0.9;
		public static final int MIN_SPEED = 1000;
		public static final int MAX_SPEED = 3000;
		public static final double PENALTY_DUE_TO_STUPID_SUPER_EFFECTIVE_TEXT = 0.75;

		@Override
		public DodgeReactionTime build(Random r) {
			return new DodgeReactionTime(r, MIN_DODGE_PERCENT, MAX_DODGE_PERCENT, MIN_SPEED, MAX_SPEED,
					PENALTY_DUE_TO_STUPID_SUPER_EFFECTIVE_TEXT, f);
		}
	}

}

package com.pokebattler.fight.calculator.dodge;

import java.util.Random;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pokebattler.fight.calculator.CombatantState;
import com.pokebattler.fight.calculator.Formulas;
import com.pokebattler.fight.data.proto.FightOuterClass.DodgeStrategyType;
import com.pokebattler.fight.data.proto.MoveOuterClass.Move;

public class DodgeReactionTime implements DodgeStrategy {
	private final Random random;
	private final double minDodgePercent;
	private final double maxDodgePercent;
	private final int minSpeed;
	private final int maxSpeed;
	private final double effectiveAdjustment;
	private final Formulas f;
	private final double fastMoveBonus;
	private final static Logger log = LoggerFactory.getLogger(DodgeReactionTime.class); 
	private final DodgeStrategyType dodgeStrategyType;

	public DodgeReactionTime(Random random, double minDodgePercent, double maxDodgePercent, int minSpeed, int maxSpeed,
			double effectiveAdjustment, double fastMoveBonus, Formulas f, DodgeStrategyType dodgeStrategyType) {
		this.random = random;
		this.minDodgePercent = minDodgePercent;
		this.maxDodgePercent = maxDodgePercent;
		this.minSpeed = minSpeed;
		this.maxSpeed = maxSpeed;
		this.f = f;
		this.effectiveAdjustment = effectiveAdjustment;
		this.fastMoveBonus = fastMoveBonus;
		this.dodgeStrategyType = dodgeStrategyType;
	}
	@Override
	public Random getRandom() {
		return random;
	}

	@Override
	public double chanceToDodge(CombatantState attackerState, CombatantState defenderState) {
		Move nextMove = defenderState.getNextMove();
		double chanceToDodge = maxDodgePercent;
		int damageWindow = nextMove.getDamageWindowStartMs();
		if (damageWindow <= minSpeed) {
			chanceToDodge = minDodgePercent;
		} else if (damageWindow < maxSpeed) {
			double ratio = (damageWindow - minSpeed) / ((double) (maxSpeed - minSpeed));
			chanceToDodge = (maxDodgePercent - minDodgePercent) * ratio + minDodgePercent;
		}
		if (isSuperEffective(attackerState, defenderState)) {
			log.debug("Adjusting for super effective");
			chanceToDodge *= effectiveAdjustment;
		}
		
		Move previousMove = attackerState.getPreviousMove();
		chanceToDodge = quickBonus(previousMove, nextMove, chanceToDodge);
		return chanceToDodge;
		
	}
	public double quickBonus(Move attackerPreviousMove, Move defenderNextMove, double chanceToDodge) {
		if (attackerPreviousMove != null) {
			double quickness = (double)defenderNextMove.getDamageWindowStartMs()/ attackerPreviousMove.getDurationMs()  - 0.5;
			if (quickness > 0) {
				chanceToDodge += (maxDodgePercent-chanceToDodge) * Math.min(1.0, quickness * fastMoveBonus);
			}
		}
		return chanceToDodge;
	}

	public boolean isSuperEffective(CombatantState attackerState, CombatantState defenderState) {
		return attackerState.getPreviousMove() != null && f.calculateModifier(attackerState.getPreviousMove(),
				null, defenderState.getPokemon()) != 1.0;
	}

	@Override
	public DodgeStrategyType getType() {
		return dodgeStrategyType;
	}

	@Component
	public static class Builder implements DodgeStrategy.DodgeStrategyBuilder<DodgeReactionTime> {
		@Resource
		Formulas f;
		public static final double MIN_DODGE_PERCENT = 0.5;
		public static final double MAX_DODGE_PERCENT = 0.9;
		public static final int MIN_SPEED = 1000;
		public static final int MAX_SPEED = 3000;
		public static final double FAST_MOVE_BONUS = 0.05;
		public static final double PENALTY_DUE_TO_STUPID_SUPER_EFFECTIVE_TEXT = 1.0;

		@Override
		public DodgeReactionTime build(Random r) {
			return new DodgeReactionTime(r, MIN_DODGE_PERCENT, MAX_DODGE_PERCENT, MIN_SPEED, MAX_SPEED,
					PENALTY_DUE_TO_STUPID_SUPER_EFFECTIVE_TEXT, FAST_MOVE_BONUS, f, DodgeStrategyType.DODGE_REACTION_TIME);
		}
	}

	@Component
	public static class Builder2 implements DodgeStrategy.DodgeStrategyBuilder<DodgeReactionTime> {
		@Resource
		Formulas f;
		public static final double MIN_DODGE_PERCENT = 0.7;
		public static final double MAX_DODGE_PERCENT = 0.9;
		public static final int MIN_SPEED = 1000;
		public static final int MAX_SPEED = 3000;
		public static final double FAST_MOVE_BONUS = 0.05;
		public static final double PENALTY_DUE_TO_STUPID_SUPER_EFFECTIVE_TEXT = 1.0;

		@Override
		public DodgeReactionTime build(Random r) {
			return new DodgeReactionTime(r, MIN_DODGE_PERCENT, MAX_DODGE_PERCENT, MIN_SPEED, MAX_SPEED,
					PENALTY_DUE_TO_STUPID_SUPER_EFFECTIVE_TEXT, FAST_MOVE_BONUS, f,DodgeStrategyType.DODGE_REACTION_TIME2);
		}
	}

}

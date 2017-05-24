package com.pokebattler.fight.strategies;

import static com.pokebattler.fight.data.MoveRepository.DODGE_MOVE;

import java.util.Random;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.MoveOuterClass.Move;
import com.pokebattler.fight.calculator.AttackDamage;
import com.pokebattler.fight.calculator.CombatantState;
import com.pokebattler.fight.calculator.Formulas;
import com.pokebattler.fight.calculator.dodge.DodgeStrategy;
import com.pokebattler.fight.data.MoveRepository;

public class DodgeWeave implements AttackStrategy {
	private final PokemonData pokemon;
	private final int extraDelay;
	private final Move move1;
	private final Move move2;
	private final DodgeStrategy dodgeStrategy;
	private final AttackDamage move1Damage;
	private final AttackDamage move2Damage;
	private boolean dodgedSpecial = false;
	private Move defMove1;
	private Move defMove2;
	private final AttackStrategyType type;

	private final int expectedMinDefDelay;

	public static final int SECOND_ATTACK_DELAY = 1000;
	public static final int FIRST_ATTACK_TIME = 1600 - Formulas.START_COMBAT_TIME;
	public static final int REAL_MIN_DEFENDER_DELAY = 1500;
	public static final int MIN_DEFENDER_DELAY_DODGE_SPECIALS = 2500;
	public static final int MIN_DEFENDER_DELAY_RANDOM = 0;
	public static final int MIN_DEFENDER_DELAY_RANDOM_SPECIALS_ONLY = -1;
	public static final int CHARGE_REALIZATION_DELAY = 700; // Human reaction +
															// time to swipe to
															// dodge.
	public static final int HUMAN_REACTION_TIME = 250; // Average Human reaction
														// time to visual
														// stimulus.

	int timeElapsed = Formulas.START_COMBAT_TIME; // used for the beginning of
													// the battle only.

	// used for random strategy
	public static final double BASE_DODGE_MISS_RATE = 0.1;
	public PokemonAttack lastDefenderAttack = null;
	public int currentDelayToUse = REAL_MIN_DEFENDER_DELAY;

	@Override
	public AttackStrategyType getType() {
		return type;
	}

	public DodgeWeave(PokemonData pokemon, Move move1, Move move2, int extraDelay, AttackStrategyType type,
			int expectedMinDefDelay, DodgeStrategy dodgeStrategy, AttackDamage move1Damage, AttackDamage move2Damage) {
		this.pokemon = pokemon;
		this.extraDelay = extraDelay;
		this.move1 = move1;
		this.move2 = move2;
		this.defMove1 = null;
		this.defMove2 = null;
		this.type = type;
		this.expectedMinDefDelay = expectedMinDefDelay;
		dodgedSpecial = false;
		this.dodgeStrategy = dodgeStrategy;
		this.move1Damage = move1Damage;
		this.move2Damage = move2Damage;
	}

	public boolean isRandomStrategy() {
		return expectedMinDefDelay <= 0;
	}

	@Override
	public PokemonAttack nextAttack(CombatantState attackerState, CombatantState defenderState) {
		// dodge special if we can

		int earliestNextDamageTime = calculateEarliestNextDamageTime(attackerState, defenderState);

		if (defenderState.getNextMove() != null && defenderState.getTimeToNextDamage() > 0
				&& !defenderState.isDodged()) {
			/**
			 * Removing ninja dodges since humans rarely have the reflex to do
			 * this. if (defenderState.getTimeToNextDamage() <=
			 * Formulas.DODGE_WINDOW + HUMAN_REACTION_TIME + extraDelay) {
			 * dodgedSpecial = defenderState.isNextMoveSpecial(); timeElapsed +=
			 * DODGE_MOVE.getDurationMs() + extraDelay; return new
			 * PokemonAttack(DODGE_MOVE.getMoveId(), extraDelay); } else
			 */
			if (earliestNextDamageTime > move2.getDurationMs() + extraDelay + CAST_TIME
					&& attackerState.getCurrentEnergy() >= -1 * move2.getEnergyDelta()) {
				// we can sneak in a special attack
				dodgedSpecial = false;
				timeElapsed += move2.getDurationMs() + extraDelay + CAST_TIME;
				return getMove2Attack(extraDelay + CAST_TIME);
			} else if (earliestNextDamageTime > move1.getDurationMs() + extraDelay) {
				// we can sneak in a normal attack
				dodgedSpecial = false;
				timeElapsed += move1.getDurationMs() + extraDelay;
				return getMove1Attack(extraDelay);
			} else {

				// We here have to define how much time the attacker will wait
				// to dodge.
				// If we're waiting for a quick attack but we get a long windup
				// charge instead we can maybe squeeze
				// some quick attacks after realizing we're getting a charge
				// move but before taking damage.

				if (defenderState.isNextMoveSpecial()) {
					int moveStartTime = defenderState.getTimeToNextDamage()
							- defenderState.getNextMove().getDamageWindowStartMs();
					int realizationTime = moveStartTime + CHARGE_REALIZATION_DELAY;
					if (realizationTime < 0) {
						realizationTime = 0;
					}
					if (defenderState.getTimeToNextDamage() > realizationTime + move1.getDurationMs() + extraDelay) {
						// we can sneak in a normal attack after realization
						dodgedSpecial = false;
						timeElapsed += move1.getDurationMs() + realizationTime + extraDelay;
						return getMove1Attack(realizationTime + extraDelay);
					}
				}
				if (dodgeStrategy.tryToDodge(attackerState, defenderState)) {

					int dodgeWait = defenderState.getTimeToNextDamage() == Integer.MAX_VALUE ? earliestNextDamageTime
							: defenderState.getTimeToNextDamage();
					if (dodgeWait > 1000000 || dodgeWait < 0) {
						dodgeWait = 0;
					}

					dodgedSpecial = defenderState.isNextMoveSpecial();
					timeElapsed += DODGE_MOVE.getDurationMs()
							+ Math.max(0, dodgeWait - Formulas.DODGE_WINDOW + HUMAN_REACTION_TIME);
					return getDodge(Math.max(0, dodgeWait - Formulas.DODGE_WINDOW + HUMAN_REACTION_TIME));
				}
				// missed dodge fall through to rest of the code
			}
		}
		if (defenderState.getNumAttacks() < 3) { // early battle
			if (earliestNextDamageTime > move1.getDurationMs() + extraDelay) {
				// we can sneak in a normal attack
				dodgedSpecial = false;
				timeElapsed += move1.getDurationMs() + extraDelay;
				return getMove1Attack(extraDelay);
			} else {
				dodgedSpecial = false; // early battle defender never throws
										// specials
				timeElapsed += DODGE_MOVE.getDurationMs()
						+ Math.max(0, earliestNextDamageTime - Formulas.DODGE_WINDOW + HUMAN_REACTION_TIME);
				return getDodge(Math.max(0, earliestNextDamageTime - Formulas.DODGE_WINDOW + HUMAN_REACTION_TIME));
			}
		} else {
			if (attackerState.getCurrentEnergy() >= -1 * move2.getEnergyDelta()
					&& (earliestNextDamageTime > move2.getDurationMs() + HUMAN_REACTION_TIME + extraDelay + CAST_TIME
							|| dodgedSpecial)) { // two conditions for firing
													// specials, either we know
													// it fits or we've just
													// dodged a special.
				dodgedSpecial = false;
				timeElapsed += move2.getDurationMs() + extraDelay + CAST_TIME;
				return getMove2Attack(extraDelay + CAST_TIME);
			} else {
				dodgedSpecial = false;
				timeElapsed += move1.getDurationMs() + extraDelay;
				return getMove1Attack(extraDelay);
			}
		}
	}

	public int getDelay() {
		return getDelay();
	}

	private int calculateEarliestNextDamageTime(CombatantState attackerState, CombatantState defenderState) {

		// Learning Defender moves.
		if (defMove2 == null && defenderState.isNextMoveSpecial()) {
			defMove2 = defenderState.getNextMove();
		} else if (defMove1 == null && !defenderState.isNextMoveSpecial()) {
			defMove1 = defenderState.getNextMove();
		}

		int earliestNextDamageTime = -1;

		if (defenderState.getNumAttacks() < 3) {
			// Beginning of battle
			int firstDamageTime = FIRST_ATTACK_TIME + Formulas.START_COMBAT_TIME
					+ (defenderState.getNextMove() == null?0:defenderState.getNextMove().getDamageWindowStartMs());
			int secondDamageTime = firstDamageTime + SECOND_ATTACK_DELAY;
			int thirdDamageTime = firstDamageTime + (defenderState.getNextMove() == null?0:defenderState.getNextMove().getDurationMs()) + expectedMinDefDelay;

			if (timeElapsed < firstDamageTime) {
				earliestNextDamageTime = firstDamageTime - timeElapsed;
			} else if (timeElapsed < secondDamageTime) {
				earliestNextDamageTime = secondDamageTime - timeElapsed;
			} else {
				earliestNextDamageTime = thirdDamageTime - timeElapsed;
			}
		} else {
			int chargeWindowStart = Integer.MAX_VALUE;
			if(defMove1 == null) {
				defMove1 = null;
			}
			int quickWindowStart = defMove1.getDamageWindowStartMs();
			if (defMove2 != null) {
				chargeWindowStart = defMove2.getDamageWindowStartMs();
			}

			int shortestWindowStart = chargeWindowStart < quickWindowStart ? chargeWindowStart : quickWindowStart;

			int expectedDelayToUse = expectedMinDefDelay;


			if (expectedMinDefDelay == MIN_DEFENDER_DELAY_DODGE_SPECIALS) {
				// never risk eating a charge attack!!
				expectedDelayToUse = REAL_MIN_DEFENDER_DELAY + (chargeWindowStart - shortestWindowStart); 
			}

			if (defenderState.isDodged() || defenderState.getTimeToNextDamage() == Integer.MAX_VALUE) {
				// current attack already dodged or damage already taken but
				// next attack hasn't started yet.
				// We have to backtrack the current attack to figure out when
				// the next one can possibly hit.

				earliestNextDamageTime = defenderState.getTimeToNextAttack() + expectedDelayToUse + shortestWindowStart;

			} else {
				int attackStartTime = defenderState.getTimeToNextAttack() - defenderState.getNextMove().getDurationMs();

				earliestNextDamageTime = attackStartTime + shortestWindowStart
						- (defenderState.getNextAttack().getDelay() - expectedDelayToUse);
			}

		}

		// if(earliestNextDamageTime < 0){
		// System.out.println("Negative earliestNextDamage: " +
		// earliestNextDamageTime);
		// earliestNextDamageTime = 0;
		// }

		return earliestNextDamageTime;

	}

	@Component
	public static class DodgeWeaveCautiousBuilder implements AttackStrategy.AttackStrategyBuilder<DodgeWeave> {
		public static final int MIN_DEFENDER_DELAY = 1500;
		@Resource
		private MoveRepository move;

		@Override
		public DodgeWeave build(PokemonData pokemon, DodgeStrategy dodgeStrategy, AttackDamage move1Damage, AttackDamage move2Damage, Random r) {
			return new DodgeWeave(pokemon, move.getById(pokemon.getMove1()), move.getById(pokemon.getMove2()), 0,
					AttackStrategyType.DODGE_WEAVE_CAUTIOUS, MIN_DEFENDER_DELAY, dodgeStrategy, move1Damage, move2Damage);
		}
	}

	@Component
	public static class DodgeWeaveReasonableBuilder implements AttackStrategy.AttackStrategyBuilder<DodgeWeave> {
		public static final int MIN_DEFENDER_DELAY = 1750;
		@Resource
		private MoveRepository move;

		@Override
		public DodgeWeave build(PokemonData pokemon, DodgeStrategy dodgeStrategy, AttackDamage move1Damage, AttackDamage move2Damage, Random r) {
			return new DodgeWeave(pokemon, move.getById(pokemon.getMove1()), move.getById(pokemon.getMove2()), 0,
					AttackStrategyType.DODGE_WEAVE_REASONABLE, MIN_DEFENDER_DELAY, dodgeStrategy, move1Damage, move2Damage);
		}
	}

	@Component
	public static class DodgeWeaveRiskyBuilder implements AttackStrategy.AttackStrategyBuilder<DodgeWeave> {
		public static final int MIN_DEFENDER_DELAY = 2000;
		@Resource
		private MoveRepository move;

		@Override
		public DodgeWeave build(PokemonData pokemon, DodgeStrategy dodgeStrategy, AttackDamage move1Damage, AttackDamage move2Damage, Random r) {
			return new DodgeWeave(pokemon, move.getById(pokemon.getMove1()), move.getById(pokemon.getMove2()), 0,
					AttackStrategyType.DODGE_WEAVE_RISKY, MIN_DEFENDER_DELAY,  dodgeStrategy, move1Damage, move2Damage);
		}
	}

	@Component
	public static class DodgeWeaveSpecialsBuilder implements AttackStrategy.AttackStrategyBuilder<DodgeWeave> {
		@Resource
		private MoveRepository move;

		@Override
		public DodgeWeave build(PokemonData pokemon, DodgeStrategy dodgeStrategy, AttackDamage move1Damage, AttackDamage move2Damage, Random r) {
			return new DodgeWeave(pokemon, move.getById(pokemon.getMove1()), move.getById(pokemon.getMove2()), 0,
					AttackStrategyType.DODGE_WEAVE_SPECIALS, MIN_DEFENDER_DELAY_DODGE_SPECIALS, dodgeStrategy, move1Damage, move2Damage);
		}
	}

	@Component
	public static class DodgeWeaveHumanBuilder implements AttackStrategy.AttackStrategyBuilder<DodgeWeave> {
		@Resource
		private MoveRepository move;

		@Override
		public DodgeWeave build(PokemonData pokemon, DodgeStrategy dodgeStrategy, AttackDamage move1Damage, AttackDamage move2Damage, Random r) {
			return new DodgeWeave(pokemon, move.getById(pokemon.getMove1()), move.getById(pokemon.getMove2()), 0,
					AttackStrategyType.DODGE_WEAVE_HUMAN, MIN_DEFENDER_DELAY_RANDOM, dodgeStrategy, move1Damage, move2Damage);
		}
	}

	@Component
	public static class DodgeWeaveSpecialsHumanBuilder implements AttackStrategy.AttackStrategyBuilder<DodgeWeave> {
		@Resource
		private MoveRepository move;

		@Override
		public DodgeWeave build(PokemonData pokemon, DodgeStrategy dodgeStrategy, AttackDamage move1Damage, AttackDamage move2Damage, Random r) {
			return new DodgeWeave(pokemon, move.getById(pokemon.getMove1()), move.getById(pokemon.getMove2()), 0,
					AttackStrategyType.DODGE_WEAVE_SPECIALS_HUMAN, MIN_DEFENDER_DELAY_RANDOM_SPECIALS_ONLY,
					dodgeStrategy, move1Damage, move2Damage);
		}
	}

	public PokemonData getPokemon() {
		return pokemon;
	}

	public int getExtraDelay() {
		return extraDelay;
	}

	public Move getMove1() {
		return move1;
	}

	public Move getMove2() {
		return move2;
	}

	public DodgeStrategy getDodgeStrategy() {
		return dodgeStrategy;
	}

	public AttackDamage getMove1Damage() {
		return move1Damage;
	}

	public AttackDamage getMove2Damage() {
		return move2Damage;
	}

	public int getExpectedMinDefDelay() {
		return expectedMinDefDelay;
	}

}

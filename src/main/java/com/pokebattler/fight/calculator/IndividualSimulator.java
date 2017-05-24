package com.pokebattler.fight.calculator;

import java.util.Random;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pokebattler.fight.calculator.dodge.DodgeStrategy;
import com.pokebattler.fight.calculator.dodge.DodgeStrategyRegistry;
import com.pokebattler.fight.data.CpMRepository;
import com.pokebattler.fight.data.MoveRepository;
import com.pokebattler.fight.data.PokemonDataCreator;
import com.pokebattler.fight.data.PokemonRepository;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.FightOuterClass.CombatResult;
import com.pokebattler.fight.data.proto.FightOuterClass.Combatant;
import com.pokebattler.fight.data.proto.FightOuterClass.CombatantResultOrBuilder;
import com.pokebattler.fight.data.proto.FightOuterClass.DodgeStrategyType;
import com.pokebattler.fight.data.proto.FightOuterClass.Fight;
import com.pokebattler.fight.data.proto.FightOuterClass.FightResult;
import com.pokebattler.fight.data.proto.MoveOuterClass.Move;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;
import com.pokebattler.fight.data.proto.PokemonIdOuterClass.PokemonId;
import com.pokebattler.fight.data.proto.PokemonMoveOuterClass.PokemonMove;
import com.pokebattler.fight.data.proto.PokemonOuterClass.Pokemon;
import com.pokebattler.fight.data.proto.Ranking.DefenderSubResultOrBuilder;
import com.pokebattler.fight.strategies.AttackStrategy;
import com.pokebattler.fight.strategies.AttackStrategy.PokemonAttack;
import com.pokebattler.fight.strategies.AttackStrategyRegistry;

@Service("IndividualSimulator")
public class IndividualSimulator implements AttackSimulator {
	@Resource
	private Formulas f;
	@Resource
	private PokemonRepository pokemonRepository;
	@Resource
	private MoveRepository moveRepository;
	@Resource
	private CpMRepository cpmRepository;
	@Resource
	private AttackStrategyRegistry attackStrategies;
	@Resource
	private PokemonDataCreator creator;
	@Resource
	private DodgeStrategyRegistry dodgeStrategies;

	// todo make this configurable
	public static int attackerReactionTime = 0;
	public Logger log = LoggerFactory.getLogger(getClass());

	public static final double MAX_RATIO = 100.0;
	public static final double MAX_POWER = 2.0;
	public static final double MIN_POWER = -2.0;

	@Override
	public FightResult fight(Fight fight, Random r) {
		PokemonData attacker = fight.getAttacker1();
		PokemonData defender = fight.getDefender();
		Pokemon a = pokemonRepository.getById(attacker.getPokemonId());
		Pokemon d = pokemonRepository.getById(defender.getPokemonId());
		// handle ditto
		if (attacker.getMove1() == PokemonMove.TRANSFORM_FAST) {
			a = pokemonRepository.transform(a, d);
			attacker = creator.transform(attacker, defender);
		}
		if (defender.getMove1() == PokemonMove.TRANSFORM_FAST) {
			d = pokemonRepository.transform(d, a);
			defender = creator.transform(defender, attacker);
		}
		final DodgeStrategy dodgeStrategy = dodgeStrategies.create(fight.getDodgeStrategy(), r);
		final CombatantState attackerState = new CombatantState(a, attacker, f, isDefender(fight.getStrategy()), moveRepository);
		final CombatantState defenderState = new CombatantState(d, defender, f, isDefender(fight.getDefenseStrategy()), moveRepository);

		final AttackStrategy attackerStrategy = attackStrategies.create(fight.getStrategy(), attacker, dodgeStrategy,
				f.damageOfMove(attackerState.getAttack(),  defenderState.getDefense(), moveRepository.getById(attacker.getMove1()), a, d),
				f.damageOfMove(attackerState.getAttack(),  defenderState.getDefense(), moveRepository.getById(attacker.getMove2()), a, d), r);
		final AttackStrategy defenderStrategy = attackStrategies.create(fight.getDefenseStrategy(), defender,dodgeStrategy,
				f.damageOfMove(defenderState.getAttack(),  attackerState.getDefense(), moveRepository.getById(defender.getMove1()), d, a),
				f.damageOfMove(defenderState.getAttack(),  attackerState.getDefense(), moveRepository.getById(defender.getMove2()), d, a), r);

		final FightResult.Builder fightResult = FightResult.newBuilder();

		if (isDefender(defenderStrategy)) {
			nextAttack(defenderStrategy, defenderState, attackerState);
		} else {
			nextAttack(attackerStrategy, attackerState, defenderState);
		}
		int currentTime = Formulas.START_COMBAT_TIME;
		if (log.isDebugEnabled()) {
			log.debug("{}: {} chose {} with {} energy", currentTime - Formulas.START_COMBAT_TIME,
					defenderState.getPokemonId(), defenderState.getNextMove().getMoveId(),
					defenderState.getCurrentEnergy());
		}

		while (attackerState.isAlive() && defenderState.isAlive() && currentTime < Formulas.MAX_COMBAT_TIME_MS) {
			// do defender first since defender strategy determines attacker
			// strategy
			if (defenderState.getNextMove() == null) {
				int energyGain = nextAttack(defenderStrategy, defenderState, attackerState);
				if (log.isDebugEnabled()) {
					log.debug("D{}: {} chose {}, gaining {} energy, new energy {}",
							currentTime - Formulas.START_COMBAT_TIME, defenderState.getPokemonId(),
							defenderState.getNextMove().getMoveId(), energyGain, defenderState.getCurrentEnergy());
				}
			}
			if (attackerState.getNextMove() == null) {
				int energyGain = nextAttack(attackerStrategy, attackerState, defenderState);
				
				if (log.isDebugEnabled()) {
					log.debug("A{}: {} chose {}  gaining {} energy, new energy {}",
							currentTime - Formulas.START_COMBAT_TIME, attackerState.getPokemonId(),
							attackerState.getNextMove().getMoveId(), energyGain, attackerState.getCurrentEnergy());
				}
			}
			final int timeToNextAttack = attackerState.getTimeToNextAttack();
			final int timeToNextDefense = defenderState.getTimeToNextAttack();
			final int timeToNextAttackDamage = attackerState.getTimeToNextDamage();
			final int timeToNextDefenseDamage = defenderState.getTimeToNextDamage();

			// make sure we arent over the max time
			if ((currentTime + timeToNextAttack > Formulas.MAX_COMBAT_TIME_MS
					|| currentTime + timeToNextAttackDamage > Formulas.MAX_COMBAT_TIME_MS)
					&& (currentTime + timeToNextDefense > Formulas.MAX_COMBAT_TIME_MS
							&& currentTime + timeToNextDefenseDamage > Formulas.MAX_COMBAT_TIME_MS)) {
				currentTime = Formulas.MAX_COMBAT_TIME_MS;
			}
			// tie goes to attacker
			else if (timeToNextAttackDamage >= 0 && timeToNextAttackDamage <= timeToNextAttack
					&& timeToNextAttackDamage <= timeToNextDefense
					&& timeToNextAttackDamage <= timeToNextDefenseDamage) {
				final CombatResult.Builder combatBuilder = f.getCombatResult(attackerState.getNextAttack().getDamage(), 
						attackerState.getNextMove(), attackerState.isDodged());
				currentTime += timeToNextAttackDamage;
				final CombatResult result = combatBuilder.setCurrentTime(currentTime).setAttackerId(attacker.getId())
						.setAttacker(Combatant.ATTACKER1).setDefender(Combatant.DEFENDER).setCurrentTime(currentTime)
						.build();

				int myEnergyGain = attackerState.applyAttack(result, timeToNextAttackDamage);
				int energyGain = defenderState.applyDefense(result, timeToNextAttackDamage);
				if (log.isDebugEnabled()) {
					log.debug("A{}: {} took {} damage and gained {} energy. defender gained {} energy",
							currentTime - Formulas.START_COMBAT_TIME, defenderState.getPokemonId(), result.getDamage(),
							myEnergyGain, energyGain);
				}
				if (fight.getIncludeDetails()) {
					fightResult.addCombatResult(result);
				}

			} else if (timeToNextDefenseDamage >= 0 && timeToNextDefenseDamage <= timeToNextAttack
					&& timeToNextDefenseDamage <= timeToNextDefense) {
				final CombatResult.Builder combatBuilder = f.getCombatResult(defenderState.getNextAttack().getDamage(), 
						defenderState.getNextMove(), defenderState.isDodged());
				currentTime += timeToNextDefenseDamage;

				final CombatResult result = combatBuilder.setCurrentTime(currentTime).setAttackerId(defender.getId())
						.setAttacker(Combatant.DEFENDER).setDefender(Combatant.ATTACKER1).build();
				int myEnergyGain = defenderState.applyAttack(result, timeToNextDefenseDamage);
				int energyGain = attackerState.applyDefense(result, timeToNextDefenseDamage);
				if (log.isDebugEnabled()) {
					log.debug("D{}: {} took {} damage and gained {} energy. defender gained {} energy",
							currentTime - Formulas.START_COMBAT_TIME, attackerState.getPokemonId(), result.getDamage(),
							myEnergyGain, energyGain);
				}
				// log.debug("Defender State {}",defenderState);
				if (fight.getIncludeDetails()) {
					fightResult.addCombatResult(result);
				}
			} else if (timeToNextAttack <= timeToNextDefense) {
				currentTime += timeToNextAttack;
				attackerState.resetAttack(timeToNextAttack);
				if (log.isDebugEnabled()) {
					log.debug("A{}: {} finished his attack", currentTime - Formulas.START_COMBAT_TIME,
							attackerState.getPokemonId());
				}
				defenderState.moveTime(timeToNextAttack);
			} else {
				currentTime += timeToNextDefense;
				defenderState.resetAttack(timeToNextDefense);
				if (log.isDebugEnabled()) {
					log.debug("D{}: {} finished his attack", currentTime - Formulas.START_COMBAT_TIME,
							defenderState.getPokemonId());
				}
				attackerState.moveTime(timeToNextDefense);
			}

		}
		fightResult.setTotalCombatTime(currentTime).setNumSims(1)
				.addCombatants(attackerState.toResult(Combatant.ATTACKER1, attackerStrategy.getType(), currentTime))
				.addCombatants(defenderState.toResult(Combatant.DEFENDER, defenderStrategy.getType(), currentTime))
				.setFightParameters(fight);
		fightResult.setWin(getWin(fightResult));
		fightResult.setPrestige(getPrestigeGain(fightResult));
		fightResult.setPowerLog(getPower(fightResult));
		fightResult.setPower(Math.pow(10, fightResult.getPowerLog()));
		fightResult.setEffectiveCombatTime(getEffectiveCombatTime(fightResult));
		fightResult.setPotions(getPotions(fightResult));
		fightResult.setOverallRating(getOverallRating(fightResult));
		return fightResult.build();
	}

	private int nextAttack(AttackStrategy strategy, CombatantState attackerState, CombatantState defenderState) {
		final PokemonAttack nextAttack = strategy.nextAttack(attackerState, defenderState);
		final Move nextMove = nextAttack.getMove();
		return attackerState.setNextAttack(nextAttack, nextMove);
	}

	boolean isDefender(AttackStrategy strategy) {
		return isDefender(strategy.getType());
	}

	boolean isDefender(AttackStrategyType strategyType) {
		return strategyType.name().startsWith("DEFENSE");
	}

	double getOverallRating(FightResult.Builder result) {
		int effectiveCombatTime = result.getEffectiveCombatTime();
		double potions = result.getPotions();
		double powerLog = result.getPowerLog();
		boolean isDefender = !isDefender(result.getFightParameters().getDefenseStrategy());

		return f.getOverallRating(effectiveCombatTime, potions, powerLog, isDefender);
	}


	boolean getWin(FightResult.Builder result) {
		if (isDefender(result.getFightParameters().getDefenseStrategy())) {
			return result.getCombatants(1).getEndHp() <= 0;
		} else {
			return result.getCombatants(0).getEndHp() > 0;
		}

	}

	double getPotions(FightResult.Builder result) {
		int deaths;
		int damageDealt;
		if (isDefender(result.getFightParameters().getDefenseStrategy())) {
			deaths = (int) (1.0 / result.getPower());
			damageDealt = result.getCombatants(1).getDamageDealt();
		} else {
			deaths = (int) result.getPower();
			damageDealt = result.getCombatants(0).getDamageDealt();
		}
		// add in extra potions for deaths.
		return damageDealt / 20.0 + deaths * 5 + deaths * (damageDealt / 2) / 20.0;
	}

	int getPrestigeGain(FightResult.Builder result) {
		if (isDefender(result.getFightParameters().getDefenseStrategy())) {
			if (result.getWin()) {
				return f.defensePrestigeGain(result.getCombatantsOrBuilder(0).getCp(),
						result.getCombatantsOrBuilder(1).getCp());
			}
		} else {
			if (!result.getWin()) {
				return f.defensePrestigeGain(result.getCombatantsOrBuilder(1).getCp(),
						result.getCombatantsOrBuilder(0).getCp());
			}
		}
		return 0;
	}

	int getEffectiveCombatTime(FightResult.Builder result) {
		final double multiplier;
		if (isDefender(result.getFightParameters().getDefenseStrategy())) {
			if (result.getWin()) {
				// attacker won, no penalty
				return result.getTotalCombatTime();
			} else {
				CombatantResultOrBuilder defender = result.getCombatantsOrBuilder(1);
				multiplier = defender.getStartHp() / ((double) (defender.getStartHp() - defender.getEndHp()));
			}
		} else {
			if (result.getWin()) {
				// the defender won, penalize the attacker (defender variable
				CombatantResultOrBuilder defender = result.getCombatantsOrBuilder(0);
				multiplier = defender.getStartHp() / ((double) (defender.getStartHp() - defender.getEndHp()));
			} else {
				// the defender lost, do not penalize
				return result.getTotalCombatTime();
			}
		}
		return (int) Math.round(result.getTotalCombatTime() * multiplier)
				+ Formulas.LOSS_TIME_MS * ((int) (multiplier));

	}

	double getPower(FightResult.Builder result) {
		CombatantResultOrBuilder attacker = result.getCombatantsOrBuilder(0);
		CombatantResultOrBuilder defender = result.getCombatantsOrBuilder(1);
		// cap at 1.0 and handle timeouts
		double attackerHpRatio = Math.min(MAX_RATIO,
				(attacker.getStartHp() - attacker.getEndHp()) / (double) attacker.getStartHp());
		double defenderHpRatio = Math.min(MAX_RATIO,
				(defender.getStartHp() - defender.getEndHp()) / (double) defender.getStartHp());
		if (result.getTotalCombatTime() >= Formulas.MAX_COMBAT_TIME_MS) {
			if (isDefender(result.getFightParameters().getDefenseStrategy())) {
				attackerHpRatio = 1.0;
			} else {
				defenderHpRatio = 1.0;
			}
		}
		final double retval;
		if (attackerHpRatio == 0.0) {
			// attacker takes no damage
			retval = MAX_POWER;
		} else if (defenderHpRatio == 0.0) {
			// defender takes no damage
			retval = MIN_POWER;
		} else {
			retval = Math.max(MIN_POWER, Math.min(MAX_POWER, Math.log10(defenderHpRatio / attackerHpRatio)));
		}
		return retval;
	}

	public PokemonRepository getPokemonRepository() {
		return pokemonRepository;
	}

	public PokemonDataCreator getCreator() {
		return creator;
	}

}

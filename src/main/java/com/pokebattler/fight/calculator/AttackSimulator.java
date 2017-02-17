package com.pokebattler.fight.calculator;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pokebattler.fight.data.CpMRepository;
import com.pokebattler.fight.data.MoveRepository;
import com.pokebattler.fight.data.PokemonDataCreator;
import com.pokebattler.fight.data.PokemonRepository;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.FightOuterClass.CombatResult;
import com.pokebattler.fight.data.proto.FightOuterClass.Combatant;
import com.pokebattler.fight.data.proto.FightOuterClass.CombatantResultOrBuilder;
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

@Service
public class AttackSimulator {
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

    // todo make this configurable
    public static int attackerReactionTime = 0;
    public Logger log = LoggerFactory.getLogger(getClass());

    public static final double MAX_POWER = 10.0;
    public static final double MIN_POWER = -10.0;
    
    
    public FightResult calculateMaxAttackDPS(PokemonId attackerId, PokemonId defenderId, PokemonMove move1,
            PokemonMove move2, AttackStrategyType strategy) {
        final String level = Integer.toString(Formulas.MAX_LEVEL);
        return calculateMaxAttackDPS(attackerId, defenderId, move1, move2, strategy, level, level);

    }

    public FightResult calculateMaxAttackDPS(PokemonId attackerId, PokemonId defenderId, PokemonMove move1,
            PokemonMove move2, AttackStrategyType strategy, String attackerLevel, String defenderLevel) {
        final PokemonData attacker = creator.createMaxStatPokemon(attackerId, attackerLevel, move1, move2);
        final Pokemon d = pokemonRepository.getById(defenderId);
        final PokemonData defender = creator.createMaxStatPokemon(defenderId, defenderLevel, d.getQuickMoves(0),
                d.getCinematicMoves(0));
        return calculateAttackDPS(attacker, defender, strategy);
    }

    public FightResult calculateAttackDPS(PokemonData attacker, PokemonData defender,
            AttackStrategyType attackerStrategy) {
        return calculateAttackDPS(attacker, defender, attackerStrategy, AttackStrategyType.DEFENSE);
    }

    public FightResult calculateAttackDPS(PokemonData attacker, PokemonData defender,
            AttackStrategyType attackerStrategy, AttackStrategyType defenseStrategy) {
        return fight(Fight.newBuilder().setAttacker1(attacker).setDefender(defender).setStrategy(attackerStrategy)
                .setDefenseStrategy(defenseStrategy).build(), true).build();
    }

    private void nextAttack(AttackStrategy strategy, CombatantState attackerState, CombatantState defenderState) {
        final PokemonAttack nextAttack = strategy.nextAttack(attackerState, defenderState);
        final Move nextMove = moveRepository.getById(nextAttack.getMove());
        attackerState.setNextAttack(nextAttack, nextMove);
    }
    boolean isDefender(AttackStrategy strategy) {
        return strategy.getType().name().startsWith("DEFENSE");
    }

    public FightResult.Builder fight(Fight fight, boolean includeDetails) {
        PokemonData attacker = fight.getAttacker1();
        PokemonData defender = fight.getDefender();
        Pokemon a = pokemonRepository.getById(attacker.getPokemonId());
        Pokemon d = pokemonRepository.getById(defender.getPokemonId());
        // handle ditto
        if (attacker.getMove1() == PokemonMove.TRANSFORM_FAST) {
            a = pokemonRepository.transform(a,d);
            attacker = creator.transform(attacker, defender);
        }
        if (defender.getMove1() == PokemonMove.TRANSFORM_FAST) {
            d = pokemonRepository.transform(d,a);
            defender = creator.transform(defender, attacker);
        }
        
        final AttackStrategy attackerStrategy = attackStrategies.create(fight.getStrategy(), attacker);
        final AttackStrategy defenderStrategy = attackStrategies.create(fight.getDefenseStrategy(), defender);
        final FightResult.Builder fightResult = FightResult.newBuilder();
        
        final CombatantState attackerState = new CombatantState(a, attacker, f, isDefender(attackerStrategy));
        final CombatantState defenderState = new CombatantState(d, defender, f, isDefender(defenderStrategy));
        
        
        nextAttack(defenderStrategy, defenderState, attackerState);
        int currentTime = Formulas.START_COMBAT_TIME;
        log.debug("{}: {} chose {} with {} energy", currentTime - Formulas.START_COMBAT_TIME, defenderState.getPokemon(), defenderState.getNextMove().getMoveId(), defenderState.getCurrentEnergy());

        while (attackerState.isAlive() && defenderState.isAlive() && currentTime < Formulas.MAX_COMBAT_TIME_MS) {
            // do defender first since defender strategy determines attacker
            // strategy
            if (defenderState.getNextMove() == null) {
                nextAttack(defenderStrategy, defenderState, attackerState);
                log.debug("D{}: {} chose {} with {} energy", currentTime - Formulas.START_COMBAT_TIME, defenderState.getPokemon(), defenderState.getNextMove().getMoveId(), defenderState.getCurrentEnergy());
            }
            if (attackerState.getNextMove() == null) {
                nextAttack(attackerStrategy, attackerState, defenderState);
                log.debug("A{}: {} chose {} with {} energy", currentTime - Formulas.START_COMBAT_TIME, attackerState.getPokemon(), attackerState.getNextMove().getMoveId(), attackerState.getCurrentEnergy());
            }
            final int timeToNextAttack = attackerState.getTimeToNextAttack();
            final int timeToNextDefense = defenderState.getTimeToNextAttack();
            final int timeToNextAttackDamage = attackerState.getTimeToNextDamage();
            final int timeToNextDefenseDamage = defenderState.getTimeToNextDamage();
            
            // make sure we arent over the max time
            if ((currentTime + timeToNextAttack > Formulas.MAX_COMBAT_TIME_MS ||
                currentTime + timeToNextAttackDamage > Formulas.MAX_COMBAT_TIME_MS) &&
                (currentTime + timeToNextDefense > Formulas.MAX_COMBAT_TIME_MS &&                    
                currentTime + timeToNextDefenseDamage > Formulas.MAX_COMBAT_TIME_MS)) {
                currentTime = Formulas.MAX_COMBAT_TIME_MS;
            }
            // tie goes to attacker
            else if (timeToNextAttackDamage >= 0 && timeToNextAttackDamage <= timeToNextAttack &&
                    timeToNextAttackDamage <= timeToNextDefense && timeToNextAttackDamage <= timeToNextDefenseDamage ) {
                final CombatResult.Builder combatBuilder = f.getCombatResult(attackerState.getAttack(),
                        defenderState.getDefense(), attackerState.getNextMove(), a, d,  attackerState.isDodged());
                currentTime += timeToNextAttackDamage;
                final CombatResult result = combatBuilder.setCurrentTime(currentTime).setAttackerId(attacker.getId())
                        .setAttacker(Combatant.ATTACKER1).setDefender(Combatant.DEFENDER).setCurrentTime(currentTime)
                        .build();

                attackerState.applyAttack(result, timeToNextAttackDamage);
                int energyGain = defenderState.applyDefense(result, timeToNextAttackDamage);
                log.debug("A{}: {} took {} damage and gained {} energy", currentTime - Formulas.START_COMBAT_TIME, defenderState.getPokemon(), 
                        result.getDamage(), energyGain);
                if (includeDetails) {
                	fightResult.addCombatResult(result);
                }

            } else if (timeToNextDefenseDamage >= 0 && timeToNextDefenseDamage <= timeToNextAttack &&
                    timeToNextDefenseDamage <= timeToNextDefense  ) {
                final CombatResult.Builder combatBuilder = f.getCombatResult(defenderState.getAttack(),
                        attackerState.getDefense(), defenderState.getNextMove(), d, a, defenderState.isDodged());
                currentTime += timeToNextDefenseDamage;
                
                final CombatResult result = combatBuilder.setCurrentTime(currentTime).setAttackerId(defender.getId())
                        .setAttacker(Combatant.DEFENDER).setDefender(Combatant.ATTACKER1).build();
                defenderState.applyAttack(result, timeToNextDefenseDamage);
                int energyGain = attackerState.applyDefense(result, timeToNextDefenseDamage);
                log.debug("D{}: {} took {} damage and gained {} energy", currentTime - Formulas.START_COMBAT_TIME, attackerState.getPokemon(), 
                        result.getDamage(), energyGain);
                // log.debug("Defender State {}",defenderState);
                if (includeDetails) {
                	fightResult.addCombatResult(result);
                }
            } else if (timeToNextAttack <= timeToNextDefense) {
                currentTime += timeToNextAttack;
                int energyGain = attackerState.resetAttack(timeToNextAttack);
                log.debug("A{}: {} finished his attack and gained {} energy", currentTime - Formulas.START_COMBAT_TIME, attackerState.getPokemon(), 
                         energyGain);
                defenderState.moveTime(timeToNextAttack);
            } else {
                currentTime += timeToNextDefense;
                int energyGain = defenderState.resetAttack(timeToNextDefense);
                log.debug("D{}: {} finished his attack and gained {} energy", currentTime - Formulas.START_COMBAT_TIME, defenderState.getPokemon(), 
                        energyGain);
                attackerState.moveTime(timeToNextDefense);
            }
                
        }
        int prestige = (defenderState.isAlive())?0:f.defensePrestigeGain(attacker.getCp(), defender.getCp());
        fightResult.setWin(!defenderState.isAlive()).setTotalCombatTime(currentTime).setPrestige(prestige)
                .addCombatants(attackerState.toResult(Combatant.ATTACKER1, attackerStrategy.getType(), currentTime))
                .addCombatants(defenderState.toResult(Combatant.DEFENDER, defenderStrategy.getType(), currentTime))
                .setFightParameters(fight);
        fightResult.setPowerLog(getPower(fightResult));
        fightResult.setPower(Math.pow(10, fightResult.getPowerLog()));
        return fightResult;
    }
    double getPower(FightResult.Builder result) {
        CombatantResultOrBuilder attacker = result.getCombatantsOrBuilder(0);
        CombatantResultOrBuilder defender = result.getCombatantsOrBuilder(1);
        double attackerPower =  Math.min(MAX_POWER, (attacker.getStartHp() - attacker.getEndHp()) / (double) attacker.getStartHp());
        double defenderPower =  Math.min(MAX_POWER, (defender.getStartHp() - defender.getEndHp()) / (double) defender.getStartHp());
        // if we return a log, we can add and the numbers stay much smaller!
        if (attackerPower == 0.0) {
            // attacker takes no damage
            return MAX_POWER;
        } else if (defenderPower == 0.0) {
            // defender takes no damage
            return MIN_POWER;
        } else {
            return  Math.max(MIN_POWER, Math.min(MAX_POWER,Math.log10(defenderPower/attackerPower)));
        }
    }

}

package com.pokebattler.fight.calculator;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pokebattler.fight.data.CpMRepository;
import com.pokebattler.fight.data.MoveRepository;
import com.pokebattler.fight.data.PokemonDataCreator;
import com.pokebattler.fight.data.PokemonRepository;
import com.pokebattler.fight.data.proto.FightOuterClass.*;
import com.pokebattler.fight.data.proto.MoveOuterClass.Move;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;
import com.pokebattler.fight.data.proto.PokemonIdOuterClass.PokemonId;
import com.pokebattler.fight.data.proto.PokemonMoveOuterClass.PokemonMove;
import com.pokebattler.fight.data.proto.PokemonOuterClass.Pokemon;
import com.pokebattler.fight.strategies.AttackStrategy;
import com.pokebattler.fight.strategies.AttackStrategyRegistry;
import com.pokebattler.fight.strategies.AttackStrategy.PokemonAttack;

@Service
public class AttackSimulator {
    @Resource
    Formulas f;
    @Resource
    PokemonRepository pokemonRepository;
    @Resource
    MoveRepository moveRepository;
    @Resource
    CpMRepository cpmRepository;
    @Resource
    AttackStrategyRegistry attackStrategies;
    @Resource
    PokemonDataCreator creator;
    
    public static int DEFENDER_DELAY = 2000;
    // todo make this configurable
    public static int attackerReactionTime = 0;
    public Logger log = LoggerFactory.getLogger(getClass());

    public FightResult calculateMaxAttackDPS(PokemonId attackerId, PokemonId defenderId, PokemonMove move1,
            PokemonMove move2, AttackStrategyType strategy) {
        String level = Integer.toString(Formulas.MAX_LEVEL);
        return calculateMaxAttackDPS(attackerId, defenderId, move1, move2, strategy, level, level);

    }

    public FightResult calculateMaxAttackDPS(PokemonId attackerId, PokemonId defenderId, PokemonMove move1,
            PokemonMove move2, AttackStrategyType strategy, String attackerLevel, String defenderLevel) {
        PokemonData attacker = creator.createMaxStatPokemon(attackerId, attackerLevel,move1,move2);
        Pokemon d = pokemonRepository.getById(defenderId);
        PokemonData defender = creator.createMaxStatPokemon(defenderId, defenderLevel,d.getQuickMoves(0),d.getCinematicMoves(0));
        return calculateAttackDPS(attacker, defender, strategy);
    }


    

    public FightResult calculateAttackDPS(PokemonData attacker, PokemonData defender, AttackStrategyType attackerStrategy) {
        return calculateAttackDPS(attacker, defender, attackerStrategy, AttackStrategyType.DEFENSE);
    }
    public FightResult calculateAttackDPS(PokemonData attacker, PokemonData defender, AttackStrategyType attackerStrategy,
            AttackStrategyType defenseStrategy) {
        return fight(Fight.newBuilder().setAttacker1(attacker).setDefender(defender).setStrategy(attackerStrategy)
                .setDefenseStrategy(defenseStrategy).build());
    }
    private void nextAttack(AttackStrategy strategy, CombatantState attackerState, CombatantState defenderState) {
        PokemonAttack nextAttack = strategy.nextAttack(attackerState, defenderState);
        Move nextMove = moveRepository.getById(nextAttack.getMove());
        attackerState.setNextAttack(nextAttack, nextMove);
    }
    public FightResult fight(Fight fight) {
        PokemonData attacker = fight.getAttacker1();
        PokemonData defender = fight.getDefender();
        AttackStrategy attackerStrategy = attackStrategies.create(fight.getStrategy(), attacker, 0);
        AttackStrategy defenderStrategy = attackStrategies.create(fight.getDefenseStrategy(), defender,DEFENDER_DELAY);
        Pokemon a = pokemonRepository.getById(attacker.getPokemonId());
        Pokemon d = pokemonRepository.getById(defender.getPokemonId());
        FightResult.Builder fightResult = FightResult.newBuilder();
        CombatantState attackerState = new CombatantState(a, attacker, f, false);
        CombatantState defenderState = new CombatantState(d, defender, f, true);
        int currentTime = 0;
        
        while (attackerState.isAlive() && defenderState.isAlive() && currentTime < Formulas.MAX_COMBAT_TIME_MS) {
            // do defender first since defender strategy determines attacker strategy
            if (defenderState.getNextMove() == null) {
                nextAttack(defenderStrategy,defenderState, attackerState);
            }
            if (attackerState.getNextMove() == null) {
                nextAttack(attackerStrategy,attackerState, defenderState);
            }
            int timeToNextAttack = attackerState.getTimeToNextAttack();
            int timeToNextDefense = defenderState.getTimeToNextAttack();
            // tie goes to defender
            if (timeToNextAttack < timeToNextDefense) {
                CombatResult.Builder combatBuilder = f.attackerCombat(attackerState.getAttack(),
                        defenderState.getDefense(), attackerState.getNextMove(), a, d, timeToNextAttack);
                currentTime += timeToNextAttack;
                CombatResult result = combatBuilder.setCurrentTime(currentTime).setAttackerId(attacker.getId()).setAttacker(Combatant.ATTACKER1)
                        .setDefender(Combatant.DEFENDER).setCurrentTime(currentTime).build();
                
                attackerState.applyAttack(result, timeToNextAttack);
                defenderState.applyDefense(result, timeToNextAttack );
                fightResult.addCombatResult(result);
                
            } else {
                CombatResult.Builder combatBuilder = f.defenderCombat(defenderState.getAttack(),
                        defenderState.getDefense(), defenderState.getNextMove(), d, a, timeToNextDefense, attackerState.getNextMove() == MoveRepository.DODGE_MOVE || defenderState.isDodged());
                currentTime += timeToNextDefense;
                CombatResult result = combatBuilder.setCurrentTime(currentTime).setAttackerId(defender.getId()).setAttacker(Combatant.DEFENDER)
                        .setDefender(Combatant.ATTACKER1).build();
                defenderState.applyAttack(result, timeToNextDefense);
                attackerState.applyDefense(result, timeToNextDefense);
//                log.debug("Defender State {}",defenderState);
                fightResult.addCombatResult(result);
                
            }
        }
        return fightResult.setWin(!defenderState.isAlive()).setTotalCombatTime(currentTime)
                .addCombatants(attackerState.toResult(Combatant.ATTACKER1, attackerStrategy.getType(), currentTime))
                .addCombatants(defenderState.toResult(Combatant.DEFENDER, defenderStrategy.getType(), currentTime))
                .setFightParameters(fight)
                .build();
    }

}

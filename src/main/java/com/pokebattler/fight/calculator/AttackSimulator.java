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
import com.pokebattler.fight.data.proto.FightOuterClass.Fight;
import com.pokebattler.fight.data.proto.FightOuterClass.FightResult;
import com.pokebattler.fight.data.proto.MoveOuterClass.Move;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;
import com.pokebattler.fight.data.proto.PokemonIdOuterClass.PokemonId;
import com.pokebattler.fight.data.proto.PokemonMoveOuterClass.PokemonMove;
import com.pokebattler.fight.data.proto.PokemonOuterClass.Pokemon;
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

    public static int DEFENDER_DELAY = 2000;
    // todo make this configurable
    public static int attackerReactionTime = 0;
    public Logger log = LoggerFactory.getLogger(getClass());

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
                .setDefenseStrategy(defenseStrategy).build()).build();
    }

    private void nextAttack(AttackStrategy strategy, CombatantState attackerState, CombatantState defenderState) {
        final PokemonAttack nextAttack = strategy.nextAttack(attackerState, defenderState);
        final Move nextMove = moveRepository.getById(nextAttack.getMove());
        attackerState.setNextAttack(nextAttack, nextMove);
    }

    public FightResult.Builder fight(Fight fight) {
        final PokemonData attacker = fight.getAttacker1();
        final PokemonData defender = fight.getDefender();
        final AttackStrategy attackerStrategy = attackStrategies.create(fight.getStrategy(), attacker, 0);
        final AttackStrategy defenderStrategy = attackStrategies.create(fight.getDefenseStrategy(), defender,
                DEFENDER_DELAY);
        final Pokemon a = pokemonRepository.getById(attacker.getPokemonId());
        final Pokemon d = pokemonRepository.getById(defender.getPokemonId());
        final FightResult.Builder fightResult = FightResult.newBuilder();
        final CombatantState attackerState = new CombatantState(a, attacker, f, false);
        final CombatantState defenderState = new CombatantState(d, defender, f, true);
        int currentTime = 0;

        while (attackerState.isAlive() && defenderState.isAlive() && currentTime < Formulas.MAX_COMBAT_TIME_MS) {
            // do defender first since defender strategy determines attacker
            // strategy
            if (defenderState.getNextMove() == null) {
                nextAttack(defenderStrategy, defenderState, attackerState);
            }
            if (attackerState.getNextMove() == null) {
                nextAttack(attackerStrategy, attackerState, defenderState);
            }
            final int timeToNextAttack = attackerState.getTimeToNextAttack();
            final int timeToNextDefense = defenderState.getTimeToNextAttack();
            // tie goes to defender
            if (timeToNextAttack < timeToNextDefense) {
                final CombatResult.Builder combatBuilder = f.attackerCombat(attackerState.getAttack(),
                        defenderState.getDefense(), attackerState.getNextMove(), a, d, timeToNextAttack);
                currentTime += timeToNextAttack;
                final CombatResult result = combatBuilder.setCurrentTime(currentTime).setAttackerId(attacker.getId())
                        .setAttacker(Combatant.ATTACKER1).setDefender(Combatant.DEFENDER).setCurrentTime(currentTime)
                        .build();

                attackerState.applyAttack(result, timeToNextAttack);
                defenderState.applyDefense(result, timeToNextAttack);
                fightResult.addCombatResult(result);

            } else {
                final CombatResult.Builder combatBuilder = f.defenderCombat(defenderState.getAttack(),
                        defenderState.getDefense(), defenderState.getNextMove(), d, a, timeToNextDefense,
                        attackerState.getNextMove() == MoveRepository.DODGE_MOVE || defenderState.isDodged());
                currentTime += timeToNextDefense;
                final CombatResult result = combatBuilder.setCurrentTime(currentTime).setAttackerId(defender.getId())
                        .setAttacker(Combatant.DEFENDER).setDefender(Combatant.ATTACKER1).build();
                defenderState.applyAttack(result, timeToNextDefense);
                attackerState.applyDefense(result, timeToNextDefense);
                // log.debug("Defender State {}",defenderState);
                fightResult.addCombatResult(result);

            }
        }
        return fightResult.setWin(!defenderState.isAlive()).setTotalCombatTime(currentTime)
                .addCombatants(attackerState.toResult(Combatant.ATTACKER1, attackerStrategy.getType(), currentTime))
                .addCombatants(defenderState.toResult(Combatant.DEFENDER, defenderStrategy.getType(), currentTime))
                .setFightParameters(fight);
    }

}

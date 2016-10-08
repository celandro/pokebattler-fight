package com.pokebattler.fight.calculator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pokebattler.fight.data.MoveRepository;
import com.pokebattler.fight.data.PokemonDataCreator;
import com.pokebattler.fight.data.PokemonRepository;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackerResult;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackerSubResult;
import com.pokebattler.fight.data.proto.FightOuterClass.DefenderResult;
import com.pokebattler.fight.data.proto.FightOuterClass.DefenderSubResult;
import com.pokebattler.fight.data.proto.FightOuterClass.FightResult;
import com.pokebattler.fight.data.proto.FightOuterClass.RankingResult;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;
import com.pokebattler.fight.data.proto.PokemonIdOuterClass.PokemonId;
import com.pokebattler.fight.data.proto.PokemonOuterClass.Pokemon;

@Service
public class RankingSimulator {
    @Resource
    AttackSimulator simulator;
    @Resource
    PokemonRepository pokemonRepository;
    @Resource
    MoveRepository moveRepository;
    @Resource
    PokemonDataCreator creator;

    Logger log = LoggerFactory.getLogger(getClass());

    public RankingResult rank(String level, AttackStrategyType attackStrategy, AttackStrategyType defenseStrategy) {
        RankingResult.Builder retval = RankingResult.newBuilder().setAttackStrategy(attackStrategy)
                .setDefenseStrategy(defenseStrategy);
        pokemonRepository.getAll().getPokemonList().stream().forEach((attacker) -> {
            retval.addAttackers(rankAttacker(attacker, level, attackStrategy, defenseStrategy));
            log.info(attacker.getPokemonId() + " Ranked");
        });
        return retval.build();
    }

    public AttackerResult rankAttacker(Pokemon attacker, String level, AttackStrategyType attackStrategy,
            AttackStrategyType defenseStrategy) {
        AttackerResult.Builder retval = AttackerResult.newBuilder().setPokemonId(attacker.getPokemonId());
        attacker.getQuickMovesList().forEach((quick) -> {
            attacker.getCinematicMovesList().forEach((cin) -> {
                PokemonData attackerData = creator.createMaxStatPokemon(attacker.getPokemonId(), level, quick, cin);
                retval.addByMove(rankAttackerByMoves(attackerData, attackStrategy, defenseStrategy));
            });
        });
        return retval.build();
    }

    public AttackerSubResult rankAttackerByMoves(PokemonData attackerData, AttackStrategyType attackStrategy,
            AttackStrategyType defenseStrategy) {
        AttackerSubResult.Builder retval = AttackerSubResult.newBuilder().setMove1(attackerData.getMove1())
                .setMove2(attackerData.getMove2());
        ArrayList<DefenderResult.Builder> results = new ArrayList<>();
        pokemonRepository.getAll().getPokemonList().stream().forEach((defender) -> {
            DefenderResult.Builder rankDefender = rankDefender(defender, attackerData, attackStrategy, defenseStrategy);
            // only retain the best defense
            DefenderSubResult bestDefense = rankDefender.getByMoveList().get(rankDefender.getByMoveList().size() - 1);
            rankDefender.clearByMove().addByMove(bestDefense);
            results.add(rankDefender);
        });
        //TODO:  sort defenders by win first?
        results.stream()
        .sorted(Comparator.comparing(result -> result.getByMove(0).getResult().getCombatants(1).getDamageDealt()))
        .forEach((result) -> retval.addDefenders
                
                (result));

        return retval.build();
    }

    public DefenderResult.Builder rankDefender(Pokemon defender, PokemonData attackerData,
            AttackStrategyType attackStrategy, AttackStrategyType defenseStrategy) {
        DefenderResult.Builder retval = DefenderResult.newBuilder().setPokemonId(defender.getPokemonId());
        ArrayList<DefenderSubResult.Builder> results = new ArrayList<>();
        defender.getQuickMovesList().forEach((quick) -> {
            defender.getCinematicMovesList().forEach((cin) -> {
                PokemonData defenderData = creator.createMaxStatPokemon(defender.getPokemonId(),
                        attackerData.getLevel(), quick, cin);
                results.add(rankDefenderByMoves(attackerData, defenderData, attackStrategy, defenseStrategy));
            });
        });
        results.stream()
        .sorted( Comparator.comparing(result1 -> result1.getResult().getCombatants(1).getDamageDealt()))
                .forEach((result) -> retval.addByMove(result));

        return retval;
    }

    public DefenderSubResult.Builder rankDefenderByMoves(PokemonData attackerData, PokemonData defenderData,
            AttackStrategyType attackStrategy, AttackStrategyType defenseStrategy) {
        FightResult.Builder result = simulator
                .calculateAttackDPS(attackerData, defenderData, attackStrategy, defenseStrategy).toBuilder();
        result.clearCombatResult().clearFightParameters();
        result.getCombatantsBuilder(0).clearStrategy().clearCombatTime().clearPokemon().clearStartHp().clearEndHp()
                .clearEnergy().clearCombatant().clearDps();
        result.getCombatantsBuilder(1).clearStrategy().clearCombatTime().clearPokemon().clearStartHp().clearEndHp()
                .clearEnergy().clearCombatant().clearDps();
        return DefenderSubResult.newBuilder().setMove1(defenderData.getMove1()).setMove2(defenderData.getMove2())
                .setResult(result);
    }

}

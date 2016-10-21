package com.pokebattler.fight.calculator;

import static com.pokebattler.fight.data.proto.PokemonIdOuterClass.PokemonId.DRAGONITE;
import static com.pokebattler.fight.data.proto.PokemonIdOuterClass.PokemonId.LAPRAS;
import static com.pokebattler.fight.data.proto.PokemonIdOuterClass.PokemonId.SNORLAX;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pokebattler.fight.data.MoveRepository;
import com.pokebattler.fight.data.PokemonDataCreator;
import com.pokebattler.fight.data.PokemonRepository;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.FightOuterClass.Fight;
import com.pokebattler.fight.data.proto.FightOuterClass.FightResult;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;
import com.pokebattler.fight.data.proto.PokemonIdOuterClass.PokemonId;
import com.pokebattler.fight.data.proto.PokemonOuterClass.Pokemon;
import com.pokebattler.fight.data.proto.Ranking.AttackerResult;
import com.pokebattler.fight.data.proto.Ranking.AttackerResultOrBuilder;
import com.pokebattler.fight.data.proto.Ranking.AttackerSubResult;
import com.pokebattler.fight.data.proto.Ranking.AttackerSubResultOrBuilder;
import com.pokebattler.fight.data.proto.Ranking.DefenderResult;
import com.pokebattler.fight.data.proto.Ranking.DefenderResultOrBuilder;
import com.pokebattler.fight.data.proto.Ranking.DefenderSubResult;
import com.pokebattler.fight.data.proto.Ranking.DefenderSubResultOrBuilder;
import com.pokebattler.fight.data.proto.Ranking.RankingResult;
import com.pokebattler.fight.data.proto.Ranking.SubResultTotal;

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

    public RankingResult rank(String attackerLevel, String defenderLevel, AttackStrategyType attackStrategy, AttackStrategyType defenseStrategy) {
        final RankingResult.Builder retval = RankingResult.newBuilder().setAttackStrategy(attackStrategy)
                .setDefenseStrategy(defenseStrategy);
        final ArrayList<AttackerResult.Builder> results = new ArrayList<>();
        pokemonRepository.getAllEndGame().getPokemonList().stream().forEach((attacker) -> {
            results.add(rankAttacker(attacker, attackerLevel, defenderLevel, attackStrategy, defenseStrategy));
            log.debug("{} Ranked for {} {} {} {}",attacker.getPokemonId(), attackerLevel, defenderLevel, attackStrategy.name(), defenseStrategy.name());
        });
        //TODO reuse comparators
        results.stream()
        .sorted(Comparator.comparing(result -> -((AttackerResultOrBuilder) result).getByMove(0).getTotalOrBuilder().getNumWins())
                .thenComparing(Comparator.comparing(result -> ((AttackerResultOrBuilder) result).getByMove(0).getTotalOrBuilder().getDamageTaken())
                .thenComparing(Comparator.comparing(result -> ((AttackerResultOrBuilder) result).getByMove(0).getTotalOrBuilder().getDamageDealt()))))
                .forEach((result) -> retval.addAttackers(result));        
        return retval.build();
    }

    public AttackerResult.Builder rankAttacker(Pokemon attacker, String attackerLevel, String defenderLevel, AttackStrategyType attackStrategy,
            AttackStrategyType defenseStrategy) {
        final ArrayList<AttackerSubResult.Builder> results = new ArrayList<>();
        final AttackerResult.Builder retval = AttackerResult.newBuilder().setPokemonId(attacker.getPokemonId());
        attacker.getQuickMovesList().forEach((quick) -> {
            attacker.getCinematicMovesList().forEach((cin) -> {
                final PokemonData attackerData = creator.createMaxStatPokemon(attacker.getPokemonId(), attackerLevel, quick,
                        cin);
                results.add(rankAttackerByMoves(attackerData, defenderLevel, attackStrategy, defenseStrategy));
            });
        });
        results.stream()
        .sorted(Comparator.comparing(result -> -((AttackerSubResultOrBuilder) result).getTotalOrBuilder().getNumWins())
                .thenComparing(Comparator.comparing(result -> ((AttackerSubResultOrBuilder) result).getTotalOrBuilder().getDamageTaken())
                .thenComparing(Comparator.comparing(result -> ((AttackerSubResultOrBuilder) result).getTotalOrBuilder().getDamageDealt()))))
        .forEach(result -> {
            // defender list is too big
            result.clearDefenders();
            retval.addByMove(result);
        });;
                
        
        return retval;
    }

    public AttackerSubResult.Builder rankAttackerByMoves(PokemonData attackerData, String defenderLevel, AttackStrategyType attackStrategy,
            AttackStrategyType defenseStrategy) {
        final AttackerSubResult.Builder retval = AttackerSubResult.newBuilder().setMove1(attackerData.getMove1())
                .setMove2(attackerData.getMove2());
        final ArrayList<DefenderResult.Builder> results = new ArrayList<>();
        
        
//        pokemonRepository.getAllEndGame().getPokemonList().stream().forEach((defender) -> {
        pokemonRepository.getAllEndGameDefender().getPokemonList().stream().forEach((defender) -> {
            final DefenderResult.Builder rankDefender = subRankDefender(defender, attackerData, defenderLevel, attackStrategy,
                    defenseStrategy);
            // only retain the subtotals
            final DefenderSubResult bestDefense = rankDefender.getByMoveList()
                    .get(rankDefender.getByMoveList().size() - 1);
            rankDefender.clearByMove();
            results.add(rankDefender);
        });
        // sort by winner first then damagetaken then damage dealt for tie breaker (unlikely)
        SubResultTotal.Builder subTotal = SubResultTotal.newBuilder();
        results.stream()
            .sorted(Comparator.comparing(result -> -((DefenderResultOrBuilder) result).getTotalOrBuilder().getNumWins())
                    .thenComparing(Comparator.comparing(result -> ((DefenderResultOrBuilder) result).getTotalOrBuilder().getDamageTaken())
                    .thenComparing(Comparator.comparing(result -> ((DefenderResultOrBuilder) result).getTotalOrBuilder().getDamageDealt()))))
                .forEach((result) -> {
                    subTotal.setNumWins(subTotal.getNumWins() + result.getTotalOrBuilder().getNumWins());
                    subTotal.setCombatTime(subTotal.getCombatTime() + result.getTotalOrBuilder().getCombatTime());
                    subTotal.setDamageDealt(subTotal.getDamageDealt() + result.getTotalOrBuilder().getDamageDealt());
                    subTotal.setDamageTaken(subTotal.getDamageTaken() + result.getTotalOrBuilder().getDamageTaken());
                    subTotal.setNumLosses(subTotal.getNumLosses() + result.getTotalOrBuilder().getNumLosses());
                    retval.addDefenders(result);
                    
                });
        retval.setTotal(subTotal );
        return retval;
    }

    public DefenderResult.Builder subRankDefender(Pokemon defender, PokemonData attackerData, String defenderLevel,
            AttackStrategyType attackStrategy, AttackStrategyType defenseStrategy) {
        final DefenderResult.Builder retval = DefenderResult.newBuilder().setPokemonId(defender.getPokemonId());
        final ArrayList<DefenderSubResult.Builder> results = new ArrayList<>();
        defender.getQuickMovesList().forEach((quick) -> {
            defender.getCinematicMovesList().forEach((cin) -> {
                final PokemonData defenderData = creator.createMaxStatPokemon(defender.getPokemonId(),
                        defenderLevel, quick, cin);
                results.add(subRankDefenderByMoves(attackerData, defenderData, attackStrategy, defenseStrategy));
            });
        });
        
        SubResultTotal.Builder subTotal = SubResultTotal.newBuilder();
        results.stream().sorted(Comparator.comparing(result -> !((DefenderSubResultOrBuilder) result).getResultOrBuilder().getWin()).
                thenComparing(Comparator.comparing(result -> 
                ((DefenderSubResultOrBuilder) result).getResultOrBuilder().getWin()?
                    -((DefenderSubResultOrBuilder) result).getResultOrBuilder().getCombatantsOrBuilder(1).getDamageDealt():
                    ((DefenderSubResultOrBuilder) result).getResultOrBuilder().getCombatantsOrBuilder(0).getDamageDealt())
                ))
                .forEach((result) -> {
                    boolean win =result.getResultOrBuilder().getWin();
                    subTotal.setNumWins(subTotal.getNumWins() + (win?1:0));
                    subTotal.setNumLosses(subTotal.getNumLosses() + (win?0:1));
                    subTotal.setCombatTime(subTotal.getCombatTime() + result.getResultOrBuilder().getTotalCombatTime());
                    subTotal.setDamageDealt(subTotal.getDamageDealt() + result.getResultOrBuilder().getCombatantsOrBuilder(0).getDamageDealt());
                    subTotal.setDamageTaken(subTotal.getDamageTaken() + result.getResultOrBuilder().getCombatantsOrBuilder(1).getDamageDealt());
                    retval.addByMove(result);   
                });
        retval.setTotal(subTotal);
        return retval;
    }

    public DefenderSubResult.Builder subRankDefenderByMoves(PokemonData attackerData, PokemonData defenderData,
            AttackStrategyType attackStrategy, AttackStrategyType defenseStrategy) {
        Fight fight = Fight.newBuilder().setAttacker1(attackerData).setDefender(defenderData).setStrategy(attackStrategy)
                .setDefenseStrategy(defenseStrategy).build();      
        final FightResult.Builder result = simulator.fight(fight);
        result.clearCombatResult().clearFightParameters();
        result.getCombatantsBuilder(0).clearStrategy().clearCombatTime().clearPokemon().clearStartHp().clearEndHp()
                .clearEnergy().clearCombatant().clearDps();
        result.getCombatantsBuilder(1).clearStrategy().clearCombatTime().clearPokemon().clearStartHp().clearEndHp()
                .clearEnergy().clearCombatant().clearDps();
        return DefenderSubResult.newBuilder().setMove1(defenderData.getMove1()).setMove2(defenderData.getMove2())
                .setResult(result);
    }

}

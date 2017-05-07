package com.pokebattler.fight.strategies;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.pokebattler.fight.calculator.AttackDamage;
import com.pokebattler.fight.calculator.dodge.DodgeStrategy;
import com.pokebattler.fight.data.proto.FightOuterClass.AttackStrategyType;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;
import com.pokebattler.fight.strategies.CinematicAttackWhenPossible.CinematicAttackWhenPossibleBuilder;
import com.pokebattler.fight.strategies.DefenderAttack.DefenderAttackBuilder;
import com.pokebattler.fight.strategies.DefenderRandomAttack.DefenderLuckyAttackBuilder;
import com.pokebattler.fight.strategies.DefenderRandomAttack.DefenderRandomAttackBuilder;
import com.pokebattler.fight.strategies.DodgeAll.DodgeAllBuilder;
import com.pokebattler.fight.strategies.DodgeAll2.DodgeAll2Builder;
import com.pokebattler.fight.strategies.DodgeSpecials.DodgeSpecialsBuilder;
import com.pokebattler.fight.strategies.DodgeSpecials2.DodgeSpecials2Builder;
import com.pokebattler.fight.strategies.NoAttack.NoAttackBuilder;
import com.pokebattler.fight.strategies.QuickAttackDodgeAll.QuickAttackDodgeAllBuilder;
import com.pokebattler.fight.strategies.QuickAttackDodgeSpecials.QuickAttackDodgeSpecialsBuilder;
import com.pokebattler.fight.strategies.QuickAttackOnly.QuickAttackOnlyBuilder;
import com.pokebattler.fight.strategies.DodgeWeave.DodgeWeaveCautiousBuilder;
import com.pokebattler.fight.strategies.DodgeWeave.DodgeWeaveReasonableBuilder;
import com.pokebattler.fight.strategies.DodgeWeave.DodgeWeaveRiskyBuilder;
import com.pokebattler.fight.strategies.DodgeWeave.DodgeWeaveSpecialsBuilder;
import com.pokebattler.fight.strategies.DodgeWeave.DodgeWeaveHumanBuilder;
import com.pokebattler.fight.strategies.DodgeWeave.DodgeWeaveSpecialsHumanBuilder;

@Repository
public class AttackStrategyRegistry {
    private Map<AttackStrategyType, AttackStrategy.AttackStrategyBuilder<?>> strategies;
    @Resource
    List<AttackStrategy.AttackStrategyBuilder<?>> builders;
//    @Resource
//    QuickAttackOnlyBuilder strategy1;
//    @Resource
//    NoAttackBuilder strategy2;
//    @Resource
//    DefenderAttackBuilder strategy3;
//    @Resource
//    CinematicAttackWhenPossibleBuilder strategy4;
//    @Resource
//    DefenderRandomAttackBuilder strategy5;
//    @Resource
//    DefenderLuckyAttackBuilder strategy6;
//    @Resource
//    DodgeSpecialsBuilder strategy7;
//    @Resource
//    DodgeSpecials2Builder strategy8;
//    @Resource
//    DodgeAllBuilder strategy9;
//    @Resource
//    DodgeAll2Builder strategy10;
//    @Resource
//    QuickAttackDodgeAllBuilder strategy11;
//    @Resource
//    QuickAttackDodgeSpecialsBuilder strategy12;
//    @Resource
//    DodgeWeaveCautiousBuilder strategy13;
//    @Resource
//    DodgeWeaveSpecialsBuilder strategy14;
//    @Resource
//    DodgeWeaveHumanBuilder strategy15;
//    @Resource
//    DodgeWeaveSpecialsHumanBuilder strategy16;

    @PostConstruct
    public void init() {
        strategies = new EnumMap<>(AttackStrategyType.class);
//        register(strategy1);
//        register(strategy2);
//        register(strategy3);
//        register(strategy4);
//        register(strategy5);
//        register(strategy6);
//        register(strategy7);
//        register(strategy8);
//        register(strategy9);
//        register(strategy10);
//        register(strategy11);
//        register(strategy12);
//        register(strategy13);
//        register(strategy14);
//        register(strategy15);
//        register(strategy16);
        builders.stream().forEach(builder -> register(builder));
        strategies.put(AttackStrategyType.DEFENSE_RANDOM_MC, strategies.get(AttackStrategyType.DEFENSE_RANDOM));
    }

    public boolean register(AttackStrategy.AttackStrategyBuilder<?> strategyBuilder) {
        return strategies.put(strategyBuilder.getType(), strategyBuilder) != null;
    }

    public AttackStrategy create(AttackStrategyType name, PokemonData pokemon, DodgeStrategy dodgeStrategy, AttackDamage attackDamage, AttackDamage attackDamage2, Random r) {
        if (!strategies.containsKey(name)) {
            throw new IllegalArgumentException("Unimplemented attack strategy " + name);
        }
        return strategies.get(name).build(pokemon, dodgeStrategy, attackDamage, attackDamage2, r);
    }

}

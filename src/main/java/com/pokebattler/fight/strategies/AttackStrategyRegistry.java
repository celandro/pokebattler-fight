package com.pokebattler.fight.strategies;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

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

@Repository
public class AttackStrategyRegistry {
    private Map<AttackStrategyType, AttackStrategy.AttackStrategyBuilder<?>> strategies;
    @Resource
    QuickAttackOnlyBuilder strategy1;
    @Resource
    NoAttackBuilder strategy2;
    @Resource
    DefenderAttackBuilder strategy3;
    @Resource
    CinematicAttackWhenPossibleBuilder strategy4;
    @Resource
    DefenderRandomAttackBuilder strategy5;
    @Resource
    DefenderLuckyAttackBuilder strategy6;
    @Resource
    DodgeSpecialsBuilder strategy7;
    @Resource
    DodgeSpecials2Builder strategy8;
    @Resource
    DodgeAllBuilder strategy9;
    @Resource
    DodgeAll2Builder strategy10;
    @Resource
    QuickAttackDodgeAllBuilder strategy11;
    @Resource
    QuickAttackDodgeSpecialsBuilder strategy12;

    @PostConstruct
    public void init() {
        strategies = new EnumMap<>(AttackStrategyType.class);
        register(strategy1);
        register(strategy2);
        register(strategy3);
        register(strategy4);
        register(strategy5);
        register(strategy6);
        register(strategy7);
        register(strategy8);
        register(strategy9);
        register(strategy10);
        register(strategy11);
        register(strategy12);
    }

    public boolean register(AttackStrategy.AttackStrategyBuilder<?> strategyBuilder) {
        return strategies.put(strategyBuilder.getType(), strategyBuilder) != null;
    }

    public AttackStrategy create(AttackStrategyType name, PokemonData pokemon) {
        if (!strategies.containsKey(name)) {
            throw new IllegalArgumentException("Unimplemented attack strategy " + name);
        }
        return strategies.get(name).build(pokemon);
    }

}

package com.pokebattler.fight.strategies;

import static com.pokebattler.fight.strategies.NoAttack.NoAttackBuilder;
import static com.pokebattler.fight.strategies.QuickAttackOnly.QuickAttackOnlyBuilder;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.pokebattler.fight.calculator.CinematicAttackWhenPossible;
import com.pokebattler.fight.calculator.DefenderRandomAttack;
import com.pokebattler.fight.calculator.CinematicAttackWhenPossible.CinematicAttackWhenPossibleBuilder;
import com.pokebattler.fight.calculator.DefenderRandomAttack.DefenderLuckyAttackBuilder;
import com.pokebattler.fight.calculator.DefenderRandomAttack.DefenderRandomAttackBuilder;
import com.pokebattler.fight.data.proto.FightOuterClass.*;
import com.pokebattler.fight.data.proto.PokemonDataOuterClass.PokemonData;
import com.pokebattler.fight.strategies.DefenderAttack.DefenderAttackBuilder;
import com.pokebattler.fight.strategies.DodgeSpecials.DodgeSpecialsBuilder;

@Repository
public class AttackStrategyRegistry {
    private Map<AttackStrategyType,AttackStrategy.AttackStrategyBuilder<?>> strategies;
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
    }
    
    public boolean register(AttackStrategy.AttackStrategyBuilder<?> strategyBuilder) {
        return strategies.put(strategyBuilder.getType(),strategyBuilder) != null;
    }
    public AttackStrategy create(AttackStrategyType name, PokemonData pokemon, int extraDelay) {
        if (!strategies.containsKey(name)) throw new IllegalArgumentException("Unimplemented attack strategy " + name);
        return strategies.get(name).build(pokemon, extraDelay);
    }
    
    

}
